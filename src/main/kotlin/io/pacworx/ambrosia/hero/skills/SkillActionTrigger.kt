package io.pacworx.ambrosia.hero.skills

enum class SkillActionTrigger(description: String) {
    ALWAYS("Triggers always. No value needed"),
    SKILL_LVL("Triggers on this skill level. Value needed. E.g. >2 to trigger on level greater than 2"),
    S1_LVL("Triggers on skill 1 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S2_LVL("Triggers on skill 2 level. Value needed. E.g. 3,4,5 to trigger on level 3, 4 and 5"),
    S3_LVL("Triggers on skill 3 level. Value needed. E.g. 1 to trigger only on level 1"),
    S4_LVL("Triggers on skill 4 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S5_LVL("Triggers on skill 5 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S6_LVL("Triggers on skill 6 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    S7_LVL("Triggers on skill 7 level. Value needed. E.g. 1,2 to trigger on level 1 and 2"),
    PREV_ACTION_PROCED("Triggers only when the previous step triggered and proced"),
    PREV_ACTION_NOT_PROCED("Triggers only when the previous step triggered and not proced"),
    ANY_CRIT_DMG("Triggers if at least one damage deal crits. No value needed"),
    DMG_OVER("Triggers if base damage (before armor) at this point is higher than the given value"),
    ASC_LVL("Triggers on asc lvl. Value 4,5,6 to trigger on asc lvl 4, 5 or 6. No value means any asc lvl higher than 0.")
}
