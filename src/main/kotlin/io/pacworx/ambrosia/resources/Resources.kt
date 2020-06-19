package io.pacworx.ambrosia.resources

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Resources(
    @Id
    val playerId: Long,
    var steam: Int,
    var steamUsed: Int = 0,
    var steamMax: Int,
    @JsonIgnore var steamProduceSeconds: Int = 300,
    @JsonIgnore var steamLastProduction: LocalDateTime = LocalDateTime.now(),
    var cogwheels: Int,
    var cogwheelsUsed: Int = 0,
    var cogwheelsMax: Int,
    @JsonIgnore var cogwheelsProduceSeconds: Int = 3600,
    @JsonIgnore var cogwheelsLastProduction: LocalDateTime = LocalDateTime.now(),
    var tokens: Int,
    var tokensUsed: Int = 0,
    var tokensMax: Int,
    @JsonIgnore var tokensProduceSeconds: Int = 7200,
    @JsonIgnore var tokensLastProduction: LocalDateTime = LocalDateTime.now(),
    var premiumSteam: Int = 0,
    var premiumSteamUsed: Int = 0,
    var premiumSteamMax: Int,
    var premiumCogwheels: Int = 0,
    var premiumCogwheelsUsed: Int = 0,
    var premiumCogwheelsMax: Int,
    var premiumTokens: Int = 0,
    var premiumTokensUsed: Int = 0,
    var premiumTokensMax: Int,
    var rubies: Int = 0,
    var rubiesUsed: Int = 0,
    var coinsUsed: Int = 0,
    var coins: Int,
    var metal: Int = 0,
    var metalUsed: Int = 0,
    var metalMax: Int,
    var iron: Int = 0,
    var ironUsed: Int = 0,
    var ironMax: Int,
    var steel: Int = 0,
    var steelUsed: Int = 0,
    var steelMax: Int,
    var wood: Int = 0,
    var woodUsed: Int = 0,
    var woodMax: Int,
    var brownCoal: Int = 0,
    var brownCoalUsed: Int = 0,
    var brownCoalMax: Int,
    var blackCoal: Int = 0,
    var blackCoalUsed: Int = 0,
    var blackCoalMax: Int,
    var simpleGenome: Int = 0,
    var simpleGenomeUsed: Int = 0,
    var commonGenome: Int = 0,
    var commonGenomeUsed: Int = 0,
    var uncommonGenome: Int = 0,
    var uncommonGenomeUsed: Int = 0,
    var rareGenome: Int = 0,
    var rareGenomeUsed: Int = 0,
    var epicGenome: Int = 0,
    var epicGenomeUsed: Int = 0
) {
    fun getSteamProduceIn(): Int? {
        return if (steam >= steamMax) {
            null
        } else {
            val nextProd = steamLastProduction.plusSeconds(steamProduceSeconds.toLong())
            LocalDateTime.now().until(nextProd, ChronoUnit.SECONDS).toInt()
        }
    }

    fun getCogwheelsProduceIn(): Int? {
        return if (cogwheels >= cogwheelsMax) {
            null
        } else {
            val nextProd = cogwheelsLastProduction.plusSeconds(cogwheelsProduceSeconds.toLong())
            LocalDateTime.now().until(nextProd, ChronoUnit.SECONDS).toInt()
        }
    }

    fun getTokensProduceIn(): Int? {
        return if (tokens >= tokensMax) {
            null
        } else {
            val nextProd = tokensLastProduction.plusSeconds(tokensProduceSeconds.toLong())
            LocalDateTime.now().until(nextProd, ChronoUnit.SECONDS).toInt()
        }
    }

    fun resetMaxValues() {
        steamMax = 0
        premiumSteamMax = 0
        cogwheelsMax = 0
        premiumCogwheelsMax = 0
        tokensMax = 0
        premiumTokensMax = 0
        metalMax = 0
        ironMax = 0
        steelMax = 0
        woodMax = 0
        blackCoalMax = 0
        brownCoalMax = 0
    }
}