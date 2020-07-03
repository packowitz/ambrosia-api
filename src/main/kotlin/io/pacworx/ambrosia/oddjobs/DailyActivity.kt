package io.pacworx.ambrosia.oddjobs

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DailyActivity(
    @Id
    val playerId: Long,
    var day1: LocalDateTime? = null,
    var day1claimed: Boolean = false,
    var day2: LocalDateTime? = null,
    var day2claimed: Boolean = false,
    var day3: LocalDateTime? = null,
    var day3claimed: Boolean = false,
    var day4: LocalDateTime? = null,
    var day4claimed: Boolean = false,
    var day5: LocalDateTime? = null,
    var day5claimed: Boolean = false,
    var day6: LocalDateTime? = null,
    var day6claimed: Boolean = false,
    var day7: LocalDateTime? = null,
    var day7claimed: Boolean = false,
    var day8: LocalDateTime? = null,
    var day8claimed: Boolean = false,
    var day9: LocalDateTime? = null,
    var day9claimed: Boolean = false,
    var day10: LocalDateTime? = null,
    var day10claimed: Boolean = false
) {

    fun getToday(): Int {
        val now = LocalDateTime.now()
        return when {
            day1 == null || !isOtherDay(now, day1) -> 1
            day2 == null || !isOtherDay(now, day2) -> 2
            day3 == null || !isOtherDay(now, day3) -> 3
            day4 == null || !isOtherDay(now, day4) -> 4
            day5 == null || !isOtherDay(now, day5) -> 5
            day6 == null || !isOtherDay(now, day6) -> 6
            day7 == null || !isOtherDay(now, day7) -> 7
            day8 == null || !isOtherDay(now, day8) -> 8
            day9 == null || !isOtherDay(now, day9) -> 9
            day10 == null || !isOtherDay(now, day10) -> 10
            else -> -1
        }
    }

    fun isClaimable(day: Int): Boolean {
        return when(day) {
            1 -> day1 != null && !day1claimed
            2 -> day2 != null && !day2claimed
            3 -> day3 != null && !day3claimed
            4 -> day4 != null && !day4claimed
            5 -> day5 != null && !day5claimed
            6 -> day6 != null && !day6claimed
            7 -> day7 != null && !day7claimed
            8 -> day8 != null && !day8claimed
            9 -> day9 != null && !day9claimed
            10 -> day10 != null && !day10claimed
            else -> false
        }
    }

    fun claim(day: Int) {
        when(day) {
            1 -> day1claimed = true
            2 -> day2claimed = true
            3 -> day3claimed = true
            4 -> day4claimed = true
            5 -> day5claimed = true
            6 -> day6claimed = true
            7 -> day7claimed = true
            8 -> day8claimed = true
            9 -> day9claimed = true
            10 -> day10claimed = true
        }
    }

    fun reset() {
        day1 = null
        day1claimed = false
        day2 = null
        day2claimed = false
        day3 = null
        day3claimed = false
        day4 = null
        day4claimed = false
        day5 = null
        day5claimed = false
        day6 = null
        day6claimed = false
        day7 = null
        day7claimed = false
        day8 = null
        day8claimed = false
        day9 = null
        day9claimed = false
        day10 = null
        day10claimed = false
    }

    fun checkForReset() {
        if (day10claimed && isOtherDay(LocalDateTime.now(), day10)) {
            reset()
        }
    }

    fun activityDetected(): Boolean {
        val now = LocalDateTime.now()
        if (day1 == null) {
            day1 = now
            return true
        } else if (day2 == null) {
            if (isOtherDay(now, day1)) {
                day2 = now
                return true
            }
        } else if (day3 == null) {
            if (isOtherDay(now, day2)) {
                day3 = now
                return true
            }
        } else if (day4 == null) {
            if (isOtherDay(now, day3)) {
                day4 = now
                return true
            }
        } else if (day5 == null) {
            if (isOtherDay(now, day4)) {
                day5 = now
                return true
            }
        } else if (day6 == null) {
            if (isOtherDay(now, day5)) {
                day6 = now
                return true
            }
        } else if (day7 == null) {
            if (isOtherDay(now, day6)) {
                day7 = now
                return true
            }
        } else if (day8 == null) {
            if (isOtherDay(now, day7)) {
                day8 = now
                return true
            }
        } else if (day9 == null) {
            if (isOtherDay(now, day8)) {
                day9 = now
                return true
            }
        } else if (day10 == null) {
            if (isOtherDay(now, day9)) {
                day10 = now
                return true
            }
        } else {
            if (isOtherDay(now, day10)) {
                reset()
                day1 = now
                return true
            }
        }

        return false
    }

    private fun isOtherDay(now: LocalDateTime, check: LocalDateTime?): Boolean {
        return check?.dayOfMonth != now.dayOfMonth ||
            check.month != now.month ||
            check.year != now.year
    }
}