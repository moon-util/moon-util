package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.IteratorUtil;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.moon.core.util.CollectUtil.toArrayOfEmpty;

/**
 * @author benshaoye
 */
public class MarkIteratedGroup implements MarkRenderer, Renderer {

    private final MarkRenderer[] columns;

    private final MarkColumn rootIndexer;

    private final MarkIterated iterateAt;

    private final DetailRoot root;

    /**
     * 标记{@link #columns}中是否有索引字段
     * <p>
     * 用于提升性能，避免每次都循环
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    private final boolean indexed;

    private final IterateStrategy strategy;

    /**
     * @param columns
     * @param iterateAt   如果不为 null，它是 columns 中的一项
     * @param rootIndexer null 或者索引，不属于 columns 中
     * @param root
     * @param indexed     columns 中是否包含一项或多项“索引”
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    public MarkIteratedGroup(
        List<MarkIterated> columns, MarkIterated iterateAt, MarkColumn rootIndexer, DetailRoot root, boolean indexed
    ) {
        this.strategy = IterateStrategy.getIterateStrategy(iterateAt.getPropertyType());
        this.columns = toArrayOfEmpty(columns, MarkIterated[]::new, EMPTY);
        this.rootIndexer = rootIndexer;
        this.iterateAt = iterateAt;
        this.indexed = indexed;
        this.root = root;
    }

    @Override
    public final void renderHead(SheetFactory sheetFactory) {
        // todo 列表头部渲染，供外部调用，不在内部递归调用
    }

    /**
     * @param sheetFactory
     * @param iterator
     * @param first        如果不为 null，则 firstItem 是 iterator 的第一项
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public final void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        resetAll();
        MarkIteratedExecutor executor = MarkIteratedExecutor.of(getColumns(), iterateAt);

        renderRecord(executor, sheetFactory, sheetFactory.row(), first);
        while (iterator.hasNext()) {
            renderRecord(executor, sheetFactory, null, iterator.next());
        }
    }

    @Override
    public final void renderRecord(
        MarkIteratedExecutor executor, SheetFactory sheetFactory, RowFactory factory, Object data
    ) {
        if (data == null) {
            return;
        }
        executor.setRegistriesData(data);
        Object iterable = iterateAt.getEvaluator().getPropertyValue(data);
        if (iterable == null) {
            factory = sheetFactory.row();
            executor.execute(factory, null);
        } else {
            @SuppressWarnings("rawtypes") Iterator iter = strategy.iterator(iterable);
            while (iter.hasNext()) {
                factory = sheetFactory.row();
                Object iteratorItem = iter.next();
                System.out.println(iteratorItem);
                executor.execute(factory, iteratorItem);
            }
        }
    }

    private void renderRootCol(MarkIteratedExecutor container, SheetFactory sheetFactory, RowFactory factory) {
        MarkColumn root = getRootIndexer();
        if (root != null) {
            root.renderRecord(container, sheetFactory, factory, null);
        }
    }

    /*
     * ~~~~~ initialize ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public void resetAll() {
        if (isIndexed()) {
            if (rootIndexer != null) {
                rootIndexer.resetAll();
            }
            for (MarkRenderer column : getColumns()) {
                column.resetAll();
            }
        }
    }

    /*
     * ~~~~~ getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public MarkIterated getIterateAt() { return iterateAt; }

    MarkRenderer[] getColumns() { return columns; }

    public boolean isIndexed() { return indexed; }

    MarkColumn getRootIndexer() { return rootIndexer; }

    @SuppressWarnings({"all"})
    interface IterateStrategy extends Predicate<Class> {

        Iterator iterator(Object data);

        Class getTopClass();

        @Override
        default boolean test(Class propertyClass) {
            return getTopClass().isAssignableFrom(propertyClass);
        }

        static IterateStrategy getIterateStrategy(Class propertyClass) {
            if (propertyClass.isArray()) {
                return ArrayIterateStrategy.getOrObjects(propertyClass);
            } else {
                IterateStrategy strategy = NoneIterateStrategy.NONE;
                IterateStrategy[] values = CollectIterateStrategy.getValues();
                for (IterateStrategy value : values) {
                    if (value.test(propertyClass)) {
                        return value;
                    }
                }
                values = MapIterateStrategy.getValues();
                for (IterateStrategy value : values) {
                    if (value.test(propertyClass)) {
                        return value;
                    }
                }
                throw new NullPointerException("未知集合类型: " + propertyClass);
                // return NoneIterateStrategy.NONE;
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    enum ArrayIterateStrategy implements IterateStrategy {
        /** 数组 */
        ARRAY_OBJECT(Object[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((Object[]) data);
            }
        },
        ARRAY_BOOLEAN(boolean[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((boolean[]) data);
            }
        },
        ARRAY_CHARS(char[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((char[]) data);
            }
        },
        ARRAY_BYTE(byte[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((byte[]) data);
            }
        },
        ARRAY_SHORT(short[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((short[]) data);
            }
        },
        ARRAY_INT(int[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((int[]) data);
            }
        },
        ARRAY_LONG(long[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((long[]) data);
            }
        },
        ARRAY_FLOAT(float[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((float[]) data);
            }
        },
        ARRAY_DOUBLE(double[].class) {
            @Override
            public Iterator iterator(Object data) {
                return IteratorUtil.of((double[]) data);
            }
        },
        ;

