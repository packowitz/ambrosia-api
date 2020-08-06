package io.pacworx.ambrosia.loot

import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional
import kotlin.math.max
import kotlin.math.min

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/loot")
class AdminLootController(private val gearLootRepository: GearLootRepository,
                          private val lootBoxRepository: LootBoxRepository,
                          private val auditLogService: AuditLogService) {

    @GetMapping("gear")
    fun getGearLoot(): List<GearLoot> = gearLootRepository.findAllByOrderByName()

    @PostMapping("gear")
    @Transactional
    fun saveGearLoot(@ModelAttribute("player") player: Player,
                     @RequestBody gearLoot: GearLoot): GearLoot {
        return gearLootRepository.save(gearLoot).also {
            auditLogService.log(player, "Saved gear loot ${it.name} #${it.id}", adminAction = true)
        }
    }

    @GetMapping("box")
    fun getLootBoxes(): List<LootBox> = lootBoxRepository.findAllByOrderByName()

    @PostMapping("box")
    @Transactional
    fun saveLootBox(@ModelAttribute("player") player: Player,
                    @RequestBody lootBox: LootBox): LootBox {
        if (lootBox.type.exactOneSlot && lootBox.items.any { it.slotNumber > 1 }) {
            throw GeneralException(player, "Invalid LootBox", "LootBox of type ${lootBox.type} must contain exactly 1 item slot")
        }
        if (lootBox.type.enforce100chance && lootBox.items.any { it.chance != 100 }) {
            throw GeneralException(player, "Invalid LootBox", "LootBox of type ${lootBox.type} must contain only items that are granted by 100%")
        }
        if (!lootBox.type.resourceRangeAllowed && lootBox.items.any { it.resourceFrom != it.resourceTo }) {
            throw GeneralException(player, "Invalid LootBox", "LootBox of type ${lootBox.type} does not allow ranges for resource loot items")
        }
        if (!lootBox.type.progressStatAllowed && lootBox.items.any { it.progressStat != null }) {
            throw GeneralException(player, "Invalid LootBox", "LootBox of type ${lootBox.type} must not contain items giving a progress bonus")
        }
        if (!lootBox.type.gearAllowed && lootBox.items.any { it.gearLootId != null }) {
            throw GeneralException(player, "Invalid LootBox", "LootBox of type ${lootBox.type} must not contain items containing a piece of gear")
        }
        if (lootBox.type.singleJewelTypeRequired && lootBox.items.any { it.getJewelTypes().size > 1 }) {
            throw GeneralException(player, "Invalid LootBox", "LootBox of type ${lootBox.type} must define a specific jewelType (not a list of)")
        }
        lootBox.items.forEach {item ->
            item.resourceType = item.takeIf { item.type == LootItemType.RESOURCE }?.resourceType
            item.resourceFrom = item.takeIf { item.type == LootItemType.RESOURCE }?.let { min(it.resourceFrom!!, it.resourceTo!!) }
            item.resourceTo = item.takeIf { item.type == LootItemType.RESOURCE }?.let { max(it.resourceFrom!!, it.resourceTo!!) }
            item.heroBaseId = item.takeIf { item.type == LootItemType.HERO }?.heroBaseId
            item.heroLevel = item.takeIf { item.type == LootItemType.HERO }?.heroLevel
            item.gearLootId = item.takeIf { item.type == LootItemType.GEAR }?.gearLootId
            item.jewelTypeNames = item.takeIf { item.type == LootItemType.JEWEL }?.jewelTypeNames
            item.jewelLevel = item.takeIf { item.type == LootItemType.JEWEL }?.jewelLevel
            item.vehicleBaseId = item.takeIf { item.type == LootItemType.VEHICLE }?.vehicleBaseId
            item.vehiclePartType = item.takeIf { item.type == LootItemType.VEHICLE_PART }?.vehiclePartType
            item.vehiclePartQuality = item.takeIf { item.type == LootItemType.VEHICLE_PART }?.vehiclePartQuality
            item.progressStat = item.takeIf { item.type == LootItemType.PROGRESS }?.progressStat
            item.progressStatBonus = item.takeIf { item.type == LootItemType.PROGRESS }?.progressStatBonus

            if (when (item.type) {
                LootItemType.RESOURCE -> item.resourceType == null || item.resourceFrom == null || item.resourceTo == null || item.resourceFrom!! > item.resourceTo!!
                LootItemType.HERO -> item.heroBaseId == null || item.heroLevel == null || item.heroLevel!! < 1 || item.heroLevel!! > 60
                LootItemType.GEAR -> item.gearLootId == null
                LootItemType.JEWEL -> item.jewelTypeNames == null || item.jewelLevel == null || item.jewelLevel!! < 1 || item.jewelLevel!! > 10
                LootItemType.VEHICLE -> item.vehicleBaseId == null
                LootItemType.VEHICLE_PART -> item.vehiclePartType == null || item.vehiclePartQuality == null
                LootItemType.PROGRESS -> item.progressStat == null || item.progressStatBonus == null
            }) {
                throw RuntimeException("Invalid LootItem at ${item.slotNumber} pos ${item.itemOrder}")
            }
        }
        return lootBoxRepository.save(lootBox).also {
            auditLogService.log(player, "Saved loot box ${it.name} #${it.id}", adminAction = true)
        }
    }
}
