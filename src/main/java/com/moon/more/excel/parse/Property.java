package com.moon.more.excel.parse;

import com.moon.more.excel.Evaluator;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableListable;
import org.apache.poi.ss.usermodel.Cell;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * @author benshaoye
 */
abstract class Property implements Serializable, HeadSortable {

    protected final String name;

    protected final Annotated<Method> atMethod;

    protected Annotated<Field> atField;

    protected Property(String name, Annotated<Method> atMethod) {
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

    public boolean isIterated() {
        Annotated atM = this.atMethod;
        Annotated atF = this.atField;
        return (atM == null) ? (atF != null && atF.isIterated()) : atM.isIterated();
    }

    public boolean isChildrenIterated() {
        PropertiesGroup group = getGroup();
        return group != null && (group.isIterated() || group.isChildrenIterated());
    }

    /*
     * getters
     */

    Method getAtMethod() { return atMethod == null ? null : atMethod.getMember(); }

    Field getAtField() { return atField == null ? null : atField.getMember(); }

    private <T> T gotIfNonNull(Function<Annotated, T> getter) {
        Annotated<Method> onMethod = this.atMethod;
        Annotated<Field> onField = this.atField;
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

    @Override
    public String getName() { return name; }

    @Override
    public int getOrder() {
        TableColumnFlatten flat = getFlatten();
        TableColumn column = getColumn();
        return flat == null ? column.order() : flat.order();
    }

    public TableListable getListable() { return gotIfNonNull(Annotated::getListable); }

    public TableIndexer getIndexer() { return gotIfNonNull(Annotated::getIndexer); }

    public TableColumnFlatten getFlatten() { return gotIfNonNull(Annotated::getFlatten); }

    public TableColumn getColumn() { return gotIfNonNull(Annotated::getColumn); }

    public PropertiesGroup getGroup() { return gotIfNonNull(Annotated::getChildren); }

    public Class getPropertyType() { return gotIfNonNull(Annotated::getPropertyType); }

    public Type getGenericType() { return gotIfNonNull(Annotated::getGenericType); }

    public Class getActualClass() { return gotIfNonNull(Annotated::getActualClass); }

    public Class getDeclaringClass() { return gotIfNonNull(annotated -> annotated.getMember().getDeclaringClass()); }

    public Evaluator getEvaluator() { throw new UnsupportedOperationException(); }

    protected void afterSetField() {}

    /*
     * setters
     */

    void setAboutField(Annotated<Field> atField) {
        if (this.atField == null) {
            this.atField = atField;
            SupportUtil.requireNotDuplicated(getColumn(), getFlatten(), getName());
            afterSetField();
        }
    }
}
