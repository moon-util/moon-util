package com.moon.more.excel.table;

import com.moon.core.lang.ClassUtil;
import com.moon.more.excel.annotation.CellStyleBuilder;
import com.moon.more.excel.annotation.DefinitionStyle;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author benshaoye
 */
@DefinitionStyle(createBy = StyleUtil.Builder.class)
final class StyleUtil {

    static void parsePropertyStyle(AttrConfig config) {
        Attribute attr = config.getAttribute();
        DefinitionStyle.List list = attr.getAnnotation(DefinitionStyle.List.class);
        DefinitionStyle style = attr.getAnnotation(DefinitionStyle.class);
        if (style != null) {
            DefinitionStyle[] styles = {style};
            parsePropertyStyle(config, styles);
        } else if (list != null) {
            parsePropertyStyle(config, list.value());
        }
    }

    private static void parsePropertyStyle(AttrConfig config, DefinitionStyle... style) {

    }

    public class Builder implements CellStyleBuilder {

        /**
         * 自定义样式设置
         *
         * @param style 样式
         */
        @Override
        public void accept(CellStyle style) {

        }
    }

    private static class DefCSS {

        private final boolean dft;
        private final String classname;
        private final CellStyleBuilder builder;

        private DefCSS(AttrConfig config, DefinitionStyle style, boolean onProperty) {
            String classname = style.classname();
            Attribute attr = config.getAttribute();
            this.dft = "".equals(classname) && onProperty;
            if (this.dft) {
                classname = attr.getName();
            }
            this.classname = classname;

            CellStyleBuilder builder = null;
            Class<? extends CellStyleBuilder> builderCls = style.createBy();
            if (builderCls == CellStyleBuilder.class) {

            } else {
                ParserUtil.checkValidImplClass(builderCls, CellStyleBuilder.class);
                if (!ParserUtil.isExpectCached(builderCls)) {
                    throw new IllegalStateException("实现类不能是成员内部类，只能是普通类或静态内部类");
                }
                builder = ClassUtil.newInstance(builderCls);
            }
            this.builder = builder;
        }

        public boolean isDefault() { return dft; }
    }

    private static class NormalBuilder implements CellStyleBuilder {

        @Override
        public void accept(CellStyle style) {

        }
    }
}
