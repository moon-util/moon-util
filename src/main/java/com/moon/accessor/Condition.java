package com.moon.accessor;

/**
 * @author benshaoye
 */
public interface Condition {

    Condition and(Condition condition);

    Condition or(Condition condition);
}
