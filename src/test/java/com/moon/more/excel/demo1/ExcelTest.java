package com.moon.more.excel.demo1;

import com.moon.core.enums.Systems;
import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;
import com.moon.more.excel.annotation.defaults.DefaultValue;
import com.moon.more.excel.annotation.format.DateTimePattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author moonsky
 */
public class ExcelTest {

    private final static List<String> ADDRESS = new ArrayList<>();
    private final static List<String> SCORE_NAMES = new ArrayList<>();

    static {
        ADDRESS.add("深圳市南山区脉动大厦");
        ADDRESS.add("陕西省渭南乡搞笑接到");
        ADDRESS.add("重庆市李子坝");
        ADDRESS.add("北京市三里屯");
        ADDRESS.add("成都市双流机场");
        ADDRESS.add("郑州市正定机场");

        SCORE_NAMES.add("语文");
        SCORE_NAMES.add("数学");
        SCORE_NAMES.add("英语");
        SCORE_NAMES.add("物理");
        SCORE_NAMES.add("生物");
        SCORE_NAMES.add("化学");
        SCORE_NAMES.add("政治");
        SCORE_NAMES.add("历史");
        SCORE_NAMES.add("地理");
    }


    @Test
    void testName() throws Exception {
        assertTrue(true);
    }

    public static class MemberInfo {

        @TableColumn({"基本信息", "姓名"})
        private final String name;
        @TableColumn({"基本信息", "年龄"})
        private final int age;
        @TableColumn(value = {"基本信息", "身份证号"},width = 5400)
        private final String certNo;
        @TableColumn(value = {"基本信息", "手机号码"},width = 3300)
        private final String mobile;
        @TableColumn(value = {"基本信息", "家庭住址"},width = 3000)
        private final String address;
        @TableColumn({"账户信息", "开户行"})
        private final String bankName;
        @TableColumn(value = {"账户信息", "银行卡号"}, width = 8000)
        private final String bankAccount;

        public MemberInfo() {
            this.name = RandomStringUtil.nextChinese(2, 6);
            this.age = RandomUtil.nextInt(18, 32);
            this.certNo = RandomStringUtil.nextDigit(18);
            this.mobile = 1 + RandomStringUtil.nextDigit(10);
            this.address = ADDRESS.get(RandomUtil.nextInt(0, ADDRESS.size()));
            this.bankName = RandomStringUtil.nextChinese(4, 10);
            this.bankAccount = RandomStringUtil.nextDigit(16, 19);
        }
    }


    public static class MemberScore {

        @TableColumn({"考试科目"})
        private final String scoreName;
        @DateTimePattern("yyyy-MM-dd")
        @TableColumn({"考试时间"})
        private final Date date;
        @TableColumn({"考试成绩"})
        private final int value;

        public MemberScore(String scoreName) {
            this.scoreName = scoreName;
            this.date = new Date();
            this.value = RandomUtil.nextInt(10, 150);
        }
    }

    public static class MemberDetail {

        @TableColumn({"人员基本信息"})
        @TableColumnGroup
        private MemberInfo info;

        @TableColumn({"成绩详情", "语文成绩"})
        @TableColumnGroup
        private MemberScore Chinese;
        @TableColumn(value = {"成绩详情", "数学成绩"}, offset = 3)
        @TableColumnGroup
        private MemberScore math;
        @TableColumn(value = {"成绩详情", "英语成绩"}, offsetHeadRows = Integer.MAX_VALUE - 1)
        @TableColumnGroup
        private MemberScore English;

        @TableColumn(value = {"成绩详情", "总分"}, width = 8000, offset = 3)
        private String scoreTotal;

        public MemberDetail() {
            this.info = new MemberInfo();
            this.Chinese = new MemberScore("语文");
            this.math = new MemberScore("数学");
            this.English = new MemberScore("英语");
        }

        public int getScoreTotal() {
            return ArrayUtil.sum(ArrayUtil.toArray(Chinese, math, English), score -> score.value);
        }
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExportExcelDetailOnWin() throws Exception {
        doExportExcelDetail("D:/");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExportExcelDetailOnMac() throws Exception {
        doExportExcelDetail(Systems.user_home.value());
    }

    void doExportExcelDetail(String dir) {
        ExcelUtil.xlsx().sheet("MemberDetail", sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<MemberDetail> details = new ArrayList<>();
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                details.add(new MemberDetail());
                tableFactory.renderAll(details);
            });
        }).write(new File(dir, "member-detail.xlsx"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExportExcelHeadOnWin() throws Exception {
        doExportExcelHead("D:/");
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testExportExcelHeadOnMac() throws Exception {
        doExportExcelHead(Systems.user_home.value());
    }

    void doExportExcelHead(String dir) {
        ExcelUtil.xlsx().sheet("MemberDetail", sheetFactory -> {
            sheetFactory.row(2);
            sheetFactory.table(tableFactory -> {
                tableFactory.renderHead(MemberDetail.class);
            });
        }).sheet("MemberInfo", sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderHead(MemberInfo.class);
            });
        }).sheet("MemberScore", sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderHead(MemberScore.class);
            });
        }).write(new File(dir, "member-detail.xlsx"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testBaseExportExcelOnWin() throws Exception {
        doBaseExportExcel("D:/");
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testBaseExportExcelOnMac() throws Exception {
        doBaseExportExcel(Systems.user_home.value());
    }

    void doBaseExportExcel(String dir) {
        ExcelUtil.xlsx().sheet("First Sheet", sheetFactory -> {
            sheetFactory.row(1, rowFactory -> {
                rowFactory.next("11.2", 2, 2);
                rowFactory.next("11.3", 1, 3);
            }).row(rowFactory -> {
                rowFactory.next("11.4");
                rowFactory.next("11.5");
                rowFactory.next("11.6");
            });
        }).write(new File(dir, "base-exported.xlsx"));
    }
}
