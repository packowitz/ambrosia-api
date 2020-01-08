package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SimplePlayerMapRepository: JpaRepository<PlayerMapResolved, String> {

    @Query(value = """
        select pm.player_id || '_' || pm.map_id as id, 
            pm.map_id as map_id, 
            m.name as name,
            m.background as background,
            m.min_x as min_x,
            m.max_x as max_x,
            m.min_y as min_y,
            m.max_y as max_y
        from player_map pm join map m on pm.map_id = m.id
        where pm.player_id = :playerId
    """, nativeQuery = true)
    fun findAllByPlayerId(@Param("playerId") playerId: Long): List<PlayerMapResolved>
}

