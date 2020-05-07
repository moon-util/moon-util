package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableListable;
import org.apache.poi.ss.usermodel.Cell;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public abstract class Property implements Serializable, HeadSortable {

    private final static String[] EMPTY = {};

    protected final String name;

    protected final Marked<Method> atMethod;

    protected Marked<Field> atField;

    protected PropertiesGroup group;

    protected Property(String name, Marked<Method> atMethod) {
        this.atMethod = atMethod;
        this.name = name;
    }

    /*
     * tests
     */

    public boolean isDefined() { return isDataColumn() || isDataFlatten(); }

    public boolean isUndefined() { return !isDefined(); }

    public boolean isOnlyIndexer() { return hasIndexer() && isUndefined(); }

    public boolean hasIndexer() { return getIndexer() != null; }

    public boolean isDataFlatten() { return getFlatten() != null; }

    public boolean isDataColumn() { return getColumn() != null; }

    public boolean isCanListable() {
        return atMethod == null ? (atField != null && atField.isCanListable()) : atMethod.isCanListable();
    }

    public boolean isIterated() {
        return atMethod == null ? (atField != null && atField.isIterated()) : atMethod.isIterated();
    }

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

    @SafeVarargs
    protected static <T> boolean isEmpty(T... arr) { return arr == null || arr.length == 0; }

    protected String[] defaultLabelsIfEmpty(String[] labels) { return isEmpty(labels) ? getOtherwiseLabels() : labels; }

    protected static String[] ensureNonNull(String[] labels) { return labels == null ? EMPTY : labels; }

    public String[] getHeadLabels() {
        String[] labels = gotIfNonNull(Marked::getHeadLabels);
        return isDataFlatten() ? ensureNonNull(labels) : defaultLabelsIfEmpty(labels);
    }

    public int getRowsLength() {
        int length = getHeadLabels().length;
        PropertiesGroup group = getGroup();
        return group == null ? length : length + group.getMaxRowsLength();
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getOrder() {
        TableColumnFlatten flat = getFlatten();
        TableColumn column = getColumn();
        return flat == null ? column.order() : flat.order();
    }

    public String getHeadLabelAsIndexer() { return gotIfNonNull(Marked::getHeadLabelAsIndexer); }

    public TableListable getListable() { return gotIfNonNull(Marked::getListable); }

    public TableIndexer getIndexer() { return gotIfNonNull(Marked::getIndexer); }

    public TableColumnFlatten getFlatten() { return gotIfNonNull(Marked::getFlatten); }

    public TableColumn getColumn() { return gotIfNonNull(Marked::getColumn); }

    public PropertiesGroup getGroup() { return gotIfNonNull(Marked::getChildren); }

    public Class getPropertyType() { return gotIfNonNull(Marked::getPropertyType); }

    protected void afterSetField() {}

    public void exec(Object data, Cell cell) { }

    /*
     * setters
     */

    void setAboutField(Marked<Field> atField) {
        if (this.atField == null) {
            this.atField = atField;
            SupportUtil.requireNotDuplicated(getColumn(), getFlatten(), getName());
            afterSetField();
        }
    }
}
