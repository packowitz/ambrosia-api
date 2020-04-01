package io.pacworx.ambrosia.fights

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.fights.environment.FightEnvironment
import io.pacworx.ambrosia.fights.stageconfig.FightStageConfig
import io.pacworx.ambrosia.loot.LootBox
import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.*

@Entity
data class Fight(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
    val serviceAccountId: Long,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val resourceType: ResourceType = ResourceType.STEAM,
    val costs: Int = 5,
    val xp: Int = 500,
    val level: Int = 5,
    val ascPoints: Int = 10,
    val travelDuration: Int = 30,
    val timePerTurn: Int = 1000,
    val maxTurnsPerStage: Int = 100
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "fight_id")
    @OrderBy("stage ASC")
    var stages: List<FightStage> = ArrayList()

    @ManyToOne
    @JoinColumn(name = "stage_config_id")
    lateinit var stageConfig: FightStageConfig

    @ManyToOne
    @JoinColumn(name = "environment_id")
    lateinit var environment: FightEnvironment

    @ManyToOne
    @JoinColumn(name = "loot_box_id")
    lateinit var lootBox: LootBox
}

