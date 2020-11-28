package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("tasks")
class TasksController(
    private val progressRepository: ProgressRepository,
    private val resourcesService: ResourcesService,
    private val achievementsRepository: AchievementsRepository,
    private val lootService: LootService,
    private val inboxMessageRepository: InboxMessageRepository,
    private val taskService: TaskService
) {

    @GetMapping
    fun getAll(): List<TaskCluster> = taskService.getAllClusters()

    @PostMapping("claim/{taskClusterId}")
    @Transactional
    fun claim(@ModelAttribute("player") player: Player,
              @PathVariable taskClusterId: Long): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val playerTask = taskService.getPlayerTask(player, taskClusterId)
            ?: throw EntityNotFoundException(player, "playerTask", taskClusterId)
        val taskCluster = taskService.getCluster(taskClusterId)
            ?: throw EntityNotFoundException(player, "taskCluster", taskClusterId)
        val task = taskCluster.tasks.find { it.number == playerTask.currentTaskNumber }
            ?: throw GeneralException(player, "Cannot claim task", "Number ${playerTask.currentTaskNumber} not defined")

        val achievements = achievementsRepository.getOne(player.id)
        if (task.taskType.getAmount(achievements) < task.taskAmount) {
            throw GeneralException(player, "Cannot claim task", "Task not fulfilled")
        }

        val result = lootService.openLootBox(player, task.lootBoxId, achievements)
        playerTask.currentTaskNumber ++

        return PlayerActionResponse(
            resources = resourcesService.getResources(player),
            achievements = achievements,
            progress = if (result.items.any { it.progress != null }) { progressRepository.getOne(player.id) } else { null },
            heroes = result.items.filter { it.hero != null }.map { it.hero!! }.takeIf { it.isNotEmpty() },
            gears = result.items.filter { it.gear != null }.map { it.gear!! }.takeIf{ it.isNotEmpty() },
            jewelries = result.items.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicles = result.items.filter { it.vehicle != null }.map { it.vehicle!! }.takeIf { it.isNotEmpty() },
            vehicleParts = result.items.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            playerTasks = listOf(playerTask),
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1))
        )
    }
}