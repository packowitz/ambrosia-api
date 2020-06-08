package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.battle.offline.Mission
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.fights.Fight
import io.pacworx.ambrosia.hero.skills.SkillActiveTrigger
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehicleService
import io.pacworx.ambrosia.vehicle.VehicleStat
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.round
import kotlin.random.Random

@Service
class HeroService(val heroBaseRepository: HeroBaseRepository,
                  val heroRepository: HeroRepository,
                  val propertyService: PropertyService,
                  val vehicleService: VehicleService) {

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

    fun getNumberOfHeroes(player: Player): Int = heroRepository.findNumberOfHeroes(player.id)

    fun gainStartingHeroes(player: Player): List<HeroDto> {
        return heroBaseRepository.findAllByStartingHeroIsTrueAndColor(player.color!!).map {
            asHeroDto(recruitHero(player, it))
        }
    }

    fun recruitHero(player: Player, heroBaseId: Long, level: Int): HeroDto {
        val heroBase = heroBaseRepository.findByIdOrNull(heroBaseId)
            ?: throw EntityNotFoundException(player, "heroBase", heroBaseId)
        return asHeroDto(recruitHero(player, heroBase, level))
    }

    fun recruitHero(player: Player, heroBase: HeroBase, level: Int = 1): Hero {
        return heroRepository.save(Hero(
            playerId = player.id,
            heroBase = heroBase,
            level = level,
            maxXp = propertyService.getHeroMaxXp(1),
            ascPointsMax = propertyService.getHeroMaxAsc(0)
        ))
    }

    fun recruitHero(player: Player,
                    commonChance: Double? = null,
                    uncommonChance: Double? = null,
                    rareChance: Double? = null,
                    epicChance: Double? = null,
                    default: Rarity
    ): Hero {
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

    fun wonMission(mission: Mission, vehicle: Vehicle): List<HeroDto> {
        vehicle.missionId = null
        return listOfNotNull(mission.hero1Id, mission.hero2Id, mission.hero3Id, mission.hero4Id)
            .map { heroRepository.getOne(it) }
            .map { hero ->
                if (mission.wonCount > 0) {
                    (1..mission.wonCount).forEach { _ -> gainXpAndAsc(hero, mission.fight, vehicle) }
                }
                hero.missionId = null
                hero
            }.map { asHeroDto(it) }
    }

    fun wonFight(player: Player, heroIds: List<Long>, fight: Fight?, vehicle: Vehicle?): List<HeroDto>? {
        return fight?.let { _ ->
            heroIds.map { heroId ->
                val hero = heroRepository.getOne(heroId)
                if (hero.playerId != player.id) {
                    throw UnauthorizedException(player, "You can only gain xp for heroes you own")
                }
                gainXpAndAsc(hero, fight, vehicle)
                asHeroDto(hero)
            }
        }
    }

    private fun gainXpAndAsc(hero: Hero, fight: Fight, vehicle: Vehicle?) {
        val xp = fight.xp + (fight.xp * vehicleService.getStat(vehicle, VehicleStat.BATTLE_XP) / 100)
        val ascPoints = fight.ascPoints + (fight.ascPoints * vehicleService.getStat(vehicle, VehicleStat.BATTLE_ASC_POINTS) / 100)

        heroGainXp(hero, xp)
        when (hero.level) {
            in 1..fight.level -> heroGainAsc(hero, ascPoints)
            fight.level + 1 -> heroGainAsc(hero, round(ascPoints * 90.0 / 100).toInt())
            fight.level + 2 -> heroGainAsc(hero, round(ascPoints * 80.0 / 100).toInt())
            fight.level + 3 -> heroGainAsc(hero, round(ascPoints * 70.0 / 100).toInt())
            fight.level + 4 -> heroGainAsc(hero, round(ascPoints * 60.0 / 100).toInt())
            fight.level + 5 -> heroGainAsc(hero, round(ascPoints * 50.0 / 100).toInt())
            fight.level + 6 -> heroGainAsc(hero, round(ascPoints * 40.0 / 100).toInt())
            fight.level + 7 -> heroGainAsc(hero, round(ascPoints * 30.0 / 100).toInt())
            fight.level + 8 -> heroGainAsc(hero, round(ascPoints * 20.0 / 100).toInt())
            else -> heroGainAsc(hero, round(ascPoints * 10.0 / 100).toInt())
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
                if (hero.ascLvl == 1) {
                    hero.heroBase.skills.filter { it.skillActiveTrigger == SkillActiveTrigger.ASCENDED }.forEach {
                        hero.enableSkill(it.number)
                    }
                }
                heroGainAsc(hero, overflow)
            }
        }
    }
}
