package io.pacworx.ambrosia.fights

import io.pacworx.ambrosia.io.pacworx.ambrosia.fights.stageconfig.FightStageConfigRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
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
                           private val fightStageConfigRepository: FightStageConfigRepository
) {

    @PostMapping("new")
    @Transactional
    fun createFight(@RequestBody fight: Fight): Fight {
        fight.stageConfig = fightStageConfigRepository.findByDefaultConfigTrue()
        return fightRepository.save(fight)
    }

    @PutMapping("{id}")
    @Transactional
    fun updateFight(@PathVariable id: Long, @RequestBody @Valid fight: Fight): FightService.FightResolved {
        return fightService.asFightResolved(fightRepository.save(fight))
    }
}
