package io.pacworx.ambrosia.story

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryPlaceholderRepository : JpaRepository<StoryPlaceholder, Long>
