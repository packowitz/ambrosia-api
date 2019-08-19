package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicProperty
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicPropertyRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/properties")
class AdminPropertyController(private val propertyService: PropertyService) {

    @PostMapping("type/{type}")
    fun saveProperties(@PathVariable("type") type: PropertyType, @RequestBody properties: List<DynamicProperty>): List<DynamicProperty> {
        return propertyService.upsertProperties(type, properties)
    }

    @DeleteMapping("type/{type}/{id}")
    fun deleteProperties(@PathVariable("type") type: PropertyType, @PathVariable("id") id: Long): List<DynamicProperty> {
        propertyService.deleteProperty(type, id)
        return propertyService.getAllProperties(type)
    }
}
