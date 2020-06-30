package com.moon.more.excel;

import java.io.File;
import java.util.Optional;

/**
 * @author moonsky
 */
abstract class LoadUtil {

    protected LoadUtil() { }

    /**
     * 推测 Excel 文件类型
     *
     * @param absoluteExcelFile excel 文件
     *
     * @return excel 文件类型
     *
     * @throws NullPointerException absoluteExcelFile is null
     */
    final static Optional<ExcelType> deduceType(File absoluteExcelFile) {
        return deduceType(absoluteExcelFile.getAbsolutePath());
    }

    /**
     * 推测绝对路径指向的 Excel 文件类型
     *
     * @param absoluteExcelFilepath excel 文件绝对路径
     *
     * @return excel 文件类型
     *
     * @throws NullPointerException absoluteExcelFilepath is null
     */
    final static Optional<ExcelType> deduceType(String absoluteExcelFilepath) {
        for (ExcelType value : ExcelType.values()) {
            if (value.test(absoluteExcelFilepath)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
