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
abstract class Defined implements Serializable, HeadSortable {

    private final static String[] EMPTY = {};

    protected final String name;

    protected final Marked<Method> atMethod;

    protected Marked<Field> atField;

    protected ParsedDetail childrenGroup;

    protected Defined(String name, Marked<Method> atMethod) {
        this.atMethod = atMethod;
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
        return otherwiseLabels == null ? (otherwiseLabels = new String[]{getName()}) : otherwiseLabels;
    }

    Method getAtMethod() { return atMethod == null ? null : atMethod.getMember(); }

    Field getAtField() { return atField == null ? null : atField.getMember(); }

    private <T> T gotIfNonNull(Function<Marked, T> getter) {
        Marked<Method> onMethod = this.atMethod;
        Marked<Field> onField = this.atField;
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
        if (atMethod == null) {
            return atField.isCanListable();
        } else {
            return atMethod.isCanListable();
        }
    }

    protected static <T> boolean isEmpty(T... arr) { return arr == null || arr.length == 0; }

    protected String[] defaultLabelsIfEmpty(String[] labels) { return isEmpty(labels) ? getOtherwiseLabels() : labels; }

    protected static String[] ensureNonNull(String[] labels) { return labels == null ? EMPTY : labels; }

    public String[] getHeadLabels() {
        String[] labels = gotIfNonNull(Marked::getHeadLabels);
        return isFlatColumn() ? ensureNonNull(labels) : defaultLabelsIfEmpty(labels);
    }

    public int getRowsLength() {
        int length = getHeadLabels().length;
        ParsedDetail group = getChildrenGroup();
        return group == null ? length : length + group.getMaxRowsLength();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        DataColumnFlatten flat = getFlatten();
        DataColumn column = getColumn();
        return flat == null ? column.order() : flat.order();
    }

    public String getHeadLabelAsIndexer() { return gotIfNonNull(Marked::getHeadLabelAsIndexer); }

    public DataListable getListable() { return gotIfNonNull(Marked::getListable); }

    public DataIndexer getIndexer() { return gotIfNonNull(Marked::getIndexer); }

    public DataColumnFlatten getFlatten() { return gotIfNonNull(Marked::getFlatten); }

    public DataColumn getColumn() { return gotIfNonNull(Marked::getColumn); }

    public ParsedDetail getChildrenGroup() { return gotIfNonNull(Marked::getChildren); }

    public Class getPropertyType() { return gotIfNonNull(Marked::getPropertyType); }

    /*
     * setters
     */

    void setAboutField(Marked<Field> atField) {
        this.atField = atField;
        ParamUtil.requireNotDuplicated(getColumn(), getFlatten(), getName());
    }
}
