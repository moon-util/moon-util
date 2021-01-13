package com.moon.accessor;

/**
 * @author benshaoye
 */
public enum ConditionType {
    /**
     * sql 语句逻辑关系
     */
    AND(" AND "),OR(" OR ");

    public final String keyword;

    ConditionType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() { return keyword; }
}
