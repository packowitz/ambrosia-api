package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.InsufficientResourcesException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.upgrade.Cost
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("laboratory")
class LaboratoryController(
    private val incubatorRepository: IncubatorRepository,
    private val propertyService: PropertyService,
    private val progressRepository: ProgressRepository,
    private val resourcesService: ResourcesService,
    private val heroService: HeroService,
    private val auditLogService: AuditLogService,
    private val achievementsRepository: AchievementsRepository
) {

    @GetMapping("incubators")
    fun incubators(@ModelAttribute("player") player: Player): List<Incubator> =
        incubatorRepository.findAllByPlayerIdOrderByStartTimestamp(player.id)

    @PostMapping("clone/{type}")
    @Transactional
    fun clone(@ModelAttribute("player") player: Player,
              @PathVariable type: GenomeType): PlayerActionResponse {
        val progress = progressRepository.getOne(player.id)
        val cubes = incubatorRepository.findAllByPlayerIdOrderByStartTimestamp(player.id)
        val resources = resourcesService.getResources(player)
        if (cubes.size >= progress.incubators) {
            throw InsufficientResourcesException(player.id, "free incubator", 1)
        }
        if (heroService.getNumberOfHeroes(player) >= progress.barrackSize) {
            throw InsufficientResourcesException(player.id, "barrack space", 1)
        }
        val costs: List<Cost>
        var time: Int
        when (type) {
            GenomeType.SIMPLE_GENOME -> {
                costs = listOf(Cost(progress.simpleGenomesNeeded, ResourceType.SIMPLE_GENOME))
                time = propertyService.getProperties(PropertyType.SIMPLE_GENOME_TIME).first().value1
                if (progress.simpleIncubationUpPerMil > 0) {
                    val upTime = propertyService.getProperties(PropertyType.COMMON_GENOME_TIME).first().value1
                    time += (progress.simpleIncubationUpPerMil * upTime) / 100
                }
            }
            GenomeType.COMMON_GENOME -> {
                costs = listOf(Cost(progress.commonGenomesNeeded, ResourceType.COMMON_GENOME))
                time = propertyService.getProperties(PropertyType.COMMON_GENOME_TIME).first().value1
                if (progress.commonIncubationUpPerMil > 0) {
                    val upTime = propertyService.getProperties(PropertyType.UNCOMMON_GENOME_TIME).first().value1
                    time += (progress.commonIncubationUpPerMil * upTime) / 100
                }
            }
            GenomeType.UNCOMMON_GENOME -> {
                costs = listOf(Cost(progress.uncommonGenomesNeeded, ResourceType.UNCOMMON_GENOME))
                time = propertyService.getProperties(PropertyType.UNCOMMON_GENOME_TIME).first().value1
                if (progress.uncommonIncubationUpPerMil > 0) {
                    val upTime = propertyService.getProperties(PropertyType.RARE_GENOME_TIME).first().value1
                    time += (progress.uncommonIncubationUpPerMil * upTime) / 100
                }
            }
            GenomeType.RARE_GENOME -> {
                costs = listOf(Cost(progress.rareGenomesNeeded, ResourceType.RARE_GENOME))
                time = propertyService.getProperties(PropertyType.RARE_GENOME_TIME).first().value1
                if (progress.rareIncubationUpPerMil > 0) {
                    val upTime = propertyService.getProperties(PropertyType.EPIC_GENOME_TIME).first().value1
                    time += (progress.rareIncubationUpPerMil * upTime) / 100
                }
            }
            GenomeType.EPIC_GENOME -> {
                costs = listOf(Cost(progress.epicGenomesNeeded, ResourceType.EPIC_GENOME))
                time = propertyService.getProperties(PropertyType.EPIC_GENOME_TIME).first().value1
            }
        }
        costs.forEach { resourcesService.spendResource(resources, it.type, it.amount) }

        var hero: HeroDto? = null
        var incubator: Incubator? = null
        if (time > 0) {
            time = (time * 100) / progress.labSpeed
            val now = Instant.now()
            val cube = Incubator(
                playerId = player.id,
                type = type,
                startTimestamp = now,
                finishTimestamp = now.plusSeconds(time.toLong())
            )
            cube.setResources(costs)
            incubator = incubatorRepository.save(cube)
            auditLogService.log(player, "Starts cloning in incubator #${incubator.id} spending ${costs.joinToString { "${it.amount} ${it.type}" }}")
        } else {
            hero = heroService.asHeroDto(heroService.recruitHero(player, type.commonChance, type.uncommonChance, type.rareChance, type.epicChance, type.defaultRarity))
            auditLogService.log(player, "Immediate cloning of hero ${hero.heroBase.name} #${hero.id} spending ${costs.joinToString { "${it.amount} ${it.type}" }}")
        }
        return PlayerActionResponse(
            resources = resources,
            heroes = listOfNotNull(hero),
            incubators = listOfNotNull(incubator)
        )
    }

    @PostMapping("open/{cubeId}")
    @Transactional
    fun open(@ModelAttribute("player") player: Player,
             @PathVariable cubeId: Long): PlayerActionResponse {
        val cube = incubatorRepository.findByIdOrNull(cubeId)
            ?: throw EntityNotFoundException(player, "incubator", cubeId)
        if (cube.playerId != player.id) {
            throw UnauthorizedException(player, "You can only open incubators you own")
        }
        if (!cube.isFinished()) {
            throw GeneralException(player, "Work in progress", "The incubator cannot be finished. It still needs ${cube.getSecondsUntilDone()} seconds")
        }
        val progress = progressRepository.getOne(player.id)
        val hero = when (cube.type) {
            GenomeType.SIMPLE_GENOME -> heroService.recruitHero(player, commonChance = asDoubleChance(progress.simpleIncubationUpPerMil), default = Rarity.SIMPLE)
            GenomeType.COMMON_GENOME -> heroService.recruitHero(player, uncommonChance = asDoubleChance(progress.commonIncubationUpPerMil), default = Rarity.COMMON)
            GenomeType.UNCOMMON_GENOME -> heroService.recruitHero(player, rareChance = asDoubleChance(progress.uncommonIncubationUpPerMil), default = Rarity.UNCOMMON)
            GenomeType.RARE_GENOME -> heroService.recruitHero(player, epicChance = asDoubleChance(progress.rareIncubationUpPerMil), default = Rarity.RARE)
            GenomeType.EPIC_GENOME -> heroService.recruitHero(player, default = Rarity.EPIC)
        }
        if (hero.heroBase.rarity == Rarity.UNCOMMON && progress.uncommonStartingLevel > 1) {
            hero.level = progress.uncommonStartingLevel
            hero.maxXp = propertyService.getHeroMaxXp(hero.level)
        }

        auditLogService.log(player, "Finished incubator #${cube.id} and gained hero ${hero.heroBase.name} #${hero.id}")
        incubatorRepository.delete(cube)
        val achievements = achievementsRepository.getOne(player.id)
        achievements.incubationDone(cube.type)
        return PlayerActionResponse(
            achievements = achievements,
            heroes = listOfNotNull(heroService.asHeroDto(hero)),
            incubatorDone = cube.id
        )
    }

    private fun asDoubleChance(chancePerMil: Int): Double {
        return chancePerMil.toDouble() / 1000.0
    }

    @PostMapping("cancel/{cubeId}")
    @Transactional
    fun cancel(@ModelAttribute("player") player: Player,
               @PathVariable cubeId: Long): PlayerActionResponse {
        val incubator = incubatorRepository.findByIdOrNull(cubeId)
            ?: throw EntityNotFoundException(player, "incubator", cubeId)
        if (incubator.playerId != player.id) {
            throw UnauthorizedException(player, "You can only open incubators you own")
        }
        if (incubator.isFinished()) {
            throw GeneralException(player, "Cannot cancel incubation", "Incubator is already finished")
        }

        var resources: Resources? = null
        incubator.getResourcesAsCosts().forEach {
            resources = resourcesService.gainResources(player, it.type, it.amount)
        }

        auditLogService.log(player, "Cancel incubation #${incubator.id} regaining ${incubator.getResourcesAsCosts().joinToString { "${it.amount} ${it.type}" }}")
        incubatorRepository.delete(incubator)
        return PlayerActionResponse(
            resources = resources,
            incubatorDone = incubator.id
        )
    }
}
