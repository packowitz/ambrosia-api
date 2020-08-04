package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.battle.offline.Mission
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.expedition.PlayerExpedition
import io.pacworx.ambrosia.fights.Fight
import io.pacworx.ambrosia.hero.skills.SkillActiveTrigger
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehicleService
import io.pacworx.ambrosia.vehicle.VehicleStat
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

@Service
class HeroService(
    val heroBaseRepository: HeroBaseRepository,
    val heroRepository: HeroRepository,
    val propertyService: PropertyService,
    val vehicleService: VehicleService,
    val knownHeroRepository: KnownHeroRepository
) {

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
        if (knownHeroRepository.findByPlayerIdAndHeroBaseId(player.id, heroBase.id) == null) {
            knownHeroRepository.save(KnownHero(playerId = player.id, heroBaseId = heroBase.id))
        }
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

        return recruitHero(player, heroBaseRepository.findAllByRarityAndRecruitableIsTrue(rarity ?: default).random())
    }

    fun wonMission(mission: Mission, progress: Progress, vehicle: Vehicle): List<HeroDto> {
        vehicle.missionId = null
        return listOfNotNull(mission.hero1Id, mission.hero2Id, mission.hero3Id, mission.hero4Id)
            .map { heroRepository.getOne(it) }
            .map { hero ->
                if (mission.wonCount > 0) {
                    (1..mission.wonCount).forEach { _ -> gainXpAndAsc(hero, mission.fight, progress, vehicle) }
                }
                hero.missionId = null
                hero
            }.map { asHeroDto(it) }
    }

    fun finishedExpedition(playerExpedition: PlayerExpedition, vehicle: Vehicle, xp: Int): List<HeroDto> {
        vehicle.playerExpeditionId = null
        return listOfNotNull(playerExpedition.hero1Id, playerExpedition.hero2Id, playerExpedition.hero3Id, playerExpedition.hero4Id)
            .map { heroRepository.getOne(it) }
            .map { hero ->
                heroGainXp(hero, xp)
                hero.playerExpeditionId = null
                hero
            }.map { asHeroDto(it) }
    }

    fun wonFight(player: Player, progress: Progress, heroIds: List<Long>, fight: Fight?, vehicle: Vehicle?): List<HeroDto>? {
        return fight?.let { _ ->
            heroIds.map { heroId ->
                val hero = heroRepository.getOne(heroId)
                if (hero.playerId != player.id) {
                    throw UnauthorizedException(player, "You can only gain xp for heroes you own")
                }
                gainXpAndAsc(hero, fight, progress, vehicle)
                asHeroDto(hero)
            }
        }
    }

    private fun gainXpAndAsc(hero: Hero, fight: Fight, progress: Progress, vehicle: Vehicle?) {
        val xp = fight.xp + (fight.xp * (progress.battleXpBoost + vehicleService.getStat(vehicle, VehicleStat.BATTLE_XP)) / 100)
        val ascPoints = fight.ascPoints + (fight.ascPoints * vehicleService.getStat(vehicle, VehicleStat.BATTLE_ASC_POINTS) / 100)

        heroGainXp(hero, xp)
        when (abs(hero.level - fight.level)) {
            in 0..1 -> heroGainAsc(hero, ascPoints)
            2 -> heroGainAsc(hero, round(ascPoints * 90.0 / 100).toInt())
            3 -> heroGainAsc(hero, round(ascPoints * 80.0 / 100).toInt())
            4 -> heroGainAsc(hero, round(ascPoints * 70.0 / 100).toInt())
            5 -> heroGainAsc(hero, round(ascPoints * 60.0 / 100).toInt())
            6 -> heroGainAsc(hero, round(ascPoints * 50.0 / 100).toInt())
            7 -> heroGainAsc(hero, round(ascPoints * 40.0 / 100).toInt())
            8 -> heroGainAsc(hero, round(ascPoints * 30.0 / 100).toInt())
            9 -> heroGainAsc(hero, round(ascPoints * 20.0 / 100).toInt())
            else -> heroGainAsc(hero, round(ascPoints * 10.0 / 100).toInt())
        }
    }

    fun heroGainXp(hero: Hero, xp: Int): Int {
        val gapToMax = hero.maxXp - hero.xp
        hero.xp += xp
        if (hero.xp >= hero.maxXp) {
            val overflow = hero.xp - hero.maxXp
            hero.xp = hero.maxXp
            if (hero.level < hero.stars * 10) {
                hero.level ++
                hero.xp = 0
                hero.maxXp = propertyService.getHeroMaxXp(hero.level)
                return gapToMax + heroGainXp(hero, overflow)
            } else {
                return gapToMax
            }
        } else {
            return xp
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

    fun heroGainAsc(hero: Hero, asc: Int): Int {
        val gapToMax = hero.ascPointsMax - hero.ascPoints
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
                return gapToMax + heroGainAsc(hero, overflow)
            } else {
                return gapToMax
            }
        } else {
            return asc
        }
    }
}
