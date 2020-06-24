package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/expedition")
class AdminExpeditionBaseController(private val expeditionBaseRepository: ExpeditionBaseRepository,
                                    private val auditLogService: AuditLogService
) {
    @GetMapping
    fun getAllExpeditions(): List<ExpeditionBase> = expeditionBaseRepository.findAll()

    @PostMapping
    @Transactional
    fun saveExpeditionBase(@ModelAttribute("player") player: Player,
                           @RequestBody @Valid expeditionBase: ExpeditionBase): ExpeditionBase {
        return expeditionBaseRepository.save(expeditionBase).also {
            auditLogService.log(player, "Create or Update expeditionBase ${it.name} #${it.id}", adminAction = true)
        }
    }
}
