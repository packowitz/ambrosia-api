package io.pacworx.ambrosia.hero

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface HeroRepository: JpaRepository<Hero, Long> {

    fun findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(playerId: Long): List<Hero>

    fun findAllByPlayerIdAndIdIn(playerId: Long, ids: List<Long>): List<Hero>

    fun findAllByHeroBase(heroBase: HeroBase): List<Hero>

    @Modifying
    @Query("update hero set max_xp = :maxXp where level = :level", nativeQuery = true)
    fun updateMaxXp(@Param("level") level: Int, @Param("maxXp") maxXp: Int)

    @Modifying
    @Query("update hero set asc_points_max = :maxAsc where asc_lvl = :ascLevel", nativeQuery = true)
    fun updateMaxAsc(@Param("ascLevel") ascLevel: Int, @Param("maxAsc") maxAsc: Int)

    fun deleteAllByIdIn(ids: List<Long>)
}
