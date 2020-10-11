package com.moon.data.identifier;

import com.moon.core.lang.LongUtil;
import com.moon.data.IdentifierGenerator;

/**
 * @author moonsky
 */
public final class ShortlyStringSequenceIdentifier extends StringSequenceIdentifier implements IdentifierGenerator<String, Object> {

    @Override
    protected String apply(Object e, Object i) {
        return LongUtil.toString(originGenerateId(e, i), 62);
    }
}
