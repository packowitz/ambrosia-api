package io.pacworx.ambrosia.vehicle

enum class PartQuality(private val value: Int) {
    BASIC(1), MODERATE(2), GOOD(3);

    fun isHigherThan(other: PartQuality): Boolean {
        return this.value > other.value
    }
}
