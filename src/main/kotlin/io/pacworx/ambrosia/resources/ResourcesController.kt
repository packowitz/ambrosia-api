package io.pacworx.ambrosia.resources

import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("resources")
class ResourcesController(private val resourcesService: ResourcesService) {

    @GetMapping
    @Transactional
    fun getResources(@ModelAttribute("player") player: Player): Resources {
        return resourcesService.getResources(player)
    }
}