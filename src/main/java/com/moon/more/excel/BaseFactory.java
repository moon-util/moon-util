package com.moon.more.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public abstract class BaseFactory<T, F extends BaseFactory<T, F>> {

    protected final WorkbookProxy proxy;

    public BaseFactory(WorkbookProxy proxy) { this.proxy = proxy; }

    abstract T get();

    public F definitionStyle(String classname, BiConsumer<CellStyle, Font> builder) {
        proxy.definitionStyle(new StyleBuilder(classname, builder));
        return (F) this;
    }

    public F definitionStyle(String classname, Consumer<CellStyle> builder) {
        proxy.definitionStyle(new StyleBuilder(classname, builder));
        return (F) this;
    }

    public F finish() {
        proxy.applyCellStyle();
        return (F) this;
    }

    protected void afterDefinedStyle(CellStyle style, Font font) {}
}
