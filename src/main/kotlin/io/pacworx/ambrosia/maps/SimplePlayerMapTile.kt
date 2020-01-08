package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class SimplePlayerMapTile(
    @Id val id: String,
    val mapId: Long,
    @Column(name = "pos_x") val posX: Int,
    @Column(name = "pos_y") val posY: Int,
    val dungeonId: Long? = null,
    val fightRepeatable: Boolean = false,
    var discovered: Boolean = false,
    var victoriousFight: Boolean = false
)

@Repository
interface SimplePlayerMapTileRepository: JpaRepository<SimplePlayerMapTile, String> {
    @Query(value = """
        select pm.player_id || '_' || pm.map_id || '_' || pmt.pos_x || '_' || pmt.pos_y as id,
            pm.map_id as map_id,
            pmt.pos_x as pos_x,
            pmt.pos_y as pos_y,
            mt.dungeon_id as dungeon_id,
            mt.fight_repeatable as fight_repeatable,
            pmt.discovered as discovered,
            pmt.victorious_fight as victorious_fight
        from player_map pm
        join player_map_tile pmt on pm.id = pmt.player_map_id
        join map_tile mt on pm.map_id = mt.map_id and pmt.pos_x = mt.pos_x and pmt.pos_y = mt.pos_y
        where pm.player_id = :playerId and pm.map_id = :mapId and pmt.pos_x = :posX and pmt.pos_y = :posY
    """, nativeQuery = true)
    fun findPlayerMapTile(@Param("playerId") playerId: Long,
                          @Param("mapId") mapId: Long,
                          @Param("posX") posX: Int,
                          @Param("posY") posY: Int): SimplePlayerMapTile?
}
