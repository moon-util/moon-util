package com.moon.data;

import com.moon.core.lang.MoonKey;
import org.hibernate.annotations.Where;

/**
 * @author moonsky
 */
public final class RecordConst {

    private RecordConst() { }

    /**
     * moon.data.jpa.identifier
     *
     * @see com.moon.spring.data.jpa.id.RecordIdentifierGenerator
     */
    public final static String jpaIdentifierKey = MoonKey.Data.Jpa.IDENTIFIER;

    public final static String CACHE_GROUP = "IM_RECORD";

    /**
     * 逻辑删除用
     *
     * @see Where#clause()
     * @see Record#WHERE_IDX
     */
    public final static String WHERE_IDX = Available.WHERE_IDX;
    /**
     * 逻辑删除用
     *
     * @see Where#clause()
     * @see Record#WHERE_STR
     */
    public final static String WHERE_STR = Available.WHERE_STR;
}
