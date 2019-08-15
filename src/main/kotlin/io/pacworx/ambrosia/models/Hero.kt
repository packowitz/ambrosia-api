package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.GearType
import io.pacworx.ambrosia.models.HeroBase
import javax.persistence.*

@Entity
data class Hero(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @ManyToOne
    @JoinColumn(name = "hero_base_id")
    val heroBase: HeroBase,
    var stars: Int,
    var level: Int = 1,
    var xp: Int = 0,
    var maxXp: Int,
    var skill1: Int = 0,
    var skill2: Int = 0,
    var skill3: Int = 0,
    var skill4: Int = 0,
    var skill5: Int = 0,
    var skill6: Int = 0,
    var skill7: Int = 0,
    var ascLvl: Int = 0,
    var ascPoints: Int = 0,
    var ascPointsMax: Int,
    @OneToOne
    @JoinColumn(name = "weapon_id")
    var weapon: Gear? = null,
    @OneToOne
    @JoinColumn(name = "shield_id")
    var shield: Gear? = null,
    @OneToOne
    @JoinColumn(name = "helmet_id")
    var helmet: Gear? = null,
    @OneToOne
    @JoinColumn(name = "armor_id")
    var armor: Gear? = null,
    @OneToOne
    @JoinColumn(name = "pants_id")
    var pants: Gear? = null,
    @OneToOne
    @JoinColumn(name = "boots_id")
    var boots: Gear? = null
) {

    constructor(playerId: Long, heroBase: HeroBase) : this(
        playerId = playerId,
        heroBase = heroBase,
        stars = heroBase.rarity.stars,
        maxXp = 10000,
        ascPointsMax = 10000
    )

    fun getGear(type: GearType): Gear? = when (type) {
        GearType.WEAPON -> this.weapon
        GearType.SHIELD -> this.shield
        GearType.HELMET -> this.helmet
        GearType.ARMOR -> this.armor
        GearType.PANTS -> this.pants
        GearType.BOOTS -> this.pants
    }

    fun equip(gear: Gear): Gear? {
        val unequipped = getGear(gear.type)
        unequipped?.equippedTo = null
        when (gear.type) {
            GearType.WEAPON -> this.weapon = gear
            GearType.SHIELD -> this.shield = gear
            GearType.HELMET -> this.helmet = gear
            GearType.ARMOR -> this.armor = gear
            GearType.PANTS -> this.pants = gear
            GearType.BOOTS -> this.pants = gear
        }
        gear.equippedTo = this.id
        return unequipped
    }

    fun unequip(gearType: GearType): Gear? {
        val unequipped = getGear(gearType)
        unequipped?.equippedTo = null
        when (gearType) {
            GearType.WEAPON -> this.weapon = null
            GearType.SHIELD -> this.shield = null
            GearType.HELMET -> this.helmet = null
            GearType.ARMOR -> this.armor = null
            GearType.PANTS -> this.pants = null
            GearType.BOOTS -> this.pants = null
        }
        return unequipped
    }

}
