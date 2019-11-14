package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicProperty
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicPropertyRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/properties")
class AdminPropertyController(private val propertyService: PropertyService) {

    @Value("\${ambrosia.jwt-secret}")
    private lateinit var jwtSecret: String
    @Value("\${ambrosia.pw-salt-one}")
    private lateinit var pwSalt1: String
    @Value("\${ambrosia.pw-salt-two}")
    private lateinit var pwSalt2: String

    @PostMapping("type/{type}")
    fun saveProperties(@PathVariable("type") type: PropertyType, @RequestBody properties: List<DynamicProperty>): List<DynamicProperty> {
        return propertyService.upsertProperties(type, properties)
    }

    @DeleteMapping("type/{type}/{id}")
    fun deleteProperties(@PathVariable("type") type: PropertyType, @PathVariable("id") id: Long): List<DynamicProperty> {
        propertyService.deleteProperty(type, id)
        return propertyService.getAllProperties(type)
    }

    @GetMapping("hidden")
    fun getHidden(): Props {
        return Props(jwtSecret, pwSalt1, pwSalt2)
    }

    data class Props(val jwtSecret: String, val pwSalt1: String, val pwSalt2: String)
}
