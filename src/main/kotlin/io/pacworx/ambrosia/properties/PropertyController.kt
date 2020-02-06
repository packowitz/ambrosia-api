package io.pacworx.ambrosia.properties

import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("properties")
class PropertyController(private val propertyService: PropertyService) {

    @GetMapping("type/{type}")
    fun getPropertiesByType(@PathVariable("type") type: PropertyType): List<DynamicProperty> {
        return propertyService.getAllProperties(type)
    }

    @GetMapping("category/{category}")
    fun getPropertiesByCategory(@PathVariable("category") category: PropertyCategory): Map<String, List<DynamicProperty>> {
        return PropertyType.values().asList().filter { it.category == category }.map { it.name to propertyService.getAllProperties(it) }.toMap()
    }


}
