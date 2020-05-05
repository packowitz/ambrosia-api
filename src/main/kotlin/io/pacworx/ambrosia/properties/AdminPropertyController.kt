package io.pacworx.ambrosia.properties

import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/properties")
class AdminPropertyController(private val propertyService: PropertyService) {

    @PostMapping("type/{type}")
    @Transactional
    fun saveProperties(@PathVariable("type") type: PropertyType, @RequestBody properties: List<DynamicProperty>): List<DynamicProperty> {
        return propertyService.upsertProperties(type, properties)
    }

    @DeleteMapping("type/{type}/{id}")
    @Transactional
    fun deleteProperties(@PathVariable("type") type: PropertyType, @PathVariable("id") id: Long): List<DynamicProperty> {
        propertyService.deleteProperty(type, id)
        return propertyService.getAllProperties(type)
    }
}
