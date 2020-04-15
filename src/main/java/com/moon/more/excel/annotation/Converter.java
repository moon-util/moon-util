package com.moon.more.excel.annotation;

/**
 * java 实体字段数据类型和 Excel 单元格兼容数据类型的相互转换
 *
 * @param <F> java 实体对应字段类型
 * @param <T> CellType 兼容的数据类型：
 *            Date,LocalDate,LocalDateTime,
 *            Number,CharSequence,Calendar
 *
 * @author benshaoye
 */
public interface Converter<F, T> {

    /**
     * 转换
     * <p>
     * 将 java 数据类型转换为 excel 可接受的数据类型
     * <p>
     * 目标类型有：
     * <p>
     * Date,LocalDate,LocalDateTime,
     * <p>
     * Number,CharSequence,Calendar
     *
     * @param from
     *
     * @return
     */
    T convert(F from);

    /**
     * 从 Excel 文档读时，将读取的{@code cell value}转换为对应 java 字段兼容类型
     *
     * @param cellValue 读取到的{@code cell value}
     *
     * @return 与 java 字段兼容的类型
     */
    default F convertBackward(Object cellValue) {
        throw new UnsupportedOperationException("如果需要读 Excel 应实现[ convertBackward ]方法.");
    }
}
