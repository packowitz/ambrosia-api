package io.pacworx.ambrosia.speedup

import org.springframework.stereotype.Service
import kotlin.math.ln
import kotlin.math.roundToInt

@Service
class SpeedupService {

    fun speedup(type: SpeedupType, duration: Long, secondsLeft: Long): Speedup {
        return calc(type.oneRubySeconds(duration), type.increase(duration), duration, secondsLeft)
    }

    private fun calc(oneRubySecs: Long, inc: Double, duration: Long, secondsLeft: Long): Speedup {
        var secs = oneRubySecs
        val speedup = Speedup(
            rubies = 1,
            secondsUntilChange = secondsLeft
        )
        while ((secs + 3) <= secondsLeft) {
            val secsAdded = if (inc == 1.0) {
                oneRubySecs.toInt()
            } else {
                val fixedPart = (secs * inc).roundToInt()
                val lnPart = 2.0 / ln(Math.E + speedup.rubies)
                (fixedPart * lnPart).roundToInt()
            }

            speedup.rubies++
            speedup.secondsUntilChange = secondsLeft - secs
            secs += secsAdded
        }
        return speedup
    }
}