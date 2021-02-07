package io.pacworx.ambrosia.speedup

enum class SpeedupType {
    EXPEDITION {
        override fun oneRubySeconds(duration: Long): Long = 250 + (duration / 3600) * 50

        override fun increase(duration: Long): Double = 1.0 / (2 + (duration / 3600))
    },
    MISSION {
        override fun oneRubySeconds(duration: Long): Long = 30

        override fun increase(duration: Long): Double = 1.0
    },
    INCUBATION {
        override fun oneRubySeconds(duration: Long): Long = 90

        override fun increase(duration: Long): Double = 0.1
    },
    UPGRADE {
        override fun oneRubySeconds(duration: Long): Long = 50

        override fun increase(duration: Long): Double = 0.075
    };

    open fun oneRubySeconds(duration: Long): Long = 0

    open fun increase(duration: Long): Double = 1.0
}