package io.pacworx.ambrosia.buildings.merchant

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/merchant/item")
class AdminMerchantController(
    private val merchantItemRepository: MerchantItemRepository,
    private val lootBoxRepository: LootBoxRepository,
    private val auditLogService: AuditLogService
) {

    @GetMapping
    fun getAllOddJobs(): List<MerchantItem> = merchantItemRepository.findAllByOrderByMerchantLevelDescSortOrderAsc()

    @PostMapping
    @Transactional
    fun saveOddJobBase(@ModelAttribute("player") player: Player,
                       @RequestBody @Valid merchantItem: MerchantItem): MerchantItem {
        val lootBox = lootBoxRepository.findByIdOrNull(merchantItem.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", merchantItem.lootBoxId)
        if (lootBox.items.any { it.slotNumber > 1 }) {
            throw GeneralException(player, "Invalid merchant item", "Loot must contain exactly 1 item slot")
        }
        if (lootBox.items.any { it.progressStat != null }) {
            throw GeneralException(player, "Invalid merchant item", "Loot must not contain a progress stat")
        }
        return merchantItemRepository.save(merchantItem).also {
            auditLogService.log(player, "Create or Update merchant item #${it.id}", adminAction = true)
        }
    }
}