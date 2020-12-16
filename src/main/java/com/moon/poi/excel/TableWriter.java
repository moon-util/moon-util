package com.moon.poi.excel;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.poi.excel.annotation.TableColumn;
import com.moon.poi.excel.annotation.TableColumnGroup;
import com.moon.poi.excel.annotation.TableRecord;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 针对注解的渲染工厂
 *
 * @author moonsky
 * @see TableColumn 标记一个字段
 * @see TableColumnGroup 标记一个实体字段
 * @see TableRecord 标记一个实体
 * * @see TableListable 标记一个列表字段，一个实体里最多只能有一列可迭代
 // * @see TableIndexer 标记一列索引
 */
public class TableWriter extends BaseWriter<Sheet, TableWriter, SheetWriter> {

    private final PartRenderer HEAD_RENDERER

        = renderer -> renderer.renderHead(end());

    private final static PartRenderer EMPTY = r -> {};

    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;
    /**
     * 标题
     */
    private TableTitle title;
    /**
     * 是否渲染表头
     */
    private boolean renderHead = true;
    /**
     * 是否渲染数据
     */
    private boolean renderBody = true;
    /**
     * 禁用缓存
     */
    private boolean cacheDisabled = false;

    public TableWriter(WorkbookProxy proxy, SheetWriter parent) { super(proxy, parent); }

    void setSheet(Sheet sheet) { this.sheet = sheet; }

    @Override
    protected Sheet get() { return getSheet(); }

    public Sheet getSheet() { return sheet; }

    private boolean isCacheDisabled() { return cacheDisabled; }

    /**
     * 设置标题，标题为 null 表示不设置
     *
     * @param title 标题内容
     *
     * @return TableFactory
     */
    public TableWriter title(String title) {
        return titleOf(title == null ? null : TableTitle.of(title));
    }

    /**
     * 设置标题，标题为 null 表示不设置
     *
     * @param title     标题内容
     * @param classname 标题样式
     *
     * @return TableFactory
     */
    public TableWriter title(String title, String classname) {
        return titleOf(title == null ? null : TableTitle.of(title, classname));
    }

    /**
     * 设置标题，标题为 null 表示不设置
     *
     * @param title 标题
     *
     * @return TableFactory
     */
    public TableWriter titleOf(TableTitle title) {
        this.title = title;
        return this;
    }

    /**
     * 设置渲染是否渲染表头和数据内容
     *
     * @param renderHead 是否渲染表头
     * @param renderBody 是否渲染数据内容
     *
     * @return TableFactory
     */
    public TableWriter config(boolean renderHead, boolean renderBody) {
        this.renderHead = renderHead;
        this.renderBody = renderBody;
        return this;
    }

    private PartRenderer getHeaderRenderer() {
        return renderHead ? HEAD_RENDERER : EMPTY;
    }

    private TableTitle getTitle() { return title; }

    private boolean isRenderBody() { return renderBody; }

    /**
     * 是否禁用解析缓存，生产环境下适当缓存可避免频繁解析，提高效率
     * 开发测试环境下可随时修改一些注解配置等
     *
     * @param disabled 是否禁用
     *
     * @return this
     */
    public TableWriter cacheDisabled(boolean disabled) {
        this.cacheDisabled = disabled;
        return this;
    }

    /*
     * -----------------------------------------------------------------------------------
     * 渲染表头和数据
     * -----------------------------------------------------------------------------------
     */

    /**
     * 渲染列表数据，默认解析第一项的类上的配置
     *
     * @param iterator 列表
     * @param <T>      数据类型
     *
     * @return this
     */
    public <T> TableWriter render(Iterator<? extends T> iterator) { return render(iterator, null); }

    /**
     * 渲染列表数据，如果{@code targetClass}不为{@code null}，则解析{@code targetClass}
     * 否则默认解析第一项的类上的配置
     *
     * @param iterator    列表
     * @param targetClass 自定义配置类
     * @param <T>         数据类型
     *
     * @return this
     */
    public <T> TableWriter render(Iterator<? extends T> iterator, Class<? super T> targetClass) {
        return doRenderData(iterator, targetClass, getHeaderRenderer());
    }

    /**
     * 渲染列表数据，默认解析第一项的类上的配置
     *
     * @param iterable 列表
     * @param <T>      数据类型
     *
     * @return this
     */
    public <T> TableWriter render(Iterable<? extends T> iterable) { return render(iterable.iterator()); }

