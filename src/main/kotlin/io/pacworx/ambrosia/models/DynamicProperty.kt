package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.HeroStat
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyCategory
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyType
import javax.persistence.*

@Entity
data class DynamicProperty (@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                            val id: Long = 0,
                            @Enumerated(EnumType.STRING)
                            val category: PropertyCategory,
                            @Enumerated(EnumType.STRING)
                            val type: PropertyType,
                            val level: Int?,
                            @Enumerated(EnumType.STRING)
                            val stat: HeroStat?,
                            val value1: Int,
                            val value2: Int?)
