package io.pacworx.ambrosia.progress

enum class ProgressStat {
    PLAYER_XP {
        override fun apply(progress: Progress, bonus: Int) {
            progress.xp += bonus
        }
    },
    GARAGE_SLOT {
        override fun apply(progress: Progress, bonus: Int) {
            progress.garageSlots += bonus
        }
    },
    MISSION_SPEED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.offlineBattleSpeed += bonus
        }
    },
    MISSION_MAX_BATTLES {
        override fun apply(progress: Progress, bonus: Int) {
            progress.maxOfflineBattlesPerMission += bonus
        }
    },
    BUILDER_QUEUE {
        override fun apply(progress: Progress, bonus: Int) {
            progress.builderQueueLength += bonus
        }
    },
    BUILDER_SPEED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.builderSpeed += bonus
        }
    },
    BARRACKS_SIZE {
        override fun apply(progress: Progress, bonus: Int) {
            progress.barrackSize += bonus
        }
    },
    HERO_TRAIN_LEVEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.maxTrainingLevel += bonus
        }
    },
    VEHICLE_UPGRADE_LEVEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.vehicleUpgradeLevel += bonus
        }
    },
    INCUBATORS {
        override fun apply(progress: Progress, bonus: Int) {
            progress.incubators += bonus
        }
    },
    LAB_SPEED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.labSpeed += bonus
        }
    },
    JEWEL_UPGRADE_LEVEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.maxJewelUpgradingLevel += bonus
        }
    },
    GEAR_MOD_RARITY {
        override fun apply(progress: Progress, bonus: Int) {
            progress.gearModificationRarity += bonus
        }
    },
    GEAR_MOD_SPEED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.gearModificationSpeed += bonus
        }
    },
    GEAR_BREAKDOWN_RARITY {
        override fun apply(progress: Progress, bonus: Int) {
            progress.gearBreakDownRarity += bonus
        }
    },
    GEAR_BREAKDOWN_RESOURCES {
        override fun apply(progress: Progress, bonus: Int) {
            progress.gearBreakDownResourcesInc += bonus
        }
    },
    REROLL_GEAR_QUALITY {
        override fun apply(progress: Progress, bonus: Int) {
            progress.reRollGearQualityEnabled = bonus > 0
        }
    },
    REROLL_GEAR_STAT {
        override fun apply(progress: Progress, bonus: Int) {
            progress.reRollGearStatEnabled = bonus > 0
        }
    },
    INC_GEAR_RARITY {
        override fun apply(progress: Progress, bonus: Int) {
            progress.incGearRarityEnabled = bonus > 0
        }
    },
    REROLL_GEAR_JEWEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.reRollGearJewelEnabled = bonus > 0
        }
    },
    ADD_GEAR_JEWEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.addGearJewelEnabled = bonus > 0
        }
    },
    ADD_GEAR_SPECIAL_JEWEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.addGearSpecialJewelEnabled = bonus > 0
        }
    };

    abstract fun apply(progress: Progress, bonus: Int)
}
