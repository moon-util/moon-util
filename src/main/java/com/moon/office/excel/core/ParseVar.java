package com.moon.office.excel.core;

import com.moon.core.lang.BooleanUtil;
import com.moon.core.lang.SupportUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.IntAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
final class ParseVar {
    private ParseVar() {
        ThrowUtil.noInstanceError();
    }

    private final static char YUAN_LEFT = '(';
    private final static char YUAN_RIGHT = ')';
    private final static char COMMA = ',';
    private final static char COLON = ':';
    private final static char LETTER_I = 'i';
    private final static char LETTER_N = 'n';
    private final static char EQ = '=';

    final static VarSetter parseSetVar(String var) {
        char[] chars = var.trim().toCharArray();
        final int length = chars.length;
        return length == 0 ? VarSetterNo.NO :
            parseSetVar(chars, IntAccessor.of(), length);
    }

    private final static VarSetter parseSetVar(char[] chars, IntAccessor indexer, int len) {
        String[] keys = parseKeys(chars, indexer, len);
        int type = parseType(chars, indexer, len);
        String expression = new String(chars, indexer.get(), len - indexer.get());
        return type == EQ ? new VarSetterEq(keys, expression) : new VarSetterIn(keys, expression);
    }

    private final static int parseType(char[] chars, IntAccessor indexer, int len) {
        int curr = nextChar(chars, indexer, len);
        switch (curr) {
            case LETTER_I:
                BooleanUtil.requireTrue(chars[indexer.getAndIncrement()] == LETTER_N);
                return COLON;
            case COLON:
            case EQ:
                return curr;
            default:
                return SupportUtil.throwErr(chars, indexer);
        }
    }

    final static String[] parseKeys(char[] chars, IntAccessor indexer, int len) {
        int curr = nextChar(chars, indexer, len);
        if (SupportUtil.isVar(curr)) {
            return new String[]{SupportUtil.parseVar(chars, indexer, len, curr)};
        } else if (curr == YUAN_LEFT) {
            List<String> KEYS = new ArrayList<>();
            for (; indexer.get() < len; ) {
                curr = nextChar(chars, indexer, len);
                if (curr == YUAN_RIGHT) {
                    return KEYS.toArray(new String[KEYS.size()]);
                } else if (curr == COMMA) {
                    continue;
                } else {
                    BooleanUtil.requireTrue(SupportUtil.isVar(curr));
                    KEYS.add(SupportUtil.parseVar(chars, indexer, len, curr));
                }
            }
        }
        return SupportUtil.throwErr(chars, indexer);
    }

    private final static int nextChar(char[] chars, IntAccessor indexer, int len) {
        return SupportUtil.skipWhitespaces(chars, indexer, len);
    }
}
