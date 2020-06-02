package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.hero.Hero
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.vehicle.Vehicle
import org.springframework.http.HttpStatus

class HeroBusyException(val player: Player, val hero: Hero) : AmbrosiaException, RuntimeException("Hero cannot be used") {
    override val title: String = "Unable to use hero"
    override val message: String
        get() {
            var msg = "Hero ${hero.heroBase.name} is "
            if (hero.missionId != null) {
                msg += "on mission"
            }
            return msg
        }
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
