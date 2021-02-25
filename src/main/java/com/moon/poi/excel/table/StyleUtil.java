package com.moon.poi.excel.table;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.IntUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.CollectUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.MapUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.function.ShortFunction;
import com.moon.poi.excel.annotation.RowRecord;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import com.moon.poi.excel.annotation.style.StyleBuilder;
import org.apache.poi.ss.usermodel.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.moon.core.lang.StringUtil.defaultIfEmpty;
import static com.moon.core.lang.StringUtil.isNotEmpty;

/**
 * @author moonsky
 */
final class StyleUtil {

    private final static String SEPARATOR = ":";

    final static Map defaultMap = Collections.emptyMap();

    final static String EMPTY = "";

    private static String scoped(Object type) {
        return IntUtil.toCompressionString(System.identityHashCode(type), Integer.MAX_VALUE);
    }

    private static String defaultClassnameForEmpty(
        String prefix, Attribute attribute, Function<Attribute, String> scopedPrefix
    ) {
        return classnameOf(prefix, classnameOf(scopedPrefix.apply(attribute), attribute.getName()));
    }

    private static String getClassnameIfAbsent(
        List<DefinitionStyle> styles, String prefix, Attribute attribute, Function<Attribute, String> scopedPrefix
    ) {
        if (ListUtil.isNotEmpty(styles)) {
            if (styles.size() == 1) {
                DefinitionStyle style = styles.get(0);
                String classname = style.classname();
                if (StringUtil.isEmpty(classname)) {
                    return defaultClassnameForEmpty(prefix, attribute, scopedPrefix);
                } else {
                    return classnameOf(prefix, classname);
                }
            }
            for (DefinitionStyle style : styles) {
                if (StringUtil.isEmpty(style.classname())) {
                    return defaultClassnameForEmpty(prefix, attribute, scopedPrefix);
                }
            }
        }
        return null;
    }

    /**
     * 如果字段上存在主动命名的 style，就直接使用主动命名
     * <p>
     * 如果不存在主动命名的 style，首先检查当前字段或 getter 方法是否有注解{@link DefinitionStyle}
     * 如果有且仅有一个，无论该{@link DefinitionStyle}是否定义了{@code classname}，都直接使用该样式，
     * 如果有多个，则使用其中没有设置{@code classname}的{@link DefinitionStyle}
     * 如果一个也没有，则检查是否存在全局默认定义，即在类上是否有定义没有设置{@code classname}的{@link DefinitionStyle}有就使用
     * 否则返回 null
     *
     * @param config
     *
     * @return
     */
    static String getTableColClassname(AttrConfig config) {
        Attribute attribute = config.getAttribute();
        if (!attribute.isAnnotated()) {
            return null;
        }
        String prefix = scoped(config.getTargetClass());
        String style = attribute.getTableColumn().style();
        // TableHeadClassname styleForCell = attribute.getAnnotation(TableHeadClassname.class);
        if (StringUtil.isEmpty(style)) {
            String classname = getClassnameIfAbsent(attribute.getDefinitionStylesOnMethod(),
                prefix,
                attribute,
                attr -> scoped(attr.getMemberMethod()));
            classname = classname == null ? getClassnameIfAbsent(attribute.getDefinitionStylesOnField(),
                prefix,
                attribute,
                attr -> scoped(attr.getMemberField())) : classname;
            if (classname == null) {
                return config.isDefinedDefaultStyle() ? classnameOfEmpty(config.getTargetClass()) : null;
            }
            return classname;
        } else {
            return classnameOf(prefix, style);
        }
    }

    static String classnameOfEmpty(Class prefix) {
        return classnameOf(prefix, EMPTY);
    }

    private static String classnameOf(Class prefix, String classname) {
        return classnameOf(scoped(prefix), classname);
    }

    private static String classnameOf(String prefix, String classname) {
        return StringUtil.concat(prefix, SEPARATOR, classname);
    }

    private static void collectImports(
        Set<Class<?>> rang, final Map<String, StyleBuilder> builderMap, final String prefix, final RowRecord record
    ) {
        if (record == null) {
            return;
        }
        Class<?>[] imports = record.importStyles();
        for (final Class<?> importClass : imports) {
            RowRecord importRecord = null;
            // 递归深层引入样式
            if (rang.contains(importClass)) {
                continue;
            } else {
                rang.add(importClass);
                importRecord = importClass.getAnnotation(RowRecord.class);
                collectImports(rang, builderMap, prefix, importRecord);
            }
            // 定义在类上的所有样式
            parseStyleOnClass(builderMap, importClass, prefix, importRecord);
            // 定义在 getter 方法上的命名样式
            try {
                BeanInfo info = Introspector.getBeanInfo(importClass);
                PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
                for (PropertyDescriptor descriptor : descriptors) {
                    Method getter = descriptor.getReadMethod();
                    if (getter != null) {
                        DefinitionStyle[] styles = getter.getAnnotationsByType(DefinitionStyle.class);
                        // 只保留命名样式
                        Stream<DefinitionStyle> stream = Arrays.stream(styles)
                            .filter(style -> isNotEmpty(style.classname()));
                        parseStyles(builderMap, prefix, null, ListUtil.newList(stream));
                    }
                }
            } catch (IntrospectionException e) {
                // ignore
            }
            // 定义在字段上的命名样式
            Class thisClass = importClass;
            while (thisClass != Object.class) {
                Field[] fields = thisClass.getDeclaredFields();
                for (Field field : fields) {
                    DefinitionStyle[] styles = field.getAnnotationsByType(DefinitionStyle.class);
                    // 只保留命名样式
                    Stream<DefinitionStyle> stream = Arrays.stream(styles)
                        .filter(style -> isNotEmpty(style.classname()));
                    parseStyles(builderMap, prefix, null, ListUtil.newList(stream));
                }
                thisClass = thisClass.getSuperclass();
            }
        }
    }

