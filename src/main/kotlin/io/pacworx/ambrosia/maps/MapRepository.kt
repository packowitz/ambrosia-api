package io.pacworx.ambrosia.maps

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MapRepository: JpaRepository<Map, Long> {

    @Query(value = "update map set starting_map = false where id <> :id", nativeQuery = true)
    @Modifying
    fun markStartingMap(@Param("id") mapId: Long): Int

    fun getByStartingMapTrue(): Map
}