package com.moon.more.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public abstract class BaseFactory<T, F extends BaseFactory<T, F, P>, P extends BaseFactory> {

    protected final static boolean DFT_APPEND_TYPE = true;

    protected final WorkbookProxy proxy;

    protected final P parent;

    public BaseFactory(WorkbookProxy proxy, P parent) {
        this.parent = parent == null ? (P) this : parent;
        this.proxy = proxy;
    }

    abstract T get();

    WorkbookType getWorkbookType() { return proxy.getWorkbookType(); }

    public F definitionStyle(String classname, BiConsumer<CellStyle, Font> builder) {
        proxy.definitionBuilder(new ProxyStyleBuilder(classname, builder));
        return (F) this;
    }

    public F definitionStyle(String classname, Consumer<CellStyle> builder) {
        proxy.definitionBuilder(new ProxyStyleBuilder(classname, builder));
        return (F) this;
    }

    /**
     * 预定义注释
     *
     * @param uniqueName 整个工作簿注释唯一命名
     * @param builder    将要如何定义注释的具体逻辑
     *
     * @return 当前对象
     */
    protected F definitionComment(String uniqueName, Consumer<Comment> builder) {
        proxy.definitionBuilder(new ProxyCommentBuilder(uniqueName, proxy.getSheet(), getWorkbookType(), builder));
        return (F) this;
    }

    /**
     * 预定义注释
     *
     * @param uniqueName 整个工作簿注释唯一命名
     * @param comment    注释内容
     *
     * @return 当前对象
     */
    protected F definitionComment(String uniqueName, String comment) {
        proxy.definitionBuilder(new ProxyCommentBuilder(uniqueName, proxy.getSheet(), getWorkbookType(),
            cmt -> getWorkbookType().newRichText(comment)));
        return (F) this;
    }

    /**
     * 应用代理的样式和注释设置，统一通过代理管理，并调用这个方法，将来可能会为{@link #finish()}添加更多功能
     * <p>
     * 应用方式为“按需应用”
     *
     * @return 当前操作工厂
     */
    public F finish() {
        proxy.applyProxiedModel();
        return (F) this;
    }

    /**
     * 返回父节点工厂，如果当前是“根”（ExcelFactory），则返回自身
     *
     * @return 父节点工厂
     */
    public P getParentFactory() { return parent; }
}
