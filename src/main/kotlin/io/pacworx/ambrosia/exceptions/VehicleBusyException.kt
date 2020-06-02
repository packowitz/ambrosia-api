package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.vehicle.Vehicle
import org.springframework.http.HttpStatus

class VehicleBusyException(val player: Player, val vehicle: Vehicle) : AmbrosiaException, RuntimeException("Vehicle cannot be used") {
    override val title: String = "Unable to use vehicle"
    override val message: String
        get() {
            var msg = "Vehicle ${vehicle.baseVehicle.name} is "
            if (vehicle.slot == null) {
                msg += "not parked in a garage slot"
            } else if (vehicle.missionId != null) {
                msg += "on mission"
            } else if (vehicle.upgradeTriggered) {
                msg += "currently upgrading"
            }
            return msg
        }
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
