package com.moon.poi.excel.full;

import com.moon.core.enums.Systems;
import com.moon.poi.excel.ExcelUtil;
import com.moon.poi.excel.annotation.SheetColumn;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;

/**
 * 简单表头测试
 *
 * @author moonsky
 */
public class HeaderForBasicTest {

    /**
     * 简单表头
     */
    public static class DemoForExcelHead_0 {

        @SheetColumn("姓名")
        private String name;
        @SheetColumn("年龄")
        private String age;
        @SheetColumn("家庭住址")
        private String address;
    }

    /**
     * 简单表头 + 列排序
     *
     * @see SheetColumn#order() order 按大小排序，相同 order 通常按声明顺序排序
     */
    public static class DemoForExcelHead_0_0 {

        @SheetColumn(value = "姓名", order = 1)
        private String name;
        @SheetColumn(value = "年龄", order = -1)
        private String age;
        @SheetColumn("家庭住址")
        private String address;
    }

    /**
     * 简单表头 + 列偏移
     */
    public static class DemoForExcelHead_0_1 {

        @SheetColumn("姓名")
        private String name;
        @SheetColumn("年龄")
        private String age;
        @SheetColumn(value = "家庭住址", offset = 2)
        private String address;
    }

    /**
     * 简单表头 + 行高
     */
    public static class DemoForExcelHead_0_2 {

        @SheetColumn(value = "姓名", rowsHeight4Head = 1000)
        private String name;
        @SheetColumn("年龄")
        private String age;
        @SheetColumn("家庭住址")
        private String address;
    }

    /**
     * 多级表头 1
     */
    public static class DemoForExcelHead_1 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;
    }

    /**
     * 多级表头 1 + 列排序
     */
    public static class DemoForExcelHead_1_0 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn(value = {"基本信息", "家庭住址"}, order = -1)
        private String address;
    }

    /**
     * 多级表头 2
     */
    public static class DemoForExcelHead_2 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn({"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn({"成绩", "数学得分"})
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;
    }

    /**
     * 多级表头 2 + 列偏移 1
     */
    public static class DemoForExcelHead_2_1 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn(value = {"成绩", "语文得分"}, offset = 1)
        private String ChineseScore;
        @SheetColumn({"成绩", "数学得分"})
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;
    }

    /**
     * 多级表头 2 + 列偏移 2
     */
    public static class DemoForExcelHead_2_2 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn(value = {"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn(value = {"成绩", "数学得分"}, offset = 2)
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;
    }

    /**
     * 多级表头 2 + 列偏移 2: 表头所有行均参与偏移
     */
    public static class DemoForExcelHead_2_3 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn(value = {"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn(value = {"成绩", "数学得分"}, offset = 2,
            // 这里表头一共两行，所以只要 offsetHeadRows 大于等于 2，所有表头行都会偏移
            offsetHeadRows = 2)
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;
    }

    /**
     * 多级表头 2 + 列偏移 2: 表头所有行都不参与偏移
     */
    public static class DemoForExcelHead_2_4 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn(value = {"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn(value = {"成绩", "数学得分"}, offset = 2,
            // 这里表头一共两行，所以只要 offsetHeadRows 大于等于 2，所有表头行都会偏移
            offsetHeadRows = 0)
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;
    }

    /**
     * 多级表头 3
     */
    public static class DemoForExcelHead_3 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn({"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn({"成绩", "数学得分"})
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;

        @SheetColumn({"总分"})
        private int totalScore;
    }

    /**
     * 多级表头 3 + 列排序
     */
    public static class DemoForExcelHead_3_0 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn({"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn({"成绩", "数学得分"})
        private String mathScore;
        @SheetColumn({"成绩", "英语得分"})
        private String EnglishScore;

        @SheetColumn(value = {"总分"}, order = -1)
        private int totalScore;
    }

    /**
     * 多级表头 3 + 列排序 + 行高
     */
    public static class DemoForExcelHead_3_1 {

        @SheetColumn({"基本信息", "姓名"})
        private String name;
        @SheetColumn({"基本信息", "年龄"})
        private String age;
        @SheetColumn({"基本信息", "家庭住址"})
        private String address;

        @SheetColumn({"成绩", "语文得分"})
        private String ChineseScore;
        @SheetColumn({"成绩", "数学得分"})
        private String mathScore;
        @SheetColumn(value = {"成绩", "英语得分"}, rowsHeight4Head = {-1, 1000})
        private String EnglishScore;

        @SheetColumn(value = {"总分"}, order = -1)
        private int totalScore;
    }


    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExportHeadOnWin() throws Exception {
        doExportMultiExcel("D:/");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExportHeadOnMac() throws Exception {
        doExportMultiExcel(Systems.user_home.get());
    }

    void doExportMultiExcel(String dir) throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.definitionStyle("placeholder", style -> {
                style.setBorderRight(BorderStyle.MEDIUM);
            });

            sheetFactory.row(rowFactory -> {
                rowFactory.newCell().width(6400);
            }).row(rowFactory -> {
                rowFactory.newCell().val("简单表头").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_0.class);
            });

            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单表头 + 列排序").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_0_0.class);
            });

            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单表头 + 列偏移").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_0_1.class);
            });

            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单表头 + 行高").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_0_2.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 1").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_1.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 1 + 列排序").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_1_0.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 2").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_2.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 2 + 列偏移 1").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_2_1.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 2 + 列偏移 2").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_2_2.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 2 + 列偏移 3: 表头所有行均参与偏移").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_2_3.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 2 + 列偏移 3: 表头所有行都不参与偏移").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_2_4.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 3").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_3.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("简单多级表头 3 + 列排序").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_3_0.class);
            });


            sheetFactory.row(rowFactory -> {}).row(rowFactory -> {
                rowFactory.newCell().val("多级表头 3 + 列排序 + 行高").styleAs("placeholder");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.render(DemoForExcelHead_3_1.class);
            });
        }).write(new File(dir, "member-score.xlsx"));
    }

    @Test
    void testName() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.row(rowFactory -> {
                rowFactory.newCell()
                    // .valImageUrl("http://a3.att.hudong.com/14/75/01300000164186121366756803686.jpg")
                    .width(8000);
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.newCell();//.valImageUrl("http://a0.att.hudong.com/56/12/01300000164151121576126282411.jpg");
            });
            sheetFactory.row(rowFactory -> {
                rowFactory.newCell();//.valImageUrl("http://a2.att.hudong.com/36/48/19300001357258133412489354717.jpg");
            });
        }).write("D:/export-image.xlsx");
    }
}