    /**
     * 渲染列表数据，如果{@code targetClass}不为{@code null}，则解析{@code targetClass}
     * 否则默认解析第一项的类上的配置
     *
     * @param iterable    列表
     * @param targetClass 自定义配置类
     * @param <T>         数据类型
     *
     * @return this
     */
    public <T> TableWriter render(Iterable<? extends T> iterable, Class<? super T> targetClass) {
        return render(iterable.iterator(), targetClass);
    }

    /**
     * 渲染列表数据，默认解析第一项的类上的配置
     *
     * @param stream 列表
     * @param <T>    数据类型
     *
     * @return this
     */
    public <T> TableWriter render(Stream<? extends T> stream) { return render(stream.iterator()); }

    /**
     * 渲染列表数据，如果{@code targetClass}不为{@code null}，则解析{@code targetClass}
     * 否则默认解析第一项的类上的配置
     *
     * @param stream      列表
     * @param targetClass 自定义配置类
     * @param <T>         数据类型
     *
     * @return this
     */
    public <T> TableWriter render(Stream<? extends T> stream, Class<? super T> targetClass) {
        return render(stream.iterator(), targetClass);
    }

    /**
     * 渲染数组列表数组，默认解析第一项的类上的配置
     *
     * @param data 数据列表
     * @param <T>  数据类型
     *
     * @return this
     */
    @SafeVarargs
    public final <T> TableWriter render(T... data) {
        if (data == null) {
            return this;
        }
        Class targetClass = data.getClass().getComponentType();
        return render(IteratorUtil.of(data), targetClass);
    }

    /**
     * 渲染数组列表数组，默认解析第一项的类上的配置
     *
     * @param targetClass 自定义配置类
     * @param data        数据列表
     * @param <T>         数据类型
     *
     * @return this
     */
    @SafeVarargs
    public final <T> TableWriter render(Class<? super T> targetClass, T... data) {
        return data == null ? this : render(IteratorUtil.of(data), targetClass);
    }

    /**
     * 渲染任意集合类型，用于预先未知集合数据类型的情况，这里会自动选择合适的类型进行渲染
     * <p>
     * 不支持 Map
     *
     * @param collect 集合、数组等
     *
     * @return TableFactory
     */
    public TableWriter renderAll(Object collect) { return renderAll(collect, null); }

    /**
     * 渲染任意集合类型，用于预先未知集合数据类型的情况，这里会自动选择合适的类型进行渲染
     * <p>
     * 不支持 Map
     *
     * @param collect     集合、数组等
     * @param targetClass 数据项类
     *
     * @return TableFactory
     */
    public TableWriter renderAll(Object collect, Class targetClass) {
        if (collect == null) {
            return this;
        }
        if (collect instanceof Iterable) {
            return render((Iterable) collect, targetClass);
        }
        if (collect instanceof Object[]) {
            return render(targetClass, (Object[]) collect);
        }
        if (collect instanceof Iterator) {
            return render((Iterator) collect, targetClass);
        }
        if (collect instanceof Stream) {
            return render((Stream) collect, targetClass);
        }
        if (collect.getClass().isArray()) {
            return render(targetClass, ArrayUtil.toObjectArray(collect));
        }
        throw new UnsupportedOperationException("不支持集合类型：" + collect.getClass());
    }

    /*
     * ~~~~ actual executor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected SheetWriter end() { return getParentFactory(); }

    private <T> TableWriter doRenderData(
        Iterator<? extends T> iterator, Class<? super T> targetClass, PartRenderer headRenderer
    ) {
        SheetWriter factory = end();
        Renderer renderer = null;
        Object first = null;
        if (targetClass == null) {
            if (iterator.hasNext()) {
                T data = iterator.next();
                renderer = parseRenderer(data.getClass());
                renderer.title(factory, getTitle());
                headRenderer.render(renderer);
                first = data;
            }
        } else {
            renderer = parseRenderer(targetClass);
            renderer.title(factory, getTitle());
            headRenderer.render(renderer);
        }
        if (renderer != null && isRenderBody()) {
            renderer.renderBody(factory, iterator, first);
        }
        return this;
    }

    private final Map<Class<?>, Renderer> thisCached = new ConcurrentHashMap<>();

    private Renderer parseRenderer(Class<?> targetClass) {
        if (isCacheDisabled()) {
            return TableUtil.parse(targetClass, this, true);
        } else {
            Renderer renderer = thisCached.get(targetClass);
            if (renderer == null) {
                renderer = TableUtil.parse(targetClass, this, false);
                thisCached.put(targetClass, renderer);
            }
            return renderer;
        }
    }

    /**
     * 这实际上只是个代理
     */
    interface PartRenderer {

        /**
         * 渲染表头
         *
         * @param renderer 实际执行的渲染器
         */
        void render(Renderer renderer);
    }
}
