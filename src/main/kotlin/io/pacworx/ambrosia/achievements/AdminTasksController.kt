package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootBoxType
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/tasks")
class AdminTasksController (
    private val taskClusterRepository: TaskClusterRepository,
    private val lootBoxRepository: LootBoxRepository,
    private val auditLogService: AuditLogService,
    private val taskService: TaskService
) {

    @PostMapping
    @Transactional
    fun saveTaskCluster(@ModelAttribute("player") player: Player,
                        @RequestBody taskCluster: TaskCluster): TaskCluster {
        taskCluster.tasks.sortedBy { it.number }.forEachIndexed { index, task ->
            if (task.number != index + 1) {
                throw GeneralException(player, "Invalid task #${task.number}", "It has an invalid number")
            }
            val lootBox = lootBoxRepository.findByIdOrNull(task.lootBoxId)
                ?: throw EntityNotFoundException(player, "lootBox", task.lootBoxId)
            if (lootBox.type != LootBoxType.ACHIEVEMENT) {
                throw GeneralException(player, "Invalid task #${task.number}", "Loot must be of type ACHIEVEMENT")
            }
        }

        taskService.emptyCache()

        val savedTaskCluster = taskClusterRepository.save(taskCluster)
        auditLogService.log(player, "Create or Update task cluster #${savedTaskCluster.id}", adminAction = true)
        savedTaskCluster.tasks.forEach { taskService.addLootItems(it) }
        return savedTaskCluster
    }
}