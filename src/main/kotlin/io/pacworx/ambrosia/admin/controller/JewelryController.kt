package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Jewelry
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.JewelryRepository
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/jewelry")
class JewelryController(val jewelryRepository: JewelryRepository) {

    @GetMapping("")
    fun getGears(): List<Jewelry> = jewelryRepository.findAll()

    @PostMapping("open/{type}")
    fun openJewel(@PathVariable type: String): Jewelry {
        //type can be the type of core to open. ignored for now
        val type: JewelType = JewelType.values().toList().random()
        val jewelry = jewelryRepository.findByPlayerIdAndType(1, type) ?: Jewelry(playerId = 1, type = type)
        jewelry.lvl1++
        jewelryRepository.save(jewelry)
        return jewelry
    }

    @PostMapping("merge/{type}/{lvl}")
    @Transactional
    fun mergeJewels(@PathVariable type: JewelType, @PathVariable lvl: Int): Jewelry {
        if (lvl == 10) {
            throw RuntimeException("You cannot merge jewels of level 10 as it is already highest level")
        }
        val jewelry = jewelryRepository.findByPlayerIdAndType(1, type)
        if (jewelry == null || jewelry.getAmount(lvl) < 4) {
            throw RuntimeException("You don't have enough jewels of $type $lvl* to merge them into a higher level")
        }
        jewelry.increaseAmount(lvl, -4)
        jewelry.increaseAmount((lvl + 1), 1)
        return jewelry
    }

}