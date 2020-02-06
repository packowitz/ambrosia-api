package io.pacworx.ambrosia.properties

import org.springframework.data.jpa.repository.JpaRepository

interface DynamicPropertyRepository : JpaRepository<DynamicProperty, Long> {

    fun findAllByTypeOrderByLevelAscValue1Asc(type: PropertyType): List<DynamicProperty>
}
