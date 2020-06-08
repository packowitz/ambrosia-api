package io.pacworx.ambrosia.fights.environment

import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/fight_environment")
class AdminEnvironmentController(
    val fightEnvironmentRepository: FightEnvironmentRepository,
    val auditLogService: AuditLogService
) {

    @GetMapping
    fun getConfigs(): List<FightEnvironment> = fightEnvironmentRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createStageConfig(@ModelAttribute("player") player: Player,
                          @RequestBody environment: FightEnvironment): FightEnvironment {
        return fightEnvironmentRepository.save(environment)
            .also {
                auditLogService.log(player, "Creates environment ${it.name} #${it.id}", adminAction = true)
            }
    }

    @PutMapping("{id}")
    @Transactional
    fun updateFight(@ModelAttribute("player") player: Player,
                    @PathVariable id: Long,
                    @RequestBody @Valid environment: FightEnvironment): FightEnvironment {
        if (environment.defaultEnvironment) {
            fightEnvironmentRepository.markDefaultEnvironment(environment.id)
        }
        return fightEnvironmentRepository.save(environment)
            .also {
                auditLogService.log(player, "Updates environment ${it.name} #${it.id}", adminAction = true)
            }
    }
}
