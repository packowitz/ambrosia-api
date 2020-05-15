package io.pacworx.ambrosia.story

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class StoryPlaceholder(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val identifier: String,
    val red: String,
    val green: String,
    val blue: String
)
