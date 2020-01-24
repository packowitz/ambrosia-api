package io.pacworx.ambrosia.properties

import io.pacworx.ambrosia.enums.PropertyType
import io.pacworx.ambrosia.properties.DynamicProperty
import org.springframework.data.jpa.repository.JpaRepository

interface DynamicPropertyRepository : JpaRepository<DynamicProperty, Long> {

    fun findAllByTypeOrderByLevelAscValue1Asc(type: PropertyType): List<DynamicProperty>
}
