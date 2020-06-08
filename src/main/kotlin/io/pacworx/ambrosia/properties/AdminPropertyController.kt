package io.pacworx.ambrosia.properties

import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/properties")
class AdminPropertyController(private val propertyService: PropertyService,
                              private val auditLogService: AuditLogService) {

    @PostMapping("type/{type}")
    @Transactional
    fun saveProperties(@ModelAttribute("player") player: Player,
                       @PathVariable("type") type: PropertyType,
                       @RequestBody properties: List<DynamicProperty>): List<DynamicProperty> {
        auditLogService.log(player, "Save properties ${type.name}", adminAction = true)
        return propertyService.upsertProperties(type, properties)
    }
}
