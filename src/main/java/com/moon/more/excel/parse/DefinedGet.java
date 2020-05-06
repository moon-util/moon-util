package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.LazyAccessor;
import com.moon.more.excel.PropertyGetter;
import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
class DefinedGet extends Defined {

    private final LazyAccessor<PropertyGetter> accessor;
    final LazyAccessor<Transfer4Gets> transfer;

    private DefinedGet(
        String name, Marked<Method> onMethod
    ) {
        super(name, onMethod);
        accessor = LazyAccessor.of(() -> ValueGetter.of(getAtMethod(), getAtField()));
        this.transfer = LazyAccessor.of(() -> Transfer4Gets.find(getPropertyType()));
    }

    static DefinedGet of(
        String name, Marked<Method> onMethod
    ) { return new DefinedGet(name, onMethod); }

    static DefinedGet of(
        String name, DataIndexer indexer
    ) { return new IndexedGetter(name, indexer); }

    @Override
    protected void afterSetField() { accessor.clear(); }

    @Override
    public void exec(Object data, Cell cell) {
        transfer.get().setCellValue(getValue(data), cell);
    }

    PropertyGetter getPropertyGetter() { return accessor.get(); }

    Object getValue(Object data) { return getPropertyGetter().getValue(data); }

    static class IndexedGetter extends DefinedGet {

        private final DataIndexer indexer;

        private IndexedGetter(String name, DataIndexer indexer) {
            super(name, null);
            this.indexer = indexer;
        }

        @Override
        public boolean isDefined() { return true; }

        @Override
        public boolean isUndefined() { return false; }

        @Override
        public boolean isOnlyIndexer() { return true; }

        @Override
        public boolean isDataColumn() { return false; }

        @Override
        public boolean isDataFlatten() { return false; }

        @Override
        public boolean hasIndexer() { return true; }

        @Override
        Field getAtField() { return null; }

        @Override
        Method getAtMethod() { return null; }

        @Override
        public Detail getChildrenGroup() { return null; }

        @Override
        public DataColumn getColumn() { return null; }

        @Override
        public DataColumnFlatten getFlatten() { return null; }

        @Override
        public Class getPropertyType() { return null; }

        @Override
        public DataIndexer getIndexer() { return indexer; }

        @Override
        public String[] getHeadLabels() { return null; }

        @Override
        public String getHeadLabelAsIndexer() { return getIndexer().value(); }

        @Override
        PropertyGetter getPropertyGetter() {
            return data -> IntAccessor.of(getIndexer().startingAt());
        }
    }
}
