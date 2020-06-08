package io.pacworx.ambrosia.fights

import io.pacworx.ambrosia.fights.environment.FightEnvironmentRepository
import io.pacworx.ambrosia.fights.stageconfig.FightStageConfigRepository
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/fight")
class AdminFightController(private val fightRepository: FightRepository,
                           private val fightService: FightService,
                           private val fightStageConfigRepository: FightStageConfigRepository,
                           private val fightEnvironmentRepository: FightEnvironmentRepository,
                           private val lootBoxRepository: LootBoxRepository,
                           private val auditLogService: AuditLogService
) {

    @PostMapping("new")
    @Transactional
    fun createFight(@ModelAttribute("player") player: Player,
                    @RequestBody fight: Fight): Fight {
        fight.stageConfig = fightStageConfigRepository.findByDefaultConfigTrue()
        fight.environment = fightEnvironmentRepository.findByDefaultEnvironmentTrue()
        fight.lootBox = lootBoxRepository.findFirstByOrderById()
        return fightRepository.save(fight)
            .also{ auditLogService.log(player, "Created fight ${it.name} #${it.id}", adminAction = true) }
    }

    @PutMapping
    @Transactional
    fun updateFight(@ModelAttribute("player") player: Player,
                    @RequestBody @Valid fight: Fight): FightService.FightResolved {
        auditLogService.log(player, "Updates fight ${fight.name} #${fight.id}", adminAction = true)
        return fightService.asFightResolved(fightRepository.save(fight))
    }
}
