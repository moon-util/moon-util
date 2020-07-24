package com.moon.poi.excel;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.poi.excel.annotation.TableColumn;
import com.moon.poi.excel.annotation.TableColumnGroup;
import com.moon.poi.excel.annotation.TableIndexer;
import com.moon.poi.excel.annotation.TableRecord;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * 针对注解的渲染工厂
 *
 * @author moonsky
 * @see TableColumn 标记一个字段
 * @see TableColumnGroup 标记一个实体字段
 * @see TableRecord 标记一个实体
 * * @see TableListable 标记一个列表字段，一个实体里最多只能有一列可迭代
 * @see TableIndexer 标记一列索引
 */
public class TableFactory extends BaseFactory<Sheet, TableFactory, SheetFactory> {

    private final HeadRenderer HEAD_RENDERER

        = renderer -> renderer.renderHead(end());

    private final static HeadRenderer EMPTY = r -> {};

    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;

    public TableFactory(WorkbookProxy proxy, SheetFactory parent) { super(proxy, parent); }

    void setSheet(Sheet sheet) { this.sheet = sheet; }

    @Override
    protected Sheet get() { return getSheet(); }

    public Sheet getSheet() { return sheet; }

    /**
     * 渲染表头
     *
     * @param targetClass 目标类
     *
     * @return this
     */
    public TableFactory renderHead(Class targetClass) {
        HEAD_RENDERER.render(parse(targetClass));
        return this;
    }

    /*
     * -----------------------------------------------------------------------------------
     * 渲染数据
     * -----------------------------------------------------------------------------------
     */

    public <T> TableFactory renderBody(Iterator<T> iterator) { return renderBody(iterator, null); }

    public <T> TableFactory renderBody(Iterator<T> iterator, Class<T> targetClass) {
        return doRenderData(iterator, targetClass, EMPTY);
    }

    public <T> TableFactory renderBody(Iterable<T> iterable) { return renderBody(iterable.iterator()); }

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
        return renderCollectBody(collect, null);
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
        throw new UnsupportedOperationException("不支持集合类型：" + collect.getClass());
    }

    /*
     * -----------------------------------------------------------------------------------
     * 渲染表头和数据
     * -----------------------------------------------------------------------------------
     */

    public <T> TableFactory renderAll(Iterator<T> iterator) { return renderAll(iterator, null); }

    public <T> TableFactory renderAll(Iterator<T> iterator, Class<T> targetClass) {
        return doRenderData(iterator, targetClass, HEAD_RENDERER);
    }

    public <T> TableFactory renderAll(Iterable<T> iterable) { return renderAll(iterable.iterator()); }

    public <T> TableFactory renderAll(Iterable<T> iterable, Class<T> targetClass) {
        return renderAll(iterable.iterator(), targetClass);
    }

    public <T> TableFactory renderAll(Stream<T> stream) { return renderAll(stream.iterator()); }

    public <T> TableFactory renderAll(Stream<T> stream, Class<T> targetClass) {
        return renderAll(stream.iterator(), targetClass);
    }

    public <T> TableFactory renderAll(T... data) {
        return data == null ? this : renderAll(data.getClass().getComponentType(), data);
    }

    public <T> TableFactory renderAll(T[] data, Class targetClass) { return renderAll(targetClass, data); }

    public <T> TableFactory renderAll(Class targetClass, T... data) {
        return data == null ? this : renderAll(IteratorUtil.of(data), targetClass);
    }

    public TableFactory renderCollectAll(Object collect) { return renderCollectAll(collect, null); }

    public TableFactory renderCollectAll(Object collect, Class targetClass) {
        if (collect == null) {
            return this;
        }
        if (collect instanceof Iterable) {
            return renderAll((Iterable) collect, targetClass);
        }
        if (collect instanceof Object[]) {
            return renderAll((Object[]) collect, targetClass);
        }
        if (collect instanceof Iterator) {
            return renderAll((Iterator) collect, targetClass);
        }
        if (collect instanceof Stream) {
            return renderAll((Stream) collect, targetClass);
        }
        if (collect.getClass().isArray()) {
            return renderAll(ArrayUtil.toObjectArray(collect), targetClass);
        }
        throw new UnsupportedOperationException("不支持集合类型：" + collect.getClass());
    }

    /*
     * ~~~~ actual executor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected SheetFactory end() { return getParentFactory(); }

    private <T> TableFactory doRenderData(
        Iterator<T> iterator, Class<T> targetClass, HeadRenderer headRenderer
    ) {
        SheetFactory factory = end();
        Renderer renderer = null;
        Object first = null;
        if (targetClass == null) {
            if (iterator.hasNext()) {
                T data = iterator.next();
                renderer = parse(data.getClass());
                headRenderer.render(renderer);
                first = data;
            }
        } else {
            renderer = parse(targetClass);
            headRenderer.render(renderer);
        }
        if (renderer != null) {
            renderer.renderBody(factory, iterator, first);
        }
        return this;
    }

    private Renderer parse(Class targetClass) { return TableUtil.parse(targetClass); }

    /**
     * 这实际上只是个代理
     */
    interface HeadRenderer {

        /**
         * 渲染表头
         *
         * @param renderer 实际执行的渲染器
         */
        void render(Renderer renderer);
    }
}
