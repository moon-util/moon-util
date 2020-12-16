package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class ExcelUtilTestTest {

    @Test
    void testCreateRow() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row1 = sheet.createRow(0);
        Row row2 = sheet.createRow(0);
        System.out.println(row1 == row2);
    }

    @Test
    @Disabled
    void testXlsx() {
        ExcelWriter excelFactory = ExcelUtil.xlsx();
        excelFactory.sheet("招聘进度分析", sheetFactory -> {
            sheetFactory.definitionStyle("header", (style, font) -> {
                font.setColor(IndexedColors.RED.index);
                style.setFont(font);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillBackgroundColor(IndexedColors.BLUE_GREY.index);
            });
            sheetFactory.definitionComment("default", comment -> {
                comment.setString(new XSSFRichTextString("你好啊，小伙子"));
            });
            sheetFactory.row(rowFactory -> {});
            sheetFactory.row(rowFactory -> {
                rowFactory.style("header");

                rowFactory.next("");
                rowFactory.next("");
                rowFactory.next("学历情况", 1, 6);
                rowFactory.next("年龄结构", 1, 4);
                rowFactory.next("政治面貌", 1, 3);
                rowFactory.next("性别结构", 1, 2);
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.style("header");

                rowFactory.next("岗位名称");
                rowFactory.next("申请人数");
                rowFactory.next("硕士研究生");
                rowFactory.next("普通高中");
                rowFactory.next("博士研究生");
                rowFactory.next("初中级以下");
                rowFactory.next("大学本科");
                rowFactory.next("大专");
                rowFactory.next("20岁以下");
                rowFactory.next("31-40（岁）");
                rowFactory.next("21-30（岁）");
                rowFactory.next("40岁以上");
                rowFactory.next("党员");
                rowFactory.next("群众");
                rowFactory.next("团员");
                // rowFactory.next("男性");
                rowFactory.newCell().val("男性").commentAs("default");
                rowFactory.newCell().val("女性").commentAs("default");
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.newCell(cellFactory -> cellFactory.val("女性"));
                rowFactory.newCell().val(0).comment("今天是个好日子");
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.newCell(cellFactory -> cellFactory.val("女性"));
                rowFactory.newCell(1, 1, -1).val(0).comment("今天是个好日子");
            });
        }).finish().write("D:/test.xlsx");
        // excelFactory.shee
    }

    @Test
    @Disabled
    void testExportExcel() {
        ExcelUtil.xlsx().definitionStyle("header", (style, font) -> {
            style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            style.setBorderBottom(BorderStyle.DASH_DOT);

            font.setBold(true);
            font.setColor(IndexedColors.RED.index);
        }).sheet(sheetFactory -> {
            sheetFactory.row(rowFactory -> {
                rowFactory.newCell(3, 3).val("值").styleAs("header");
                rowFactory.newCell().styleAs("header");
                rowFactory.newCell(2, 2).val("值").styleAs("header");
                rowFactory.newCell().active();
            });
        }).finish().write("D:/test.xlsx");
    }

    @Test
    @Disabled
    void testLoadExcelFile() throws Exception {
        ExcelUtil.load("D:/test000.xlsx").sheet("招聘进度分析", false, sheetFactory -> {
            sheetFactory.row(rowFactory -> {
                for (int i = 0; i < 12; i++) {
                    rowFactory.next("电脑" + i);
                }
            });
        }).write("D:/test001.xlsx");
    }

    @Test
    @Disabled
    void testLoadExcelFile4Cell() throws Exception {
        ExcelUtil.load("D:/test001.xlsx").sheet("招聘进度分析", sheetFactory -> {
            final int count = 10;
            sheetFactory.useRow(9, false, rowFactory -> {
                for (int i = 0; i < count; i++) {
                    rowFactory.useCell(i).cloneStyleAs("style" + i);
                }
            });
            for (int j = 0; j < count; j++) {
                sheetFactory.row(rowFactory -> {
                    for (int i = 0; i < count; i++) {
                        rowFactory.newCell().val("电脑").styleAs("style" + i);
                    }
                });
                sheetFactory.row(rowFactory -> {
                    for (int i = 0; i < count; i++) {
                        rowFactory.newCell().val("奖品").styleAs("style" + i);
                    }
                });
            }
        }).write("D:/test002.xlsx");
    }
}