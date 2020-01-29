package io.pacworx.ambrosia.loot

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
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
        return lootBoxRepository.save(lootBox)
    }
}
