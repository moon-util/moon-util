package com.moon.processing.decl;

import com.moon.accessor.annotation.AutoInsertable;
import com.moon.accessor.annotation.AutoUpdatable;
import com.moon.accessor.annotation.TableId;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class PropertyFieldDeclared extends FieldDeclared {

    private final TableId tableId;
    private final AutoInsertable autoInsert;
    private final AutoUpdatable autoUpdate;

    public PropertyFieldDeclared(
        TypeElement thisElement,
        TypeElement declareEnclosingElement,
        VariableElement fieldElement,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(thisElement, declareEnclosingElement, fieldElement, thisGenericMap);
        this.tableId = fieldElement.getAnnotation(TableId.class);
        this.autoInsert = fieldElement.getAnnotation(AutoInsertable.class);
        this.autoUpdate = fieldElement.getAnnotation(AutoUpdatable.class);
    }

    public boolean isTableId() { return getTableId() != null; }

    public TableId getTableId() { return tableId; }

    public AutoInsertable getAutoInsert() { return autoInsert; }

    public AutoUpdatable getAutoUpdate() { return autoUpdate; }
}
