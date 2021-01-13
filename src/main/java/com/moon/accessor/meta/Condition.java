package com.moon.accessor.meta;

import com.moon.accessor.ConditionType;
import com.moon.accessor.Conditional;
import com.moon.accessor.MatchingType;

/**
 * @author benshaoye
 */
public class Condition implements Conditional {

    private final TableField<?, ?, ? extends Table<?>> field;
    private final MatchingType matching;
    private final Object value;

    private boolean available = true;
    /**
     * “我”是被怎么关联的
     */
    private ConditionType type;

    Condition(
        TableField<?, ?, ? extends Table<?>> field, MatchingType matching, Object value
    ) {
        this.field = field;
        this.matching = matching;
        this.value = value;
    }

    TableField<?, ?, ? extends Table<?>> getField() { return field; }

    MatchingType getMatching() { return matching; }

    Object getValue() { return value; }

    ConditionType getType() { return type; }

    boolean isAvailable() { return available; }

    void setAvailable(boolean available) { this.available = available; }

    void setType(ConditionType type) { this.type = type; }

    @Override
    public Conditional and(Conditional condition) {
        return this;
    }

    @Override
    public Conditional or(Conditional condition) {
        return this;
    }

    @Override
    public Conditional on(boolean available) {
        return this;
    }
}
