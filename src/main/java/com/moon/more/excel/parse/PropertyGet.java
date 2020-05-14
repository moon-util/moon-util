package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.LazyAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.PropertyGetter;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
class PropertyGet extends Property {

    private final LazyAccessor<PropertyGetter> accessor;
    private final LazyAccessor<Transfer4Get> transfer;

    private PropertyGet(String name, Annotated<Method> onMethod) {
        super(name, onMethod);
        accessor = LazyAccessor.of(() -> ValueGetter.of(getAtMethod(), getAtField()));
        this.transfer = LazyAccessor.of(() -> Transfer4Get.find(getPropertyType()));
    }

    static PropertyGet of(
        String name, Annotated<Method> onMethod
    ) { return new PropertyGet(name, onMethod); }

    static PropertyGet of(
        String name, TableIndexer indexer
    ) { return new IndexedGetter(name, indexer); }

    @Override
    protected void afterSetField() {
        transfer.clear();
        accessor.clear();
    }

    @Override
    public Evaluator getEvaluator() { return new PropEvaluator(getPropertyGetter(), transfer.get()); }

    static class PropEvaluator implements Evaluator {

        final PropertyGetter getter;
        final Transfer4Get transfer;

        PropEvaluator(PropertyGetter getter, Transfer4Get transfer) {
            this.transfer = transfer;
            this.getter = getter;
        }

        @Override
        public Object getPropertyValue(Object data) { return getter.getValue(data); }

        @Override
        public void setCellValue(CellFactory factory, Object value) {
            transfer.setCellValue(value, factory.getCell());
        }
    }

    PropertyGetter getPropertyGetter() { return accessor.get(); }

    static class IndexedGetter extends PropertyGet {

        private final TableIndexer indexer;

        private IndexedGetter(String name, TableIndexer indexer) {
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
        public PropertiesGroup getGroup() { return null; }

        @Override
        public TableColumn getColumn() { return null; }

        @Override
        public TableColumnFlatten getFlatten() { return null; }

        @Override
        public Class getPropertyType() { return null; }

        @Override
        public TableIndexer getIndexer() { return indexer; }

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
