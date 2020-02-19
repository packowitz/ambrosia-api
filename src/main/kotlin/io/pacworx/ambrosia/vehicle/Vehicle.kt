package io.pacworx.ambrosia.vehicle

import javax.persistence.*

@Entity
data class Vehicle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @ManyToOne
    @JoinColumn(name = "base_vehicle_id")
    val baseVehicle: VehicleBase,
    val level: Int = 1,
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
)
