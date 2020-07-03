package io.pacworx.ambrosia.oddjobs

import io.pacworx.ambrosia.battle.offline.Mission
import io.pacworx.ambrosia.common.randomRarity
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.expedition.Expedition
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootItemResult
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.upgrade.Upgrade
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class OddJobService(
    private val oddJobBaseRepository: OddJobBaseRepository,
    private val oddJobRepository: OddJobRepository,
    private val progressRepository: ProgressRepository,
    private val lootBoxRepository: LootBoxRepository
) {

    fun createOddJob(player: Player, level: Int): OddJob? {
        val progress = progressRepository.getOne(player.id)
        val oddJobs = oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
        if (oddJobs.size >= progress.numberOddJobs) {
            return null
        }
        val rarity = randomRarity()
        val oddJobBase = oddJobBaseRepository.findAllByActiveIsTrueAndLevelAndRarity(level, rarity)
            .takeIf { it.isNotEmpty() }?.random() ?: return null
        return oddJobRepository.save(OddJob(
            playerId = player.id,
            oddJobBaseId = oddJobBase.id,
            level = oddJobBase.level,
            rarity = oddJobBase.rarity,
            jobType = oddJobBase.jobType,
            jobAmount = oddJobBase.jobAmount,
            lootBoxId = oddJobBase.lootBoxId
        )).also { addLootItems(player, it) }
    }

    fun getOddJobs(player: Player): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .also { jobs -> jobs.forEach { addLootItems(player, it) } }
    }

    fun resourcesSpend(player: Player, resourceType: ResourceType, amount: Int): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job ->
                (job.jobType == OddJobType.SPEND_STEAM && resourceType == ResourceType.STEAM) ||
                    (job.jobType == OddJobType.SPEND_COGWHEELS && resourceType == ResourceType.COGWHEELS) ||
                    (job.jobType == OddJobType.SPEND_TOKENS && resourceType == ResourceType.TOKENS)
            }
            .also { jobs -> jobs.forEach {
                it.jobAmountDone = min(it.jobAmount, it.jobAmountDone + amount)
                addLootItems(player, it) }
            }
    }

    fun missionFinished(player: Player, mission: Mission): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job -> job.jobType == OddJobType.FINISH_MISSIONS }
            .also { jobs -> jobs.forEach {job ->
                job.jobAmountDone = min(job.jobAmount, job.jobAmountDone + mission.battles.count { it.battleFinished })
                addLootItems(player, job) }
            }
    }

    fun chestOpened(player: Player): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job ->
                job.jobType  == OddJobType.OPEN_CHESTS
            }
            .also { jobs -> jobs.forEach {job ->
                job.jobAmountDone = min(job.jobAmount, job.jobAmountDone + 1)
                addLootItems(player, job) }
            }
    }

    fun looted(player: Player, lootedItems: List<LootItemResult>): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job ->
                job.jobType in listOf(OddJobType.LOOT_COINS, OddJobType.LOOT_GEAR, OddJobType.LOOT_PARTS)
            }
            .also { jobs -> jobs.forEach {job ->
                val inc =
                    when (job.jobType) {
                        OddJobType.LOOT_COINS -> {
                            lootedItems.filter { it.resource?.type == ResourceType.COINS }.sumBy { it.resource?.amount ?: 0 }
                        }
                        OddJobType.LOOT_GEAR -> {
                            lootedItems.filter { it.gear != null }.size
                        }
                        OddJobType.LOOT_PARTS -> {
                            lootedItems.filter { it.vehiclePart != null }.size
                        }
                        else -> { 0 }
                    }
                job.jobAmountDone = min(job.jobAmount, job.jobAmountDone + inc)
                addLootItems(player, job) }
            }
    }

    fun upgradeFinished(player: Player, upgrade: Upgrade): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job ->
                (job.jobType == OddJobType.UPGRADE_BUILDING && upgrade.buildingType != null) ||
                    (job.jobType == OddJobType.UPGRADE_VEHICLE && upgrade.vehicleId != null) ||
                    (job.jobType == OddJobType.UPGRADE_VEHICLE && upgrade.vehiclePartId != null) ||
                    (job.jobType == OddJobType.MERGE_JEWELS && upgrade.jewelType != null) ||
                    (job.jobType == OddJobType.MODIFY_GEAR && upgrade.gearId != null)
            }
            .also { jobs -> jobs.forEach {
                it.jobAmountDone = min(it.jobAmount, it.jobAmountDone + 1)
                addLootItems(player, it) }
            }
    }

    fun gearBreakDownGear(player: Player, gears: List<Gear>): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job -> job.jobType == OddJobType.BREAKDOWN_GEAR }
            .also { jobs -> jobs.forEach {job ->
                job.jobAmountDone = min(job.jobAmount, job.jobAmountDone + gears.size)
                addLootItems(player, job) }
            }
    }

    fun expeditionFinished(player: Player, expedition: Expedition): List<OddJob> {
        return oddJobRepository.findAllByPlayerIdOrderByCreated(player.id)
            .filter { job -> job.jobType == OddJobType.FINISH_EXPEDITIONS }
            .also { jobs -> jobs.forEach {job ->
                job.jobAmountDone = min(job.jobAmount, job.jobAmountDone + 1)
                addLootItems(player, job) }
            }
    }

    private fun addLootItems(player: Player, oddJob: OddJob) {
        val lootBox = lootBoxRepository.findByIdOrNull(oddJob.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", oddJob.lootBoxId)
        oddJob.reward = lootBox.items.map {
            LootedItem(
                type = it.type,
                resourceType = it.resourceType,
                progressStat = it.progressStat,
                value = if (it.resourceType != null) { it.resourceFrom } else { it.progressStatBonus }?.toLong() ?: 0
            )
        }
    }
}