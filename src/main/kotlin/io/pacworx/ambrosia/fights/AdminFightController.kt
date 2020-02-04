package io.pacworx.ambrosia.fights

import io.pacworx.ambrosia.fights.environment.FightEnvironmentRepository
import io.pacworx.ambrosia.fights.stageconfig.FightStageConfigRepository
import io.pacworx.ambrosia.loot.LootBoxRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/fight")
class AdminFightController(private val fightRepository: FightRepository,
                           private val fightService: FightService,
                           private val fightStageConfigRepository: FightStageConfigRepository,
                           private val fightEnvironmentRepository: FightEnvironmentRepository,
                           private val lootBoxRepository: LootBoxRepository
) {

    @PostMapping("new")
    @Transactional
    fun createFight(@RequestBody fight: Fight): Fight {
        fight.stageConfig = fightStageConfigRepository.findByDefaultConfigTrue()
        fight.environment = fightEnvironmentRepository.findByDefaultEnvironmentTrue()
        fight.lootBox = lootBoxRepository.findFirstByOrderById()
        return fightRepository.save(fight)
    }

    @PutMapping("{id}")
    @Transactional
    fun updateFight(@PathVariable id: Long, @RequestBody @Valid fight: Fight): FightService.FightResolved {
        return fightService.asFightResolved(fightRepository.save(fight))
    }
}
