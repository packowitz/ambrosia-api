package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.fights.Fight
import io.pacworx.ambrosia.hero.base.HeroBase
import io.pacworx.ambrosia.hero.base.HeroBaseRepository
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class HeroService(val heroBaseRepository: HeroBaseRepository,
                  val heroRepository: HeroRepository,
                  val propertyService: PropertyService) {

    fun asHeroDto(hero: Hero): HeroDto {
        val heroDto = HeroDto(hero)
        propertyService.applyBonuses(heroDto)
        return heroDto
    }

    fun getHeroDto(heroId: Long): HeroDto {
        return asHeroDto(heroRepository.getOne(heroId))
    }

    fun loadHeroes(heroIds: List<Long>): List<Hero> {
        return heroRepository.findAllById(heroIds.distinct())
    }

    fun recruitHero(player: Player, heroBaseId: Long): HeroDto {
        val heroBase = heroBaseRepository.findByIdOrNull(heroBaseId)
            ?: throw RuntimeException("Unknown base hero #$heroBaseId")
        return asHeroDto(recruitHero(player, heroBase))
    }

    fun recruitHero(player: Player, heroBase: HeroBase): Hero {
        return heroRepository.save(Hero(
            playerId = player.id,
            heroBase = heroBase,
            maxXp = propertyService.getHeroMaxXp(1),
            ascPointsMax = propertyService.getHeroMaxAsc(0)
        ))
    }

    fun recruitHero(player: Player,
                    simpleChance: Double? = null,
                    commonChance: Double? = null,
                    uncommonChance: Double? = null,
                    rareChance: Double? = null,
                    epicChance: Double? = null,
                    default: Rarity): Hero {
        var chance = Random.nextDouble()
        var rarity: Rarity? = null
        epicChance?.let {
            if (chance < it) {
                rarity = Rarity.EPIC
            } else {
                chance -= it
            }
        }
        rareChance?.takeIf { rarity == null }?.let {
            if (chance < it) {
                rarity = Rarity.RARE
            } else {
                chance -= it
            }
        }
        uncommonChance?.takeIf { rarity == null }?.let {
            if (chance < it) {
                rarity = Rarity.UNCOMMON
            } else {
                chance -= it
            }
        }
        commonChance?.takeIf { rarity == null }?.let {
            if (chance < it) {
                rarity = Rarity.COMMON
            }
        }

        return recruitHero(player, heroBaseRepository.findAllByRarityAndRecruitableIsTrue(rarity?.let { it } ?: default).random())
    }

    fun wonFight(player: Player, heroIds: List<Long>, fight: Fight): List<HeroDto> {
        return heroIds.map { heroId ->
            val hero = heroRepository.getOne(heroId)
            if (hero.playerId != player.id) {
                throw RuntimeException("Cannot gain xp for a hero you don't own.")
            }
            heroGainXp(hero, fight.xp)
            when(hero.level) {
                in 1..fight.level -> heroGainAsc(hero, fight.ascPoints)
                fight.level + 1 -> heroGainAsc(hero, fight.ascPoints / 2)
                fight.level + 2 -> heroGainAsc(hero, fight.ascPoints / 4)
                else -> {}
            }
            asHeroDto(hero)
        }
    }

    fun heroGainXp(hero: Hero, xp: Int) {
        hero.xp += xp
        if (hero.xp >= hero.maxXp) {
            val overflow = hero.xp - hero.maxXp
            hero.xp = hero.maxXp
            if (hero.level < hero.stars * 10) {
                hero.level ++
                hero.xp = 0
                hero.maxXp = propertyService.getHeroMaxXp(hero.level)
                heroGainXp(hero, overflow)
            }
        }
    }

    fun evolveHero(hero: Hero): Boolean {
        if (hero.xp == hero.maxXp && hero.level == 10 * hero.stars && hero.level < 60) {
            hero.level ++
            hero.stars ++
            hero.xp = 0
            hero.maxXp = propertyService.getHeroMaxXp(hero.level)
            return true
        }
        return false
    }

    fun heroGainAsc(hero: Hero, asc: Int) {
        hero.ascPoints += asc
        if (hero.ascPoints >= hero.ascPointsMax) {
            val overflow = hero.ascPoints - hero.ascPointsMax
            hero.ascPoints = hero.ascPointsMax
            if (hero.ascLvl < hero.heroBase.maxAscLevel) {
                hero.ascLvl ++
                hero.skillPoints ++
                hero.ascPoints = 0
                hero.ascPointsMax = propertyService.getHeroMaxAsc(hero.ascLvl)
                heroGainAsc(hero, overflow)
            }
        }
    }
}
