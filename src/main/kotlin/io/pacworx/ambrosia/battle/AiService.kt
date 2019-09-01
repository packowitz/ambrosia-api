package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import org.springframework.stereotype.Service

@Service
class AiService {

    private val SPEEDBAR_REDUCTION: Int = 10000

    fun doAction(battle: Battle, hero: BattleHero) {
        //select skill
        //use skill
        hero.currentSpeedBar -= SPEEDBAR_REDUCTION
    }
}