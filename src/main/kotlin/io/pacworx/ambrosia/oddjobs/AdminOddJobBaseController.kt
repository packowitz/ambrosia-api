package io.pacworx.ambrosia.oddjobs

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootBoxType
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/oddjob")
class AdminOddJobBaseController(
    private val oddJobBaseRepository: OddJobBaseRepository,
    private val lootBoxRepository: LootBoxRepository,
    private val auditLogService: AuditLogService
) {

    @GetMapping
    fun getAllOddJobs(): List<OddJobBase> = oddJobBaseRepository.findAll()

    @PostMapping
    @Transactional
    fun saveOddJobBase(@ModelAttribute("player") player: Player,
                       @RequestBody @Valid oddJobBase: OddJobBase): OddJobBase {
        val lootBox = lootBoxRepository.findByIdOrNull(oddJobBase.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", oddJobBase.lootBoxId)
        if (lootBox.type != LootBoxType.ODD_JOB) {
            throw GeneralException(player, "Invalid odd job", "LootBox must be of type ODD_JOB")
        }
        return oddJobBaseRepository.save(oddJobBase).also {
            auditLogService.log(player, "Create or Update oddJobBase ${it.jobType.name} lvl ${it.level} #${it.id}", adminAction = true)
        }
    }
}