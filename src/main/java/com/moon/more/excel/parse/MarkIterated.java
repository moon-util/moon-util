package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableListable;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * @author benshaoye
 */
public class MarkIterated implements MarkRenderer {

    private final int offset;
    /** property name */
    private final String name;
    /** property type */
    private final Class type;
    private final boolean iterated;

    private final Evaluator evaluator;
    private final Type actualType;
    private final Class actualClass;

    private final TableListable tableListable;
    private final TableIndexer tableIndexer;
    private final IntAccessor indexer;
    private final MarkRenderer group;

    public MarkIterated(int offset, Property property, MarkRenderer group) {
        this.offset = offset;
        this.group = group;

        this.name = property.getName();
        this.type = property.getPropertyType();
        this.evaluator = property.getEvaluator();
        this.tableListable = property.getListable();
        this.iterated = property.isIterated();

        Type actualType = property.getActualType();
        Class actualCls = actualType instanceof Class ? (Class) actualType : null;
        this.actualType = actualType;
        this.actualClass = actualCls;

        // init indexer
        TableIndexer tableIndexer = property.getIndexer();
        this.tableIndexer = tableIndexer;
        this.indexer = tableIndexer == null ? null : IntAccessor.of();
    }

    static MarkIterated of(int offset, Property property, MarkRenderer group) {
        return new MarkIterated(offset, property, group);
    }

    /*
     * getters
     */

    public int getOffset() { return offset; }

    @Override
    public void renderHead(SheetFactory sheetFactory) { }

    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) { }

    @Override
    public void renderRecord(MarkContainer container, SheetFactory sheetFactory, RowFactory f, Object data) {
        MarkRenderer childrenGroup = getGroup();
        Evaluator evaluator = getEvaluator();
        if (childrenGroup != null) {
            childrenGroup.renderRecord(container, sheetFactory, f, evaluator.getPropertyValue(data));
        } else {
            RowFactory factory = sheetFactory.row();
            container.execute(factory);
            evaluator.eval(factory.index(getOffset()), data);
        }
    }

    @Override
    public void resetAll() { }

    /*
     * ~~~~~ getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public MarkRenderer getGroup() { return group; }

    protected Evaluator getEvaluator() { return evaluator; }
}