        private final Class topClass;

        ArrayIterateStrategy(Class topClass) {
            Cached.CACHE.put(topClass, this);
            this.topClass = topClass;
        }

        @Override
        public Class getTopClass() { return topClass; }

        private final static ArrayIterateStrategy[] VALUES = values();

        public static ArrayIterateStrategy[] getValues() { return VALUES; }

        static class Cached {

            final static Map<Class, ArrayIterateStrategy> CACHE = new HashMap<>();
        }

        public static ArrayIterateStrategy getOrObjects(Class type) {
            return Cached.CACHE.getOrDefault(type, ARRAY_OBJECT);
        }
    }

    @SuppressWarnings({"all"})
    enum NoneIterateStrategy implements IterateStrategy {
        NONE;

        @Override
        public Iterator iterator(Object data) { return IteratorUtil.ofEmpty(); }

        @Override
        public Class getTopClass() { return null; }

        @Override
        public boolean test(Class propertyClass) { return true; }
    }

    @SuppressWarnings({"rawtypes"})
    enum MapIterateStrategy implements IterateStrategy {
        /** map */
        MAP(Map.class) {
            @Override
            public Iterator iterator(Object data) {
                return ((Map) data).values().iterator();
            }
        },
        ;

        private final Class topClass;

        MapIterateStrategy(Class topClass) { this.topClass = topClass; }

        @Override
        public Class getTopClass() { return topClass; }

        @Override
        public Iterator iterator(Object data) {
            return ((Map) data).values().iterator();
        }

        private final static MapIterateStrategy[] VALUES = values();

        public static MapIterateStrategy[] getValues() { return VALUES; }
    }

    @SuppressWarnings({"rawtypes"})
    enum CollectIterateStrategy implements IterateStrategy {
        /** 集合 */
        ITERATE(Iterable.class) {
            @Override
            public Iterator iterator(Object data) {
                return ((Iterable) data).iterator();
            }
        },
        /** 迭代器 */
        ITERATOR(Iterator.class) {
            @Override
            public Iterator iterator(Object data) {
                return (Iterator) data;
            }
        },
        ;

        private final Class topClass;

        CollectIterateStrategy(Class topClass) { this.topClass = topClass; }

        @Override
        public Class getTopClass() { return topClass; }

        private final static CollectIterateStrategy[] VALUES = values();

        public static CollectIterateStrategy[] getValues() { return VALUES; }
    }
}
