package io.pacworx.ambrosia.resources

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Resources(
    @Id
    val playerId: Long,
    var steam: Int,
    var steamMax: Int,
    var steamProduceSeconds: Int = 300,
    var steamLastProduction: LocalDateTime = LocalDateTime.now(),
    var cogwheels: Int,
    var cogwheelsMax: Int,
    var cogwheelsProduceSeconds: Int = 3600,
    var cogwheelsLastProduction: LocalDateTime = LocalDateTime.now(),
    var tokens: Int,
    var tokensMax: Int,
    var tokensProduceSeconds: Int = 7200,
    var tokensLastProduction: LocalDateTime = LocalDateTime.now(),
    var premiumSteam: Int = 0,
    var premiumSteamMax: Int,
    var premiumCogwheels: Int = 0,
    var premiumCogwheelsMax: Int,
    var premiumTokens: Int = 0,
    var premiumTokensMax: Int,
    var rubies: Int = 0,
    var coins: Int,
    var metal: Int = 0,
    var metalMax: Int,
    var iron: Int = 0,
    var ironMax: Int,
    var steal: Int = 0,
    var stealMax: Int,
    var wood: Int = 0,
    var woodMax: Int,
    var brownCoal: Int = 0,
    var brownCoalMax: Int,
    var blackCoal: Int = 0,
    var blackCoalMax: Int,
    var simpleGenome: Int = 0,
    var commonGenome: Int = 0,
    var uncommonGenome: Int = 0,
    var rareGenome: Int = 0,
    var epicGenome: Int = 0
)