    private static void parseStyleOnClass(
        Map<String, StyleBuilder> builderMap, Class<?> type, String prefix, RowRecord record
    ) {
        List<DefinitionStyle> classStyles = ListUtil.newList(type.getAnnotationsByType(DefinitionStyle.class));
        parseStyles(builderMap, prefix, EMPTY, classStyles);
    }

    static Map<String, StyleBuilder> collect(Class<?> type, List<Attribute> attrs) {
        Map<String, StyleBuilder> builderMap = MapUtil.newMap();
        final String prefix = scoped(type);
        // 首先引入外部定义样式
        final RowRecord rowRecord = type.getAnnotation(RowRecord.class);
        collectImports(SetUtil.newSet(type), builderMap, prefix, rowRecord);
        // 解析类定义样式
        parseStyleOnClass(builderMap, type, prefix, rowRecord);
        for (Attribute attr : attrs) {
            // getter 方法上的默认样式覆盖字段上的默认样式
            List<DefinitionStyle> stylesOnMethod = attr.getDefinitionStylesOnMethod();
            if (CollectUtil.isNotEmpty(stylesOnMethod)) {
                String name = classnameOf(scoped(attr.getMemberMethod()), attr.getName());
                parseStyles(builderMap, prefix, name, stylesOnMethod);
            }
            // 解析字段上的样式
            List<DefinitionStyle> stylesOnField = attr.getDefinitionStylesOnField();
            if (CollectUtil.isNotEmpty(stylesOnField)) {
                String name = classnameOf(scoped(attr.getMemberField()), attr.getName());
                parseStyles(builderMap, prefix, name, stylesOnField);
            }
        }
        return MapUtil.isEmpty(builderMap) ? defaultMap : builderMap;
    }

    private static void parseStyles(
        Map<String, StyleBuilder> builderMap,
        String scope,
        String defaultClassnameForEmpty,
        Collection<DefinitionStyle> styles
    ) { parseStyles(builderMap, scope, defaultClassnameForEmpty, ListUtil.toArray(styles, DefinitionStyle[]::new)); }

    private static void parseStyles(
        Map<String, StyleBuilder> builderMap, String scope, String defaultClassnameForEmpty, DefinitionStyle... styles
    ) {
        int length = styles == null ? 0 : styles.length;
        for (int i = 0; i < length; i++) {
            DefinitionStyle style = styles[i];
            String classname = classnameOf(scope, defaultIfEmpty(style.classname(), defaultClassnameForEmpty));
            builderMap.put(classname, toBuilder(style));
        }
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

        addIfValidColor(consumers, style.backgroundColor(), FillBackgroundColor::new);
        addIfValidColor(consumers, style.foregroundColor(), FillForegroundColor::new);
        short[] colors = style.borderColor();
        switch (colors.length) {
            default:
            case 4:
                addIfValidColor(consumers, colors[3], BorderColorLeft::new);
            case 3:
                addIfValidColor(consumers, colors[2], BorderColorBottom::new);
            case 2:
                addIfValidColor(consumers, colors[1], BorderColorRight::new);
            case 1:
                addIfValidColor(consumers, colors[0], BorderColorTop::new);
            case 0:
                break;
        }
        BorderStyle[] styles = style.border();
        switch (styles.length) {
            default:
            case 4:
                consumers.add(new BorderStyleLeft(styles[3]));
            case 3:
                consumers.add(new BorderStyleBottom(styles[2]));
            case 2:
                consumers.add(new BorderStyleRight(styles[1]));
            case 1:
                consumers.add(new BorderStyleTop(styles[0]));
            case 0:
                break;
        }
        return new TableStyleBuilder(ListUtil.toArray(consumers, Consumer[]::new));
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

    private abstract static class BorderStyleBase implements Consumer<CellStyle> {

        protected final BorderStyle style;

        protected BorderStyleBase(BorderStyle style) {this.style = style;}
    }

    private final static class BorderStyleTop extends BorderStyleBase {

        protected BorderStyleTop(BorderStyle style) { super(style); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setBorderTop(style); }
    }

    private final static class BorderStyleRight extends BorderStyleBase {

        protected BorderStyleRight(BorderStyle style) { super(style); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setBorderRight(style); }
    }

    private final static class BorderStyleBottom extends BorderStyleBase {

        protected BorderStyleBottom(BorderStyle style) { super(style); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setBorderBottom(style); }
    }

    private final static class BorderStyleLeft extends BorderStyleBase {

        protected BorderStyleLeft(BorderStyle style) { super(style); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setBorderLeft(style); }
    }

    private abstract static class BorderColorBase implements Consumer<CellStyle> {

        protected final short color;

        protected BorderColorBase(short color) {this.color = color;}
    }

    private final static class BorderColorTop extends BorderColorBase {

        protected BorderColorTop(short width) { super(width); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setTopBorderColor(color); }
    }

    private final static class BorderColorRight extends BorderColorBase {

        protected BorderColorRight(short width) { super(width); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setRightBorderColor(color); }
    }

    private final static class BorderColorBottom extends BorderColorBase {

        protected BorderColorBottom(short width) { super(width); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setBottomBorderColor(color); }
    }

    private final static class BorderColorLeft extends BorderColorBase {

        protected BorderColorLeft(short width) { super(width); }

        @Override
        public void accept(CellStyle cellStyle) { cellStyle.setLeftBorderColor(color); }
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

    private static <T> void addIfValidColor(List<? super T> consumers, short color, ShortFunction<T> builder) {
        if (color > -1) {
            consumers.add(builder.apply(color));
        }
    }
}
