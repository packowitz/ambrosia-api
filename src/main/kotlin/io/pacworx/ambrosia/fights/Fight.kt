package io.pacworx.ambrosia.fights

import io.pacworx.ambrosia.io.pacworx.ambrosia.fights.stageconfig.FightStageConfig
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
data class Fight(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        var name: String,
        val serviceAccountId: Long
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "fight_id")
    @OrderBy("stage ASC")
    var stages: List<FightStage> = ArrayList()

    @ManyToOne
    @JoinColumn(name = "stage_config_id")
    lateinit var stageConfig: FightStageConfig
}

