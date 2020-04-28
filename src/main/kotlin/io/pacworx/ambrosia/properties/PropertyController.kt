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
    fun getPropertiesByCategory(@PathVariable("category") category: PropertyCategory): List<DynamicProperty> {
        return PropertyType.values().asList().find { it.category == category }?.let { propertyService.getAllProperties(it) }
            ?: throw RuntimeException("Unknown property category")
    }

    @GetMapping("categories/{categories}")
    fun getPropertiesByCategories(@PathVariable("categories") categories: List<PropertyCategory>): List<DynamicProperty> {
        return PropertyType.values().asList().filter { it.category in categories }.map { propertyService.getAllProperties(it) }.flatten()
    }


}
