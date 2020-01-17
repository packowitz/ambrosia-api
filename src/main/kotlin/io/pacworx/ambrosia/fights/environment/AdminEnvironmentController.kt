package io.pacworx.ambrosia.io.pacworx.ambrosia.fights.environment

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
@RequestMapping("admin/fight_environment")
class AdminEnvironmentController(val fightEnvironmentRepository: FightEnvironmentRepository) {

    @GetMapping
    fun getConfigs(): List<FightEnvironment> = fightEnvironmentRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createStageConfig(@RequestBody environment: FightEnvironment): FightEnvironment {
        return fightEnvironmentRepository.save(environment)
    }

    @PutMapping("{id}")
    @Transactional
    fun updateFight(@PathVariable id: Long, @RequestBody @Valid environment: FightEnvironment): FightEnvironment {
        if (environment.defaultEnvironment) {
            fightEnvironmentRepository.markDefaultEnvironment(environment.id)
        }
        return fightEnvironmentRepository.save(environment)
    }
}
