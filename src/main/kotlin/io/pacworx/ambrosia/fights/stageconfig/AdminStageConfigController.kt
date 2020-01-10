package io.pacworx.ambrosia.io.pacworx.ambrosia.fights.stageconfig

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
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
class AdminStageConfigController(val fightStageConfigRepository: FightStageConfigRepository) {

    @GetMapping
    fun getConfigs(): List<FightStageConfig> = fightStageConfigRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createStageConfig(@RequestBody config: FightStageConfig): FightStageConfig {
        return fightStageConfigRepository.save(config)
    }

    @PutMapping("{id}")
    @Transactional
    fun updateFight(@PathVariable id: Long, @RequestBody @Valid config: FightStageConfig): FightStageConfig {
        return fightStageConfigRepository.save(config)
    }
}
