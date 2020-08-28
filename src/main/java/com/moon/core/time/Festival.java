package com.moon.core.time;

import com.moon.core.enums.EnumDescriptor;

/**
 * @author moonsky
 */
public enum Festival implements EnumDescriptor {
    NEW_YEAR_DAY("元旦节","New Year's Day"),
    LABOR_DAY("劳动节","Labor day"),
    CHILDREN_DAY("儿童节","Children's day"),
    PARTY_BUILDING_DAY("建党节","Party Building Day"),
    ARMY_DAY("建军节","Army Day"),
    TEACHERS_DAY("教师节","Teachers' Day"),
    NATIONAL_DAY("国庆节","National Day"),
    ;

    private final String ChineseText;
    private final String EnglishText;

    Festival(String text, String EnglishText) {
        this.ChineseText = text;
        this.EnglishText = EnglishText;
    }

    public String getChineseText() {
        return ChineseText;
    }

    /**
     * 枚举信息
     *
     * @return 枚举信息
     */
    @Override
    public String getText() {
        return EnglishText;
    }
}
