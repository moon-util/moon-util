package com.moon.data.identifier;

import com.moon.core.lang.LongUtil;
import com.moon.data.IdentifierGenerator;

/**
 * @author moonsky
 */
public final class ShortlyStringSequenceIdentifier extends StringSequenceIdentifier
    implements IdentifierGenerator<String, Object> {

    private final static int RADIX = 36;

    private final int radix;

    public ShortlyStringSequenceIdentifier() { this(RADIX); }

    public ShortlyStringSequenceIdentifier(int radix) { this.radix = radix; }

    public ShortlyStringSequenceIdentifier(long workerId, long datacenterId) {
        this(RADIX, workerId, datacenterId);
    }

    public ShortlyStringSequenceIdentifier(int radix, long workerId, long datacenterId) {
        super(workerId, datacenterId);
        this.radix = radix;
    }

    @Override
    public String generateId(Object e, Object i) {
        return LongUtil.toString(originGenerateId(e, i), radix);
    }
}
