package io.pacworx.ambrosia.fights.environment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FightEnvironmentRepository: JpaRepository<FightEnvironment, Long> {

    fun findByDefaultEnvironmentTrue(): FightEnvironment

    @Query(value = "update fight_environment set default_environment = false where id <> :id", nativeQuery = true)
    @Modifying
    fun markDefaultEnvironment(@Param("id") envId: Long): Int
}
