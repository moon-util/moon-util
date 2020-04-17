package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
class DefinedSet extends Defined {

    private DefinedSet(
        String name, Marked<Method> onMethod
    ) { super(name, onMethod); }

    static DefinedSet of(
        String name, Marked<Method> onMethod
    ) { return new DefinedSet(name, onMethod); }

    static DefinedSet of(
        String name, DataIndexer indexer
    ) { return new IndexedSetter(name, indexer); }

    static class IndexedSetter extends DefinedSet {

        private final DataIndexer indexer;

        private IndexedSetter(String name, DataIndexer indexer) {
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
        public boolean isOnlyColumn() { return false; }

        @Override
        public boolean isFlatColumn() { return false; }

        @Override
        public boolean hasIndexer() { return true; }

        @Override
        Field getAtField() { return null; }

        @Override
        Method getAtMethod() { return null; }

        @Override
        public ParsedDetail getChildrenGroup() { return null; }

        @Override
        public DataColumn getColumn() { return null; }

        @Override
        public DataColumnFlatten getFlatten() { return null; }

        @Override
        public Class getPropertyType() { return null; }

        @Override
        public DataIndexer getIndexer() { return indexer; }
    }
}
