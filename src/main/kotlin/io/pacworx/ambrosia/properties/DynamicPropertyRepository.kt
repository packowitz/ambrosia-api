package io.pacworx.ambrosia.properties

import org.springframework.data.jpa.repository.JpaRepository

interface DynamicPropertyRepository : JpaRepository<DynamicProperty, Long> {

    fun findAllByTypeAndVersionOrderByLevelAscValue1Asc(type: PropertyType, version: Int): List<DynamicProperty>

    fun findAllByTypeOrderByLevelAscValue1Asc(type: PropertyType): List<DynamicProperty>
}
