package io.pacworx.ambrosia.vehicle

enum class PartType(val slot: VehicleSlot) {
    ENGINE(VehicleSlot.ENGINE),
    FRAME(VehicleSlot.FRAME),
    COMPUTER(VehicleSlot.COMPUTER),
    SMOKE_BOMB(VehicleSlot.SPECIAL),
    RAILGUN(VehicleSlot.SPECIAL),
    SNIPER_SCOPE(VehicleSlot.SPECIAL),
    MEDI_KIT(VehicleSlot.SPECIAL),
    REPAIR_KIT(VehicleSlot.SPECIAL),
    EXTRA_ARMOR(VehicleSlot.SPECIAL),
    NIGHT_VISION(VehicleSlot.SPECIAL),
    MAGNETIC_SHIELD(VehicleSlot.SPECIAL),
    MISSILE_DEFENSE(VehicleSlot.SPECIAL)
}
