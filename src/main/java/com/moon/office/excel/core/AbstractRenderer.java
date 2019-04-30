package com.moon.office.excel.core;

import com.moon.core.lang.SupportUtil;
import com.moon.core.util.DetectUtil;
import com.moon.core.util.runner.RunnerUtil;
import com.moon.core.util.function.IntBiConsumer;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

/**
 * @author benshaoye
 */
abstract class AbstractRenderer<T extends Annotation> implements CenterRenderer {

    protected final static int NOT_INIT = -1;
    protected final static int MINUS = '-';

    final T annotation;
    final CenterRenderer[] children;
    private final String[] delimiters;
    private final int length;
    final VarSetter setter;

    protected int skips = NOT_INIT;

    protected AbstractRenderer(T annotation, CenterRenderer[] children, String var, String[] delimiters) {
        this.annotation = annotation;
        this.delimiters = delimiters;
        this.children = children;
        this.length = delimiters.length;
        this.setter = ParseVar.parseSetVar(var);
    }

    public final String[] getDelimiters() {
        return delimiters;
    }

    protected final boolean isZero() {
        return length == 0;
    }

    public final T getAnnotation() {
        return annotation;
    }

    @Override
    public final CenterRenderer[] getChildren() {
        return children;
    }

    @Override
    public WorkCenterMap render(WorkCenterMap centerMap) {
        return setter.setVarAndRender(centerMap, this);
    }

    private final static IntBiConsumer<AbstractRenderer> skipsValueSetter
        = (renderer, value) -> renderer.skips = value;
    protected final static BiConsumer<AbstractRenderer, Boolean> whenSetter
        = (renderer, value) -> renderer.setWhen(value);

    protected void setWhen(Boolean value) {
    }

    private int whenMark = NOT_INIT;

    protected boolean getValue(WorkCenterMap centerMap, String expression, Boolean value, BiConsumer consumer) {
        if (value == null) {
            if (length == 0) {
                if (whenMark == NOT_INIT) {
                    String temp = expression.trim();
                    value = assertAndSyncSetValue(temp, consumer);
                    if (temp != null) {
                        return value;
                    }
                }
                return (Boolean) RunnerUtil.run(expression, centerMap);
            }
            if (whenMark == NOT_INIT) {
                String temp = OtherUtil.cutWrapped(expression, delimiters);
                value = assertAndSyncSetValue(temp, consumer);
                if (temp != null) {
                    return value;
                }
            }
            return (Boolean) RunnerUtil.parseRun(expression, delimiters, centerMap);
        } else {
            return value.booleanValue();
        }
    }

    private Boolean assertAndSyncSetValue(String temp, BiConsumer consumer) {
        Boolean value = null;
        if (OtherUtil.TRUE.equals(temp) || OtherUtil.FALSE.equals(temp)) {
            value = Boolean.valueOf(temp);
        }
        synchronized (this) {
            consumer.accept(this, value);
            whenMark = 0;
        }
        return value;
    }

    protected int getValue(WorkCenterMap centerMap, String expression, int value, IntBiConsumer consumer) {
        if (value > NOT_INIT) {
            return value;
        }
        if (length == 0) {
            if (value == NOT_INIT) {
                String temp = expression.trim();
                value = assertAndSyncSetValue(temp, expression, consumer);
                if (value > NOT_INIT) {
                    return value;
                }
            }
            return Math.max(((Number) RunnerUtil.run(expression)).intValue(), 0);
        }
        if (value == NOT_INIT) {
            String temp = OtherUtil.cutWrapped(expression, getDelimiters());
            value = assertAndSyncSetValue(temp, expression, consumer);
            if (value > NOT_INIT) {
                return value;
            }
        }
        return ((Number) RunnerUtil.parseRun(expression, getDelimiters(), centerMap)).intValue();
    }

    protected int getSkipsValue(WorkCenterMap centerMap, String skipExp) {
        return getValue(centerMap, skipExp, skips, skipsValueSetter);
    }

    private int assertAndSyncSetValue(String temp, String expression, IntBiConsumer consumer) {
        if (temp.length() < 1) {
            throw new IllegalArgumentException("SkipExp: " + expression
                + " 的值（或表达式返回值）必须是非负整数");
        }
        int value;
        if (DetectUtil.isInteger(temp) && SupportUtil.isNum(temp.charAt(0))) {
            value = Math.max(((Number) RunnerUtil.run(expression)).intValue(), 0);
        } else {
            value = -2;
        }
        synchronized (this) {
            consumer.accept(this, value);
        }
        return value;
    }
}
