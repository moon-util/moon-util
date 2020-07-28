package com.moon.poi.excel.table;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.MapUtil;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import com.moon.poi.excel.annotation.style.StyleBuilder;
import org.apache.poi.ss.usermodel.*;

import java.util.*;
import java.util.function.Consumer;

import static com.moon.core.lang.StringUtil.defaultIfEmpty;

/**
 * @author moonsky
 */
final class StyleUtil {

    final static Map defaultMap = Collections.emptyMap();

    final static String EMPTY = "";

    static Map<Class, Map<String, StyleBuilder>> collectStyleMap(TableCol[] columns, Map thisStyleMap) {
        Map<Class, Map<String, StyleBuilder>> definitions = MapUtil.newHashMap();

        for (TableCol column : columns) {
            column.collectStyleMap(definitions, thisStyleMap);
        }

        return returningMap(definitions);
    }

    static String classname(Class type, String propertyName) {
        return StringUtil.concat(type.getSimpleName(), "#", propertyName);
    }

    static Map<String, StyleBuilder> toStyleMapOnTargetClass(Class<?> type) {
        return parsePropertyStyle(EMPTY, type.getAnnotationsByType(DefinitionStyle.class));
    }

    static Map<String, StyleBuilder> toStyleMap(Class type, Attribute attr) {
        DefinitionStyle.List list = attr.getAnnotation(DefinitionStyle.List.class);
        DefinitionStyle style = attr.getAnnotation(DefinitionStyle.class);
        String defaultName = classname(type, attr.getName());
        Map resultMap = null;
        if (style != null) {
            DefinitionStyle[] styles = {style};
            resultMap = parsePropertyStyle(defaultName, styles);
        } else if (list != null) {
            resultMap = parsePropertyStyle(defaultName, list.value());
        }
        return returningMap(resultMap);
    }

    static Map getScopedMapOrEmpty(Map<?, Map> definitions, Class type) {
        return definitions.isEmpty() ? defaultMap : definitions.getOrDefault(type, defaultMap);
    }

    private static Map returningMap(Map resultMap) {
        return MapUtil.isEmpty(resultMap) ? defaultMap : resultMap;
    }

    private static Map<String, StyleBuilder> parsePropertyStyle(String dftName, DefinitionStyle... styles) {
        Map<String, StyleBuilder> builderMap = MapUtil.newHashMap();
        for (DefinitionStyle style : styles) {
            StyleBuilder builder = toBuilder(style);
            String classname = defaultIfEmpty(style.classname(), dftName);
            builderMap.put(classname, builder);
        }
        return builderMap;
    }

    private static StyleBuilder toBuilder(DefinitionStyle style) {
        Class<? extends StyleBuilder> type = style.createBy();
        if (type != StyleBuilder.class) {
            ParserUtil.checkValidImplClass(type, StyleBuilder.class);
            return ClassUtil.newInstance(type);
        }

        List<Consumer> consumers = new ArrayList<>();
        consumers.add(new Align(style.align()));
        consumers.add(new VerticalAlign(style.verticalAlign()));
        consumers.add(new FillPattern(style.fillPattern()));

        consumers.add(style.wrapText() ? WrapText.WRAP : WrapText.UNWRAP);

        if (style.backgroundColor() > -1) {
            consumers.add(new FillBackgroundColor(style.backgroundColor()));
        }
        if (style.foregroundColor() > -1) {
            consumers.add(new FillForegroundColor(style.foregroundColor()));
        }
        if (style.borderColor().length > 0) {
            consumers.add(new BorderColor(style.borderColor()));
        }
        if (style.border().length > 0) {
            consumers.add(new Border(style.border()));
        }
        Consumer[] consumerArr = ListUtil.toArray(consumers, Consumer[]::new);
        return new TableStyleBuilder(consumerArr);
    }

    private static class TableStyleBuilder implements StyleBuilder {

        private final Consumer<CellStyle>[] setters;

        private TableStyleBuilder(Consumer<CellStyle>[] setters) {
            this.setters = setters;
        }

        @Override
        public void accept(CellStyle style) {
            Consumer<CellStyle>[] setters = this.setters;
            for (Consumer<CellStyle> setter : setters) {
                setter.accept(style);
            }
        }
    }

    private static class Border implements Consumer<CellStyle> {

        private final BorderStyle[] styles;
        private final int length;

        private Border(BorderStyle[] styles) {
            this.styles = Arrays.copyOf(styles, styles.length);
            this.length = Math.min(styles.length, 4);
        }

        @Override
        public void accept(CellStyle style) {
            BorderStyle[] colors = this.styles;
            int length = this.length;
            for (int i = 0; i < length; i++) {
                switch (i) {
                    case 0:
                        style.setBorderTop(colors[i]);
                        break;
                    case 1:
                        style.setBorderRight(colors[i]);
                        break;
                    case 2:
                        style.setBorderBottom(colors[i]);
                        break;
                    case 3:
                        style.setBorderLeft(colors[i]);
                        break;
                }
            }
        }
    }

    private static class BorderColor implements Consumer<CellStyle> {

        private final short[] colors;
        private final int length;

        private BorderColor(short[] colors) {
            this.colors = Arrays.copyOf(colors, colors.length);
            this.length = Math.min(colors.length, 4);
        }

        @Override
        public void accept(CellStyle style) {
            short[] colors = this.colors;
            int length = this.length;
            for (int i = 0; i < length; i++) {
                switch (i) {
                    case 0:
                        style.setTopBorderColor(colors[i]);
                        break;
                    case 1:
                        style.setRightBorderColor(colors[i]);
                        break;
                    case 2:
                        style.setBottomBorderColor(colors[i]);
                        break;
                    case 3:
                        style.setLeftBorderColor(colors[i]);
                        break;
                }
            }
        }
    }

    private enum WrapText implements Consumer<CellStyle> {

        WRAP(true),
        UNWRAP(false);

        private final boolean wrap;

        WrapText(boolean wrap) {
            this.wrap = wrap;
        }

        @Override
        public void accept(CellStyle style) {
            style.setWrapText(wrap);
        }
    }

    private static class Align implements Consumer<CellStyle> {

        private final HorizontalAlignment align;

        private Align(HorizontalAlignment align) {this.align = align;}

        @Override
        public void accept(CellStyle style) {
            style.setAlignment(align);
        }
    }

    private static class VerticalAlign implements Consumer<CellStyle> {

        private final VerticalAlignment alignment;


        private VerticalAlign(VerticalAlignment alignment) {this.alignment = alignment;}

        @Override
        public void accept(CellStyle style) {
            style.setVerticalAlignment(alignment);
        }
    }

    private static class FillPattern implements Consumer<CellStyle> {

        private final FillPatternType type;

        private FillPattern(FillPatternType pattern) {this.type = pattern;}

        @Override
        public void accept(CellStyle style) {
            style.setFillPattern(type);
        }
    }

    private static class FillBackgroundColor implements Consumer<CellStyle> {

        private final short color;

        private FillBackgroundColor(short color) {this.color = color;}

        @Override
        public void accept(CellStyle style) {
            style.setFillBackgroundColor(color);
        }
    }

    private static class FillForegroundColor implements Consumer<CellStyle> {


        private final short color;

        private FillForegroundColor(short color) {this.color = color;}

        @Override
        public void accept(CellStyle style) {
            style.setFillForegroundColor(color);
        }
    }
}
