package com.moon.more.excel;

import com.moon.more.excel.parse.ParseUtil;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */

class SheetUtil extends ParseUtil {

    private SheetUtil() { noInstanceError(); }

    static Renderer parseRenderer(Class targetClass) { return parse(targetClass); }
}
