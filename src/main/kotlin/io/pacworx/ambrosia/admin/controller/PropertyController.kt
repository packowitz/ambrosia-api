package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicProperty
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicPropertyRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/properties")
class PropertyController(private val propertyService: PropertyService) {

    @GetMapping("type/{type}")
    fun getProperties(@PathVariable("type") type: PropertyType): List<DynamicProperty> {
        return propertyService.getAllProperties(type)
    }

    @PostMapping("type/{type}")
    fun saveProperties(@RequestBody property: DynamicProperty): List<DynamicProperty> {
        return propertyService.upsertProperty(property)
    }

    @DeleteMapping("type/{type}/{id}")
    fun deleteProperties(@PathVariable("type") type: PropertyType, @PathVariable("id") id: Long) {
        return propertyService.deleteProperty(type, id)
    }
}
