package io.pacworx.ambrosia.loot

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
