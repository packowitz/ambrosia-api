package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TeamRepository : JpaRepository<Team, Long> {
    fun getAllByPlayerId(playerId: Long): List<Team>

    @Query(value = "select * from team where type = :type order by random() limit :limit", nativeQuery = true)
    fun getTeamsByType(@Param("type") type: String, @Param("limit") limit: Int = 10): List<Team>
}
