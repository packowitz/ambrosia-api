package io.pacworx.ambrosia.loot

import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/loot")
class AdminLootController(private val gearLootRepository: GearLootRepository,
                          private val lootBoxRepository: LootBoxRepository) {

    @GetMapping("gear")
    fun getGearLoot(): List<GearLoot> = gearLootRepository.findAll()

    @PostMapping("gear")
    @Transactional
    fun saveGearLoot(@RequestBody gearLoot: GearLoot): GearLoot {
        return gearLootRepository.save(gearLoot)
    }

    @GetMapping("box")
    fun getLootBoxes(): List<LootBox> = lootBoxRepository.findAll()

    @PostMapping("box")
    @Transactional
    fun saveLootBox(@RequestBody lootBox: LootBox): LootBox {
        lootBox.items.forEach {
            when(it.type) {
                LootItemType.RESOURCE -> { it.heroBaseId = null; it.gearLootId = null }
                LootItemType.HERO -> { it.resourceAmount = null; it.resourceType = null; it.gearLootId = null }
                LootItemType.GEAR -> { it.resourceAmount = null; it.resourceType = null; it.heroBaseId = null }
            }
        }
        return lootBoxRepository.save(lootBox)
    }
}
