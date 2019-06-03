package com.moon.notice;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class NoticeUtil {

    private NoticeUtil() { noInstanceError(); }

    public static NoticeParameters newParameters() { return new NoticeParametersImpl(); }

    public static NoticeParameter ofParam(String name, String value) { return NamedParameter.of(name, value); }
}
