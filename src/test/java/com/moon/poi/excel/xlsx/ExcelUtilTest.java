package com.moon.poi.excel.xlsx;

import com.moon.core.enums.Systems;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.poi.excel.ExcelUtil;
import com.moon.poi.excel.annotation.TableColumn;
import com.moon.poi.excel.annotation.TableColumnGroup;
import com.moon.poi.excel.annotation.value.DefaultNumber;
import com.moon.poi.excel.annotation.value.DefaultValue;
import com.moon.poi.excel.annotation.format.DateTimePattern;
import com.moon.poi.excel.annotation.format.NumberPattern;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import com.moon.poi.excel.annotation.style.StyleFontBuilder;
import com.moon.poi.excel.annotation.style.HeadStyle;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static com.moon.core.util.RandomUtil.nextBoolean;
import static com.moon.core.util.RandomUtil.nextInt;

/**
 * @author moonsky
 */
class ExcelUtilTest {

    private final static List<String> ADDRESS = new ArrayList<>();

    static {
        ADDRESS.add("深圳市南山区脉动大厦");
        ADDRESS.add("陕西省渭南乡搞笑接到");
        ADDRESS.add("重庆市李子坝");
        ADDRESS.add("北京市三里屯");
        ADDRESS.add("成都市双流机场");
        ADDRESS.add("郑州市正定机场");
    }

    public static class BackgroundColor implements StyleFontBuilder {

        @Override
        public void accept(CellStyle style, Font font) {
            font.setBold(true);
            font.setColor(IndexedColors.RED.index);
            style.setAlignment(HorizontalAlignment.RIGHT);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.AQUA.index);
            style.setFont(font);
        }
    }

    @DefinitionStyle(classname = "name",
        align = HorizontalAlignment.RIGHT,
        fillPattern = FillPatternType.SOLID_FOREGROUND,
        foregroundColor = 49)
    public static class BasicInfo {

        // @TableColumn
        @HeadStyle("name")
        @TableColumn({"姓名"})
        private String name = "张三";

        // @TableColumn
        @TableColumn({"年龄"})
        private int age = nextInt(18, 25);

        // @TableColumn
        @TableColumn({"性别"})
        @DefinitionStyle(classname = "sex", createBy = BackgroundColor.class)
        private String sex = nextBoolean() ? "男" : "女";

        // @TableColumn
        @TableColumn(value = {"居住地址"}, width = 4000)
        @HeadStyle("sex")
        private String address = ADDRESS.get(nextInt(0, ADDRESS.size()));

        // @TableColumn
        @TableColumn({"邮政编码"})
        private String zip = RandomStringUtil.nextDigit(6);
    }

    public static class ScoreCompare {

        // @TableColumn
        @NumberPattern(value = "$##.00", minIntDigit = 4, minFractionDigit = 4)
        @TableColumn({"上次得分"})
        private int prev = nextInt(59, 95);

        // @TableColumn
        @TableColumn({"此次得分"})
        private int curr = nextInt(59, 95);

        public int getPrev() { return prev; }

        public int getCurr() { return curr; }
    }

    public static class Score {

        @TableColumn("语文")
        @TableColumnGroup
        private ScoreCompare chinese = new ScoreCompare();

        @TableColumn(value = {"数学"})
        @TableColumnGroup
        private ScoreCompare math = new ScoreCompare();

        @TableColumn({"英语"})
        @TableColumnGroup
        private ScoreCompare english = new ScoreCompare();

        @TableColumn(value = {"物理"})
        @TableColumnGroup
        private ScoreCompare score1 = new ScoreCompare();

        @TableColumn({"化学"})
        @TableColumnGroup
        private ScoreCompare score2 = new ScoreCompare();

        // @TableColumnOffset(1)
        // @TableColumn
        @TableColumn(value = {"分数统计", "上次总分"}, offset = 0)
        public int getPrevTotal() {
            return toTotal(ScoreCompare::getPrev);
        }

        // @TableColumn
        @TableColumn(value = {"分数统计", "此次总分"}, order = 0)
        public int getThisTotal() {
            return toTotal(ScoreCompare::getCurr);
        }

        // @TableColumnOffset(1)
        // @TableColumn
        @TableColumn(value = {"分数统计", "分数变化"}, order = 1, offset = 2, offsetHeadRows = 1)
        public int getDiffTotal() {
            return getThisTotal() - getPrevTotal();
        }

        private int toTotal(ToIntFunction<ScoreCompare> mapper) {
            return ListUtil.newList(chinese, math, english, score1, score2).stream().mapToInt(mapper).sum();
        }
    }

    public static class FeeDetail {

        @TableColumn(value = "基本工资", rowsHeight4Head = 800, width = 0)
        // @TableColumn({"基本工资"})
        private int basicSalary = nextInt(5000, 10000);

        // @TableColumn
        @TableColumn(value = {"岗位工资"})
        private int jobSalary = nextInt(500, 1000);

        // @TableColumn
        @TableColumn({"业务补贴"})
        private int business = nextInt(200, 800);

        // @TableColumn
        @TableColumn({"健康补贴"})
        private int health = nextInt(200, 800);

        // @TableColumn
        @TableColumn({"交通补贴"})
        private int traffic = nextInt(200, 800);

        @DefaultNumber(value = 0, when = {DefaultNumber.Strategy.NULL, DefaultNumber.Strategy.NEGATIVE})
        @TableColumn({"应发合计"})
        public Integer getSalary() {
            return null;
        }
    }

    public static class Member {

        // @TableColumn
        @TableColumn(value = {"部门"})
        private String department = "市场部";

        @DateTimePattern("yyyy-MM-dd")
        // @TableColumn("导出日期")
        private DateTime datetime = DateTime.now();
        // private Date now = new Date();
        // private LocalDateTime time = LocalDateTime.now();

        @TableColumn({"基本信息"})
        @TableColumnGroup
        private BasicInfo info = new BasicInfo();

        // @TableColumn
        @TableColumn({"基本信息", "身体状况"})
        private String status = nextBoolean() ? "健康" : "其他";

        @DefaultValue("<空>")
        @TableColumn(value = {"值", "纸质", "侄子", "侄子的侄子"}, offset = 2)
        private String value;

        @TableColumn({"得分情况"})
        @TableColumnGroup
        private Score score = new Score();

        @TableColumn(value = {"应发项目"}, rowsHeight4Head = {400, 800})
        @TableColumnGroup
        private FeeDetail detail;

        public Member() { this.detail = nextBoolean() ? new FeeDetail() : null; }
    }

    @EnabledOnOs(OS.WINDOWS)
    @ParameterizedTest
    @ValueSource(strings = "D:/")
    void testExportMultiExcelOnWindows(String dir) throws Exception {
        doExportMultiExcel(dir);
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testExportMultiExcelOnMax() throws Exception {
        doExportMultiExcel(Systems.user_home.get());
    }

    void doExportMultiExcel(String dir) throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.definitionStyle("header", style -> {
                style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            });
            sheetFactory.table(tableFactory -> {
                List<Member> members = ListUtil.newList();
                for (int i = 0; i < 12; i++) {
                    members.add(new Member());
                }
                tableFactory.render(members);
            });
        }).write(new File(dir, "member-score.xlsx"));
    }
}
