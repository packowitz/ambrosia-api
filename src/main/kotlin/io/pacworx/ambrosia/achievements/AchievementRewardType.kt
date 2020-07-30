package io.pacworx.ambrosia.achievements

enum class AchievementRewardType {
    STEAM_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.steamUsed.toLong()
    },
    COGWHEELS_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.cogwheelsUsed.toLong()
    },
    TOKENS_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.tokensUsed.toLong()
    },
    COINS_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.coinsUsed.toLong()
    },
    RUBIES_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.rubiesUsed.toLong()
    },
    METAL_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.metalUsed.toLong()
    },
    IRON_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.ironUsed.toLong()
    },
    STEEL_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.steelUsed.toLong()
    },
    WOOD_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.woodUsed.toLong()
    },
    BROWN_COAL_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.brownCoalUsed.toLong()
    },
    BLACK_COAL_USED {
        override fun getAmount(achievements: Achievements): Long = achievements.blackCoalUsed.toLong()
    },
    SIMPLE_INCUBATIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.simpleIncubationsDone.toLong()
    },
    COMMON_INCUBATIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.commonIncubationsDone.toLong()
    },
    UNCOMMON_INCUBATIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.uncommonIncubationsDone.toLong()
    },
    RARE_INCUBATIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.rareIncubationsDone.toLong()
    },
    EPIC_INCUBATIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.epicIncubationsDone.toLong()
    },
    EXPEDITIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.expeditionsDone.toLong()
    },
    ODD_JOBS {
        override fun getAmount(achievements: Achievements): Long = achievements.oddJobsDone.toLong()
    },
    DAILY_ACTIVITY {
        override fun getAmount(achievements: Achievements): Long = achievements.dailyRewardsClaimed.toLong()
    },
    ACADEMY_XP {
        override fun getAmount(achievements: Achievements): Long = achievements.academyXpGained
    },
    ACADEMY_ASC {
        override fun getAmount(achievements: Achievements): Long = achievements.academyAscGained
    },
    MERCHANT_ITEMS_BOUGHT {
        override fun getAmount(achievements: Achievements): Long = achievements.merchantItemsBought.toLong()
    },
    MAP_TILES_DISCOVERED {
        override fun getAmount(achievements: Achievements): Long = achievements.mapTilesDiscovered.toLong()
    },
    GEAR_MODIFICATIONS {
        override fun getAmount(achievements: Achievements): Long = achievements.gearModified.toLong()
    },
    JEWELS_MERGED {
        override fun getAmount(achievements: Achievements): Long = achievements.jewelsMerged.toLong()
    },
    BUILDING_UPGRADES {
        override fun getAmount(achievements: Achievements): Long = achievements.buildingsUpgradesDone.toLong()
    },
    VEHICLE_UPGRADES {
        override fun getAmount(achievements: Achievements): Long = achievements.vehiclesUpgradesDone.toLong()
    },
    VEHICLE_PART_UPGRADES {
        override fun getAmount(achievements: Achievements): Long = achievements.vehiclePartUpgradesDone.toLong()
    },
    BUILDING_MIN_LEVEL {
        override fun getAmount(achievements: Achievements): Long = achievements.buildingMinLevel.toLong()
    },
    WOODEN_KEYS_COLLECTED {
        override fun getAmount(achievements: Achievements): Long = achievements.woodenKeysCollected
    },
    BRONZE_KEYS_COLLECTED {
        override fun getAmount(achievements: Achievements): Long = achievements.bronzeKeysCollected
    },
    SILVER_KEYS_COLLECTED {
        override fun getAmount(achievements: Achievements): Long = achievements.silverKeysCollected
    },
    GOLDEN_KEYS_COLLECTED {
        override fun getAmount(achievements: Achievements): Long = achievements.goldenKeysCollected
    },
    CHESTS_OPENED {
        override fun getAmount(achievements: Achievements): Long = achievements.chestsOpened
    };

    abstract fun getAmount(achievements: Achievements): Long
}