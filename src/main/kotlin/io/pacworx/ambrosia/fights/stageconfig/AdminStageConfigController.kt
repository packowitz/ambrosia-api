package io.pacworx.ambrosia.fights.stageconfig

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
@RequestMapping("admin/fight_stage_config")
class AdminStageConfigController(
    val fightStageConfigRepository: FightStageConfigRepository,
    val auditLogService: AuditLogService
) {

    @GetMapping
    fun getConfigs(): List<FightStageConfig> = fightStageConfigRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createStageConfig(@ModelAttribute("player") player: Player,
                          @RequestBody config: FightStageConfig): FightStageConfig {
        return fightStageConfigRepository.save(config)
            .also {
                auditLogService.log(player, "Creates stage configuration ${it.name} #${it.id}", adminAction = true)
            }
    }

    @PutMapping("{id}")
    @Transactional
    fun updateFight(@ModelAttribute("player") player: Player,
                    @PathVariable id: Long,
                    @RequestBody @Valid config: FightStageConfig): FightStageConfig {
        if (config.defaultConfig) {
            fightStageConfigRepository.markDefaultConfig(config.id)
        }
        return fightStageConfigRepository.save(config)
            .also {
                auditLogService.log(player, "Updates environment ${it.name} #${it.id}", adminAction = true)
            }
    }
}
