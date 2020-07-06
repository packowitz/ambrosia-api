package io.pacworx.ambrosia.properties

import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("properties")
class PropertyController(
    private val propertyService: PropertyService,
    private val propertyVersionRepository: PropertyVersionRepository
) {

    @GetMapping("versions")
    fun getVersion(): List<PropertyVersion> = propertyVersionRepository.findAllByActiveIsTrue()

    @GetMapping("category/{category}/types")
    fun getPropertyTypes(@PathVariable("category") category: PropertyCategory): List<String> {
        return PropertyType.values().filter { it.category == category }.map { it.name }
    }

    @GetMapping("category/types/{categories}")
    fun getCategoryTypes(@PathVariable("categories") categories: List<PropertyCategory>): List<String> {
        return PropertyType.values().filter { it.category in categories }.map { it.name }
    }

    @GetMapping("type/{type}/v/{version}")
    fun getPropertiesByType(@PathVariable("type") type: PropertyType,
                            @PathVariable("version") version: Int): List<DynamicProperty> {
        return propertyService.getProperties(Pair(type, version))
    }

    @GetMapping("type/{type}")
    fun getPropertiesByType(@PathVariable("type") type: PropertyType): List<DynamicProperty> {
        return propertyService.getProperties(type)
    }

    @GetMapping("type/{type}/{level}")
    fun getPropertiesByTypeAndLevel(
        @PathVariable("type") type: PropertyType,
        @PathVariable("level") level: Int
    ): List<DynamicProperty> {
        return propertyService.getProperties(type, level)
    }

    @GetMapping("category/{category}")
    fun getPropertiesByCategory(@PathVariable("category") category: PropertyCategory): List<DynamicProperty> {
        return PropertyType.values().asList().find { it.category == category }?.let { propertyService.getProperties(it) }
            ?: throw RuntimeException("Unknown property category")
    }

    @GetMapping("categories/{categories}")
    fun getPropertiesByCategories(@PathVariable("categories") categories: List<PropertyCategory>): List<DynamicProperty> {
        return PropertyType.values().asList().filter { it.category in categories }.map { propertyService.getProperties(it) }.flatten()
    }

}
