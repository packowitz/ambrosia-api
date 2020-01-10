package io.pacworx.ambrosia.fights

import org.springframework.data.jpa.repository.JpaRepository

interface FightRepository: JpaRepository<Fight, Long>
