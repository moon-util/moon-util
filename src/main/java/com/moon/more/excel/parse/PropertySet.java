package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
class PropertySet extends Property {

    private PropertySet(
        String name, Annotated<Method> onMethod
    ) { super(name, onMethod); }

    static PropertySet of(
        String name, Annotated<Method> onMethod
    ) { return new PropertySet(name, onMethod); }

    static PropertySet of(
        String name, TableIndexer indexer
    ) { return new IndexedSetter(name, indexer); }

    static class IndexedSetter extends PropertySet {

        private final TableIndexer indexer;

        private IndexedSetter(String name, TableIndexer indexer) {
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
    }
}
