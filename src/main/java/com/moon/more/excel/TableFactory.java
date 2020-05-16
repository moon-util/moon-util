package com.moon.more.excel;

import com.moon.core.util.IteratorUtil;
import com.moon.more.excel.annotation.*;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 针对注解的渲染工厂
 *
 * @author benshaoye
 * @see TableColumn 标记一个字段
 * @see TableColumnFlatten 标记一个实体字段
 * @see TableRecord 标记一个实体
 * @see TableListable 标记一个列表字段，一个实体里最多只能有一列可迭代
 * @see TableIndexer 标记一列索引
 */
public class TableFactory extends BaseFactory<Sheet, TableFactory, SheetFactory> {

    private final HeadExecutor HEAD_RENDERER

        = renderer -> renderer.renderHead(end());

    private final HeadExecutor EMPTY = r -> {};

    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;

    public TableFactory(WorkbookProxy proxy, SheetFactory parent) { super(proxy, parent); }

    void setSheet(Sheet sheet) { this.sheet = sheet; }

    @Override
    protected Sheet get() { return getSheet(); }

    public Sheet getSheet() { return sheet; }

    public TableFactory renderHead(Class targetClass) {
        HEAD_RENDERER.doTask(parse(targetClass));
        return this;
    }

    public <T> TableFactory renderBody(Iterator<T> iterator) { return renderBody(iterator, null); }

    public <T> TableFactory renderBody(Iterator<T> iterator, Class<T> targetClass) {
        return renderAll(iterator, targetClass, EMPTY);
    }

    public <T> TableFactory renderBody(Iterable<T> iterable) {
        return renderBody(iterable.iterator());
    }

    public <T> TableFactory renderBody(Iterable<T> iterable, Class<T> targetClass) {
        return renderBody(iterable.iterator(), targetClass);
    }

    public <T> TableFactory renderBody(Stream<T> stream) { return renderBody(stream.iterator()); }

    public <T> TableFactory renderBody(Stream<T> stream, Class<T> targetClass) {
        return renderBody(stream.iterator(), targetClass);
    }

    public <T> TableFactory renderBody(T... data) {
        return data == null ? this : renderBody(data.getClass().getComponentType(), data);
    }

    public <T> TableFactory renderBody(T[] data, Class targetClass) { return renderBody(targetClass, data); }

    public <T> TableFactory renderBody(Class targetClass, T... data) {
        return data == null ? this : renderBody(IteratorUtil.of(data), targetClass);
    }

    public TableFactory renderCollectBody(Object collect) {
        if (collect == null) {
            return this;
        }
        if (collect instanceof Iterable) {
            return renderBody((Iterable) collect);
        }
        if (collect instanceof Object[]) {
            return renderBody((Object[]) collect);
        }
        if (collect instanceof Iterator) {
            return renderBody((Iterator) collect);
        }
        if (collect instanceof Stream) {
            return renderBody((Stream) collect);
        }
        throw new UnsupportedOperationException();
    }

    public TableFactory renderCollectBody(Object collect, Class targetClass) {
        if (collect == null) {
            return this;
        }
        if (collect instanceof Iterable) {
            return renderBody((Iterable) collect, targetClass);
        }
        if (collect instanceof Object[]) {
            return renderBody((Object[]) collect, targetClass);
        }
        if (collect instanceof Iterator) {
            return renderBody((Iterator) collect, targetClass);
        }
        if (collect instanceof Stream) {
            return renderBody((Stream) collect, targetClass);
        }
        throw new UnsupportedOperationException();
    }

    public <T> TableFactory renderList(Iterator<T> iterator) { return renderList(iterator, null); }

    public <T> TableFactory renderList(Iterator<T> iterator, Class<T> targetClass) {
        return renderAll(iterator, targetClass, HEAD_RENDERER);
    }

    public <T> TableFactory renderList(Iterable<T> iterable) { return renderList(iterable.iterator()); }

    public <T> TableFactory renderList(Iterable<T> iterable, Class<T> targetClass) {
        return renderList(iterable.iterator(), targetClass);
    }

    public <T> TableFactory renderList(Stream<T> stream) { return renderList(stream.iterator()); }

    public <T> TableFactory renderList(Stream<T> stream, Class<T> targetClass) {
        return renderList(stream.iterator(), targetClass);
    }

    public <T> TableFactory renderList(T... data) {
        return data == null ? this : renderList(data.getClass().getComponentType(), data);
    }

    public <T> TableFactory renderList(T[] data, Class targetClass) { return renderList(targetClass, data); }

    public <T> TableFactory renderList(Class targetClass, T... data) {
        return data == null ? this : renderList(IteratorUtil.of(data), targetClass);
    }

    public TableFactory renderCollectList(Object collect) {
        if (collect == null) {
            return this;
        }
        if (collect instanceof Iterable) {
            return renderList((Iterable) collect);
        }
        if (collect instanceof Object[]) {
            return renderList((Object[]) collect);
        }
        if (collect instanceof Iterator) {
            return renderList((Iterator) collect);
        }
        if (collect instanceof Stream) {
            return renderList((Stream) collect);
        }
        throw new UnsupportedOperationException();
    }

    public TableFactory renderCollectList(Object collect, Class targetClass) {
        if (collect == null) {
            return this;
        }
        if (collect instanceof Iterable) {
            return renderList((Iterable) collect, targetClass);
        }
        if (collect instanceof Object[]) {
            return renderList((Object[]) collect, targetClass);
        }
        if (collect instanceof Iterator) {
            return renderList((Iterator) collect, targetClass);
        }
        if (collect instanceof Stream) {
            return renderList((Stream) collect, targetClass);
        }
        throw new UnsupportedOperationException();
    }

    /*
     * ~~~~ actual executor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected SheetFactory end() { return getParentFactory(); }

    private <T> TableFactory renderAll(
        Iterator<T> iterator, Class<T> targetClass, HeadExecutor beforeRenderBody
    ) {
        SheetFactory factory = end();
        Renderer renderer = null;
        Object firstItem = null;
        if (targetClass == null) {
            if (iterator.hasNext()) {
                T data = iterator.next();
                renderer = parse(data.getClass());
                beforeRenderBody.doTask(renderer);
                firstItem = data;
            }
        } else {
            renderer = parse(targetClass);
            beforeRenderBody.doTask(renderer);
        }
        if (renderer != null) {
            renderer.renderBody(factory, iterator, firstItem);
        }
        return this;
    }

    private Renderer parse(Class targetClass) { return SheetUtil.parseRenderer(targetClass); }

    interface HeadExecutor {

        /**
         * 执行
         *
         * @param renderer
         */
        void doTask(Renderer renderer);
    }
}
