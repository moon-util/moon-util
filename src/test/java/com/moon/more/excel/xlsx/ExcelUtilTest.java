package com.moon.more.excel.xlsx;

import com.moon.core.enums.SystemProps;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;
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
 * @author benshaoye
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

    public static class BasicInfo {

        @TableColumn
        // @TableColumn({"姓名"})
        private String name = "张三";

        @TableColumn
        // @TableColumn({"年龄"})
        private int age = nextInt(18, 25);

        @TableColumn
        // @TableColumn({"性别"})
        private String sex = nextBoolean() ? "男" : "女";

        @TableColumn
        // @TableColumn({"居住地址"})
        private String address = ADDRESS.get(nextInt(0, ADDRESS.size()));

        @TableColumn
        // @TableColumn({"邮政编码"})
        private String zip = RandomStringUtil.nextDigit(6);
    }

    public static class ScoreCompare {

        @TableColumn
        // @TableColumn({"上次得分"})
        private int prev = nextInt(59, 95);

        @TableColumn
        // @TableColumn({"此次得分"})
        private int curr = nextInt(59, 95);

        public int getPrev() { return prev; }

        public int getCurr() { return curr; }
    }

    public static class Score {

        @TableColumnGroup()
        private ScoreCompare chinese = new ScoreCompare();

        @TableColumnGroup({"数学"})
        private ScoreCompare math = new ScoreCompare();

        @TableColumnGroup({"英语"})
        private ScoreCompare english = new ScoreCompare();

        @TableColumnGroup(value = {"物理"})
        private ScoreCompare score1 = new ScoreCompare();

        @TableColumnGroup({"化学"})
        private ScoreCompare score2 = new ScoreCompare();

        @TableColumn
        // @TableColumn(value = {"上次", "总分"}, offset = 1, offsetOnFull = true)
        public int getPrevTotal() {
            return toTotal(ScoreCompare::getPrev);
        }

        @TableColumn
        // @TableColumn(value = {"此次总分"}, order = 2)
        public int getThisTotal() {
            return toTotal(ScoreCompare::getCurr);
        }

        @TableColumn
        // @TableColumn(order = 1, offset = 1)
        public int getDiffTotal() {
            return getThisTotal() - getPrevTotal();
        }

        private int toTotal(ToIntFunction<ScoreCompare> mapper) {
            return ListUtil.newArrayList(chinese, math, english, score1, score2).stream().mapToInt(mapper).sum();
        }
    }

    public static class FeeDetail {

        @TableColumn
        // @TableColumn({"基本工资"})
        private int basicSalary = nextInt(5000, 10000);

        @TableColumn
        // @TableColumn({"岗位工资"})
        private int jobSalary = nextInt(500, 1000);

        @TableColumn
        // @TableColumn({"业务补贴"})
        private int businexx = nextInt(200, 800);

        @TableColumn
        // @TableColumn({"健康补贴"})
        private int health = nextInt(200, 800);

        @TableColumn
        // @TableColumn({"交通补贴"})
        private int traffic = nextInt(200, 800);
    }

    public static class Member {

        @TableColumn
        // @TableColumn({"部门"})
        private String department = "市场部";

        @TableColumnGroup({"基本信息"})
        private BasicInfo info = new BasicInfo();

        @TableColumn
        // @TableColumn({"基本信息", "身体状况"})
        private String status = nextBoolean() ? "健康" : "其他";

        @TableColumnGroup({"得分情况"})
        private Score score = new Score();

        @TableColumnGroup({"应发项目"})
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
    @ValueSource(strings = "D:/")
    void testExportMultiExcelOnMax() throws Exception {
        doExportMultiExcel(SystemProps.user_home.get());
    }

    void doExportMultiExcel(String dir) throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> sheetFactory.table(tableFactory -> {
            List<Member> members = ListUtil.newArrayList();
            for (int i = 0; i < 12; i++) {
                members.add(new Member());
            }
            tableFactory.renderAll(members);
        })).write(new File(dir, "member-score.xlsx"));
    }
}
