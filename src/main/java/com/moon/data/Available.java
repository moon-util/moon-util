package com.moon.data;

import com.moon.core.enums.EnumDescriptor;
import org.hibernate.annotations.Where;

/**
 * 是否有效、可用等
 *
 * @author moonsky
 */
public enum Available implements EnumDescriptor {
    /**
     * 否
     */
    NO,
    /**
     * 是
     */
    YES,
    ;
    /**
     * @see Where#clause()
     */
    public final static String WHERE_IDX = " available=1 ";
    /**
     * @see Where#clause()
     */
    public final static String WHERE_STR = " available='YES' ";

    @Override
    public String getText() { return name(); }
}
