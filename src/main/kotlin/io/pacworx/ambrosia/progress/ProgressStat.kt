package io.pacworx.ambrosia.progress

enum class ProgressStat {
    PLAYER_XP {
        override fun apply(progress: Progress, bonus: Int) {
            progress.xp += bonus
        }
    },
    EXPEDITION_LEVEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.expeditionLevel += bonus
        }
    },
    EXPEDITION_SPEED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.expeditionSpeed += bonus
        }
    },
    NUMBER_ODD_JOBS {
        override fun apply(progress: Progress, bonus: Int) {
            progress.numberOddJobs += bonus
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
    GEAR_QUALITY_INCREASE {
        override fun apply(progress: Progress, bonus: Int) {
            progress.gearQualityIncrease += bonus
        }
    },
    HERO_TRAIN_LEVEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.maxTrainingLevel += bonus
        }
    },
    TRAINING_XP_BOOST {
        override fun apply(progress: Progress, bonus: Int) {
            progress.trainingXpBoost += bonus
        }
    },
    TRAINING_ASC_BOOST {
        override fun apply(progress: Progress, bonus: Int) {
            progress.trainingAscBoost += bonus
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
    SIMPLE_GENOMES_NEEDED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.simpleGenomesNeeded += bonus
        }
    },
    COMMON_GENOMES_NEEDED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.commonGenomesNeeded += bonus
        }
    },
    UNCOMMON_GENOMES_NEEDED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.uncommonGenomesNeeded += bonus
        }
    },
    RARE_GENOMES_NEEDED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.rareGenomesNeeded += bonus
        }
    },
    EPIC_GENOMES_NEEDED {
        override fun apply(progress: Progress, bonus: Int) {
            progress.epicGenomesNeeded += bonus
        }
    },
    SIMPLE_INCUBATION_UP_PER_MIL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.simpleIncubationUpPerMil += bonus
        }
    },
    COMMON_INCUBATION_UP_PER_MIL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.commonIncubationUpPerMil += bonus
        }
    },
    UNCOMMON_INCUBATION_UP_PER_MIL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.uncommonIncubationUpPerMil += bonus
        }
    },
    RARE_INCUBATION_UP_PER_MIL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.rareIncubationUpPerMil += bonus
        }
    },
    UNCOMMON_STARTING_LEVEL {
        override fun apply(progress: Progress, bonus: Int) {
            progress.uncommonStartingLevel += bonus
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
