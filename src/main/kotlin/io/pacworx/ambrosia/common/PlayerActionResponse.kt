package io.pacworx.ambrosia.common

import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.achievements.Achievements
import io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.battle.offline.Mission
import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.Incubator
import io.pacworx.ambrosia.expedition.Expedition
import io.pacworx.ambrosia.expedition.PlayerExpedition
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.Jewelry
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.maps.PlayerMapResolved
import io.pacworx.ambrosia.oddjobs.DailyActivity
import io.pacworx.ambrosia.oddjobs.OddJob
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.properties.PropertyVersion
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.upgrade.Upgrade
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehiclePart

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlayerActionResponse(
    val player: Player? = null,
    val progress: Progress? = null,
    val achievements: Achievements? = null,
    val resources: Resources? = null,
    val token: String? = null,
    val hero: HeroDto? = null,
    val heroes: List<HeroDto>? = null,
    val heroIdsRemoved: List<Long>? = null,
    val gear: Gear? = null,
    val gears: List<Gear>? = null,
    val gearIdsRemovedFromArmory: List<Long>? = null,
    val jewelries: List<Jewelry>? = null,
    val buildings: List<Building>? = null,
    val vehicles: List<Vehicle>? = null,
    val vehicleParts: List<VehiclePart>? = null,
    val playerMaps: List<PlayerMapResolved>? = null,
    val currentMap: PlayerMapResolved? = null,
    val ongoingBattle: Battle? = null,
    val looted: Looted? = null,
    val missions: List<Mission>? = null,
    val missionIdFinished: Long? = null,
    val upgrades: List<Upgrade>? = null,
    val upgradeRemoved: Long? = null,
    val incubators: List<Incubator>? = null,
    val incubatorDone: Long? = null,
    val knownStories: List<String>? = null,
    val expeditions: List<Expedition>? = null,
    val playerExpeditions: List<PlayerExpedition>? = null,
    val playerExpeditionCancelled: Long? = null,
    val oddJobs: List<OddJob>? = null,
    val oddJobDone: Long? = null,
    val dailyActivity: DailyActivity? = null,
    val propertyVersions: List<PropertyVersion>? = null
)
