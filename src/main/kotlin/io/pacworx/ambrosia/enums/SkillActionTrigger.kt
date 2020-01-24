package io.pacworx.ambrosia.enums

enum class SkillActionTrigger(description: String) {
    ALWAYS("Triggers always. No value needed"),
    S1_LVL("Triggers on skill 1 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S2_LVL("Triggers on skill 2 level. Value needed. E.g. 3,4,5 to trigger on level 3, 4 and 5"),
    S3_LVL("Triggers on skill 3 level. Value needed. E.g. 1 to trigger only on level 1"),
    S4_LVL("Triggers on skill 4 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S5_LVL("Triggers on skill 5 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S6_LVL("Triggers on skill 6 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S7_LVL("Triggers on skill 7 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    PREV_ACTION_PROCED("Triggers only when the previous step triggered and proced"),
    PREV_ACTION_NOT_PROCED("Triggers only when the previous step triggered and not proced"),
    ANY_CRIT_DMG("Triggers if at least damage crits. No value needed"),
    DMG_OVER("Triggers if base damage (before armor) at this point is higher than the given value"),
    ASCENDED("Triggers if the hero is fully ascended")
}