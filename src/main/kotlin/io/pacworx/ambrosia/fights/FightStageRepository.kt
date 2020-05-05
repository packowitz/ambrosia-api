package io.pacworx.ambrosia.fights

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FightStageRepository: JpaRepository<FightStage, Long> {

    @Query("select fight_id from fight_stage where hero1id = :heroId or hero2id = :heroId or hero3id = :heroId or hero4id = :heroId", nativeQuery = true)
    fun findStagesContainingHero(@Param("heroId") heroId: Long): List<Long>
}