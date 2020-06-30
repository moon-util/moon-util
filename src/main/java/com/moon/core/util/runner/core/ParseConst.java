package com.moon.core.util.runner.core;

import com.moon.core.lang.SupportUtil;
import com.moon.core.lang.ref.IntAccessor;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
class ParseConst {
    private ParseConst() { noInstanceError(); }

    final static AsValuer parseStr(char[] chars, IntAccessor indexer, int endChar) {
        return DataConst.get(SupportUtil.parseStr(chars, indexer, endChar));
    }

    final static AsValuer parseNum(char[] chars, IntAccessor indexer, int len, int current) {
        return DataConst.get(SupportUtil.parseNum(chars, indexer, len, current));
    }
}
