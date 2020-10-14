package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootBoxType
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/expedition")
class AdminExpeditionBaseController(private val expeditionBaseRepository: ExpeditionBaseRepository,
                                    private val auditLogService: AuditLogService,
                                    private val lootBoxRepository: LootBoxRepository
) {
    @GetMapping
    fun getAllExpeditions(): List<ExpeditionBase> = expeditionBaseRepository.findAll()

    @PostMapping
    @Transactional
    fun saveExpeditionBase(@ModelAttribute("player") player: Player,
                           @RequestBody @Valid expeditionBase: ExpeditionBase): ExpeditionBase {
        val lootBox = lootBoxRepository.findByIdOrNull(expeditionBase.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", expeditionBase.lootBoxId)
        if (lootBox.type != LootBoxType.EXPEDITION) {
            throw GeneralException(player, "Invalid expedition", "Loot must be of type EXPEDITION")
        }
        return expeditionBaseRepository.save(expeditionBase).also {
            auditLogService.log(player, "Create or Update expeditionBase ${it.name} #${it.id}", adminAction = true)
        }
    }
}
