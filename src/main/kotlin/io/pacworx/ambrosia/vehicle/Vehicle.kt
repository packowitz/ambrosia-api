package io.pacworx.ambrosia.vehicle

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class Vehicle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @ManyToOne
    @JoinColumn(name = "base_vehicle_id")
    val baseVehicle: VehicleBase,
    var level: Int = 1,
    var upgradeTriggered: Boolean = false,
    var slot: Int? = null,
    var missionId: Long? = null,
    var playerExpeditionId: Long? = null,
    @OneToOne
    @JoinColumn(name = "engine_part_id")
    var engine: VehiclePart? = null,
    @OneToOne
    @JoinColumn(name = "frame_part_id")
    var frame: VehiclePart? = null,
    @OneToOne
    @JoinColumn(name = "computer_part_id")
    var computer: VehiclePart? = null,
    @OneToOne
    @JoinColumn(name = "special_part1id")
    var specialPart1: VehiclePart? = null,
    @OneToOne
    @JoinColumn(name = "special_part2id")
    var specialPart2: VehiclePart? = null,
    @OneToOne
    @JoinColumn(name = "special_part3id")
    var specialPart3: VehiclePart? = null
) {

    @JsonIgnore
    fun getAllParts(): List<VehiclePart> {
        return listOfNotNull(engine, frame, computer, specialPart1, specialPart2, specialPart3)
    }

    @JsonIgnore
    fun isAvailable(): Boolean {
        return slot != null &&
            missionId == null &&
            playerExpeditionId == null &&
            !upgradeTriggered
    }
}
