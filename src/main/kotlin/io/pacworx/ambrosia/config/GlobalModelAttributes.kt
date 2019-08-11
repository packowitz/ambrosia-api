package io.pacworx.ambrosia.io.pacworx.ambrosia.config

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class GlobalModelAttributes {

    @ModelAttribute("player")
    fun grabUser(request: HttpServletRequest): Player? {
        return request.getAttribute("player") as Player?
    }


}