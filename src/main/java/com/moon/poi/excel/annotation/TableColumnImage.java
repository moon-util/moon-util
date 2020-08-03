package com.moon.poi.excel.annotation;

import com.moon.core.lang.Unsupported;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Unsupported("暂不支持以注解方式图片导出")
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnImage {

    /**
     * 图片来源
     *
     * @return
     */
    Source value() default Source.LOCAL;

    /**
     * 图片类型
     *
     * @return type
     */
    Type type() default Type.AUTO;

    enum Type {
        EMP(Workbook.PICTURE_TYPE_EMF),
        WMP(Workbook.PICTURE_TYPE_WMF),
        PICT(Workbook.PICTURE_TYPE_PICT),
        JPEG(Workbook.PICTURE_TYPE_JPEG),
        PNG(Workbook.PICTURE_TYPE_PNG),
        DIB(Workbook.PICTURE_TYPE_DIB),
        /**
         * 仅支持 JPEG 和 PNG
         */
        AUTO(-1);

        public final int pictureType;

        Type(int pictureType) {
            this.pictureType = pictureType;
        }
    }

    /**
     * 图片来源
     */
    enum Source {
        /**
         * 本地磁盘图片
         */
        LOCAL,
        /**
         * 网络图片
         */
        NETWORK,
        /**
         * 自动判断
         */
        AUTO,
    }
}
