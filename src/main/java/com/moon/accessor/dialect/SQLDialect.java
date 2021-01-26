package com.moon.accessor.dialect;

/**
 * @author benshaoye
 */
public enum SQLDialect {
    /**
     * default
     */
    DEFAULT(Keywords.DEFAULT),
    /**
     * MySQL
     */
    MySQL(Keywords.MySQL),
    ;

    public final Keywords keywords;

    SQLDialect(Keywords keywords) { this.keywords = keywords; }

    /**
     * 转换为可用的名称，以关键字为列名或其他名称的符合通常需要处理一下，如：
     * <p>
     * MySQL 中若以{@code select}为列名，查询应使用{@code `select`}，
     * <p>
     * 不建议这样使用关键字作为列名
     *
     * @param name   原始列名
     * @param policy 当是关键字时也不转换，如果确保所有字段名等符号
     *               都不是关键字时，这个字段可传{@code true}，否则先
     *               判断 name 是否是关键字，并做相应处理后返回
     *
     * @return 可用列名
     *
     * @see Keywords#asAvailableName(String, KeywordPolicy)
     */
    public String asAvailableName(String name, KeywordPolicy policy) {
        return keywords.asAvailableName(name, policy);
    }
}
