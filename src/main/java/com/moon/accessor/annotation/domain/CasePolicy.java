package com.moon.accessor.annotation.domain;

/**
 * @author benshaoye
 */
public enum CasePolicy {
    /**
     * 小写字母并用下划线分割: UserInfo -> user_info
     */
    UNDERSCORE_LOWERCASE,
    /**
     * 大写字母并用下划线分割: UserInfo -> USER_INFO
     */
    UNDERSCORE_UPPERCASE,
    /**
     * 大写字母: UserInfo -> USERINFO
     */
    UPPERCASE,
    /**
     * 小写字母: UserInfo -> userinfo
     */
    LOWERCASE,
    /**
     * 下划线分割: UserInfo -> User_Info
     */
    UNDERSCORE,
    /**
     * 保持原样: UserInfo -> UserInfo
     */
    ORIGIN
}
