package io.pacworx.ambrosia.io.pacworx.ambrosia.services

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroBaseRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class HeroService(val heroBaseRepository: HeroBaseRepository,
                  val heroRepository: HeroRepository) {

    fun recruitHero(simpleChance: Double? = null,
                    commonChance: Double? = null,
                    uncommonChance: Double? = null,
                    rareChance: Double? = null,
                    epicChance: Double? = null,
                    default: Rarity): Hero {
        var chance = Random.nextDouble()
        var rarity: Rarity? = null
        epicChance?.let {
            if (chance < it) {
                rarity = Rarity.EPIC
            } else {
                chance -= it
            }
        }
        rareChance?.takeIf { rarity == null }?.let {
            if (chance < it) {
                rarity = Rarity.RARE
            } else {
                chance -= it
            }
        }
        uncommonChance?.takeIf { rarity == null }?.let {
            if (chance < it) {
                rarity = Rarity.UNCOMMON
            } else {
                chance -= it
            }
        }
        commonChance?.takeIf { rarity == null }?.let {
            if (chance < it) {
                rarity = Rarity.COMMON
            }
        }

        return heroBaseRepository.findAllByRarityAndRecruitableIsTrue(rarity?.let { it } ?: default).random().let {
            val hero = Hero(1L, it)
            heroRepository.save(hero)
            hero
        }
    }
}