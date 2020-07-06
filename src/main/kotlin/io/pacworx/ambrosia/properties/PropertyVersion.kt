package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class PropertyVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val propertyType: PropertyType,
    val version: Int,
    @JsonIgnore
    var active: Boolean
)