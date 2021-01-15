package com.moon.accessor.annotation;

/**
 * @author benshaoye
 */
public enum CasePolicy {
    /**
     * 大写字母: UserInfo -> userinfo
     */
    UPPERCASE,
    /**
     * 小写字母: UserInfo -> USERINFO
     */
    LOWERCASE,
    /**
     * 小写字母并用下划线分割: UserInfo -> user_info
     */
    UNDERSCORE_LOWERCASE,
    /**
     * 大写字母并用下划线分割: UserInfo -> USER_INFO
     */
    UNDERSCORE_UPPERCASE,
    /**
     * 保持原样: UserInfo -> UserInfo
     */
    ORIGIN
}
