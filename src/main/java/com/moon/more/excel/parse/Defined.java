package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;
import com.moon.more.excel.annotation.DataListable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author benshaoye
 */
abstract class Defined implements Serializable {

    private final static String[] EMPTY = {};

    protected final String name;

    protected final Marked<Method> onMethod;

    protected Marked<Field> onField;

    protected ParsedDetail childrenGroup;

    protected Defined(String name, Marked<Method> onMethod) {
        this.onMethod = onMethod;
        this.name = name;
    }

    /*
     * tests
     */

    public boolean isDefined() { return isOnlyColumn() || isFlatColumn(); }

    public boolean isUndefined() { return !isDefined(); }

    public boolean isOnlyIndexer() { return hasIndexer() && isUndefined(); }

    public boolean hasIndexer() { return getIndexer() != null; }

    public boolean isFlatColumn() { return getFlatten() != null; }

    public boolean isOnlyColumn() { return getColumn() != null; }

    /*
     * getters
     */

    private String[] otherwiseLabels;

    public String[] getOtherwiseLabels() {
        return otherwiseLabels == null ? (otherwiseLabels = new String[]{getPropertyName()}) : otherwiseLabels;
    }

    Method getMethod() { return onMethod == null ? null : onMethod.getMember(); }

    Field getField() { return onField == null ? null : onField.getMember(); }

    private <T> T gotIfNonNull(Function<Marked, T> getter) {
        Marked<Method> onMethod = this.onMethod;
        Marked<Field> onField = this.onField;
        T value = null;
        if (onMethod != null) {
            value = getter.apply(onMethod);
        }
        if (value == null && onField != null) {
            value = getter.apply(onField);
        }
        return value;
    }

    public boolean isCanListable() {
        if (onMethod == null) {
            return onField.isCanListable();
        } else {
            return onMethod.isCanListable();
        }
    }

    protected static <T> boolean isEmpty(T... arr) {
        return arr == null || arr.length == 0;
    }

    protected String[] defaultLabelsIfEmpty(String[] labels) {
        return isEmpty(labels) ? getOtherwiseLabels() : labels;
    }

    protected String[] ensureNonNull(String[] labels) {
        return labels == null ? EMPTY : labels;
    }

    public String[] getHeadLabels() {
        String[] labels = gotIfNonNull(Marked::getHeadLabels);
        return isFlatColumn() ? ensureNonNull(labels) : defaultLabelsIfEmpty(labels);
    }

    public int getRowsLength() {
        int length = getHeadLabels().length;
        ParsedDetail group = getChildrenGroup();
        return group == null ? length : length + group.getMaxRowsLength();
    }

    public int getOrder() {
        DataColumnFlatten flat = getFlatten();
        DataColumn column = getColumn();
        return flat == null ? column.order() : flat.order();
    }

    public String getHeadLabelAsIndexer() {
        return gotIfNonNull(Marked::getHeadLabelAsIndexer);
    }

    public DataListable getListable() {
        return gotIfNonNull(Marked::getListable);
    }

    public DataIndexer getIndexer() {
        return gotIfNonNull(Marked::getIndexer);
    }

    public DataColumnFlatten getFlatten() {
        return gotIfNonNull(Marked::getFlatten);
    }

    public DataColumn getColumn() { return gotIfNonNull(Marked::getColumn); }

    public ParsedDetail getChildrenGroup() { return gotIfNonNull(Marked::getChildren); }

    public Class getPropertyType() { return gotIfNonNull(Marked::getPropertyType); }

    public String getPropertyName() { return name; }

    /*
     * setters
     */

    void setAboutField(Marked<Field> onField) {
        this.onField = onField;
        ParamUtil.requireNotDuplicated(getColumn(), getFlatten(), getPropertyName());
    }
}
