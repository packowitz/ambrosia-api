package io.pacworx.ambrosia.story

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Story(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val trigger: StoryTrigger,
    val number: Int,
    val title: String?,
    val message: String,
    val buttonText: String,
    val leftPic: String?,
    val rightPic: String?
)
