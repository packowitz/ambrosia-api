package io.pacworx.ambrosia.buildings.blackmarket

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
@RequestMapping("admin/blackmarket/item")
class AdminBlackMarketController(
    private val blackMarketItemRepository: BlackMarketItemRepository,
    private val lootBoxRepository: LootBoxRepository,
    private val auditLogService: AuditLogService
) {

    @GetMapping
    fun getBlackMarketItems(): List<BlackMarketItem> = blackMarketItemRepository.findAllByOrderBySortOrderAsc()

    @PostMapping
    @Transactional
    fun saveOddJobBase(@ModelAttribute("player") player: Player,
                       @RequestBody @Valid blackMarketItem: BlackMarketItem): BlackMarketItem {
        val lootBox = lootBoxRepository.findByIdOrNull(blackMarketItem.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", blackMarketItem.lootBoxId)
        if (lootBox.type != LootBoxType.BLACK_MARKET) {
            throw GeneralException(player, "Invalid merchant item", "Loot must be of type BLACK_MARKET")
        }
        return blackMarketItemRepository.save(blackMarketItem).also {
            auditLogService.log(player, "Create or Update blackmarket item #${it.id}", adminAction = true)
        }
    }
}