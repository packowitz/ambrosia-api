package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.upgrade.Cost
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.Instant
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("laboratory")
class LaboratoryController(private val incubatorRepository: IncubatorRepository,
                           private val propertyService: PropertyService,
                           private val progressRepository: ProgressRepository,
                           private val resourcesService: ResourcesService,
                           private val heroService: HeroService) {

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
            throw RuntimeException("No incubator available to clone hero")
        }
        val costs: List<Cost>
        var time: Int
        when (type) {
            GenomeType.SIMPLE_GENOME -> {
                costs = propertyService.getAllProperties(PropertyType.SIMPLE_GENOME_COST).map { Cost(it.value1, it.resourceType!!) }
                time = propertyService.getAllProperties(PropertyType.SIMPLE_GENOME_TIME).first().value1
            }
            GenomeType.COMMON_GENOME -> {
                costs = propertyService.getAllProperties(PropertyType.COMMON_GENOME_COST).map { Cost(it.value1, it.resourceType!!) }
                time = propertyService.getAllProperties(PropertyType.COMMON_GENOME_TIME).first().value1
            }
            GenomeType.UNCOMMON_GENOME -> {
                costs = propertyService.getAllProperties(PropertyType.UNCOMMON_GENOME_COST).map { Cost(it.value1, it.resourceType!!) }
                time = propertyService.getAllProperties(PropertyType.UNCOMMON_GENOME_TIME).first().value1
            }
            GenomeType.RARE_GENOME -> {
                costs = propertyService.getAllProperties(PropertyType.RARE_GENOME_COST).map { Cost(it.value1, it.resourceType!!) }
                time = propertyService.getAllProperties(PropertyType.RARE_GENOME_TIME).first().value1
            }
            GenomeType.EPIC_GENOME -> {
                costs = propertyService.getAllProperties(PropertyType.EPIC_GENOME_COST).map { Cost(it.value1, it.resourceType!!) }
                time = propertyService.getAllProperties(PropertyType.EPIC_GENOME_TIME).first().value1
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
        } else {
            hero = heroService.asHeroDto(heroService.recruitHero(player, type.commonChance, type.uncommonChance, type.rareChance, type.epicChance, type.defaultRarity))
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
            ?: throw RuntimeException("Unknown incubator")
        if (cube.playerId != player.id) {
            throw RuntimeException("You can only open incubators you own")
        }
        if (!cube.isFinished()) {
            throw RuntimeException("Incubator is not finished yet")
        }
        val hero = heroService.asHeroDto(heroService.recruitHero(player, cube.type.commonChance, cube.type.uncommonChance, cube.type.rareChance, cube.type.epicChance, cube.type.defaultRarity))
        incubatorRepository.delete(cube)
        return PlayerActionResponse(
            heroes = listOfNotNull(hero),
            incubatorDone = cube.id
        )
    }

    @PostMapping("cancel/{cubeId}")
    @Transactional
    fun cancel(@ModelAttribute("player") player: Player,
               @PathVariable cubeId: Long): PlayerActionResponse {
        val incubator = incubatorRepository.findByIdOrNull(cubeId)
            ?: throw RuntimeException("Unknown incubator")
        if (incubator.playerId != player.id) {
            throw RuntimeException("You can only cancel incubators you own")
        }
        if (incubator.isFinished()) {
            throw RuntimeException("Incubator is already finished")
        }

        var resources: Resources? = null
        incubator.getResourcesAsCosts().forEach {
            resources = resourcesService.gainResources(player, it.type, it.amount)
        }

        incubatorRepository.delete(incubator)
        return PlayerActionResponse(
            resources = resources,
            incubatorDone = incubator.id
        )
    }
}