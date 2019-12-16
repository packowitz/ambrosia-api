package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyType
import org.springframework.data.jpa.repository.JpaRepository

interface DynamicPropertyRepository : JpaRepository<DynamicProperty, Long> {

    fun findAllByTypeOrderByLevelAscValue1Asc(type: PropertyType): List<DynamicProperty>
}
