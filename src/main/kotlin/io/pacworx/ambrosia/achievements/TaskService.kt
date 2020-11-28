package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val playerTaskRepository: PlayerTaskRepository,
    private val taskClusterRepository: TaskClusterRepository,
    private val lootBoxRepository: LootBoxRepository
) {

    var cache: List<TaskCluster>? = null

    fun emptyCache() {
        cache = null
    }

    fun getAllClusters(): List<TaskCluster> {
        return cache ?: taskClusterRepository.findAll().let {
            it.forEach { cluster ->
                cluster.tasks.forEach { task -> addLootItems(task) }
            }
            cache = it
            it
        }
    }

    fun getCluster(clusterId: Long): TaskCluster? {
        return getAllClusters().find { it.id == clusterId }
    }

    fun getPlayerTasks(player: Player): List<PlayerTask> {
        val tasks = playerTaskRepository.findByPlayerId(player.id).toMutableList()
        getAllClusters().forEach { cluster ->
            if (tasks.none { it.taskClusterId == cluster.id }) {
                tasks.add(playerTaskRepository.save(PlayerTask(
                    playerId = player.id,
                    taskClusterId = cluster.id,
                    currentTaskNumber = 1
                )))
            }
        }
        return tasks
    }

    fun getPlayerTask(player: Player, clusterId: Long): PlayerTask? {
        return playerTaskRepository.findByPlayerIdAndTaskClusterId(player.id, clusterId);
    }

    fun addLootItems(task: Task) {
        val lootBox = lootBoxRepository.findByIdOrNull(task.lootBoxId)
            ?: throw ConfigurationException("lootBox ${task.lootBoxId} not found")
        task.reward = lootBox.items.map {
            LootedItem(
                type = it.type,
                resourceType = it.resourceType,
                progressStat = it.progressStat,
                jewelType = it.getJewelTypes().takeIf { list -> list.size == 1 }?.first(),
                value = when {
                    it.resourceType != null -> it.resourceFrom
                    it.progressStatBonus != null -> it.progressStatBonus
                    it.jewelLevel != null -> it.jewelLevel
                    else -> 0
                }?.toLong() ?: 0
            )
        }
    }
}