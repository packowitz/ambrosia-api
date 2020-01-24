package io.pacworx.ambrosia.fights.stageconfig

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FightStageConfigRepository: JpaRepository<FightStageConfig, Long> {

    fun findByDefaultConfigTrue(): FightStageConfig

    @Query(value = "update fight_stage_config set default_config = false where id <> :id", nativeQuery = true)
    @Modifying
    fun markDefaultConfig(@Param("id") configId: Long): Int
}
