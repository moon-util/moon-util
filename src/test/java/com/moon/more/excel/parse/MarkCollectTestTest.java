package com.moon.more.excel.parse;

import com.moon.core.enums.SystemProps;
import com.moon.core.io.FileUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.DateUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.WorkbookFactory;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class MarkCollectTestTest extends ParseUtil {

    private static String getFilepath() {
        return getFilepath(SystemProps.user_home.get());
    }

    private static String getFilepath(String exportDirectory) {
        if (StringUtil.isBlank(exportDirectory)) {
            return getFilepath();
        } else {
            File rootPath = new File(exportDirectory, DateUtil.format("'test'-yyyyMMdd-HHmmss.'xlsx'"));
            return rootPath.getAbsolutePath();
        }
    }

    @ToString
    public static class GroupedBody {

        private String name = "Body-张三";

        private SimpleBodyExport grouped = new SimpleBodyExport(1);
    }

    @ToString
    public static class GroupedDetail {

        @TableColumn
        private String name = "Detail-张三";
        @TableColumn
        private SimpleBodyExport grouped = new SimpleBodyExport(1);
    }

    public static class GroupedInformation {

        @TableColumn
        private String name = "Information-张三";
        @TableColumnFlatten
        private SimpleBodyExport grouped = new SimpleBodyExport(1);
    }

    <T> List<T> create(int count, Supplier<T> supplier) {
        List<T> list = ListUtil.newArrayList();
        for (int i = 0; i < count; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    public static class Group {

        @TableColumn
        private String name;

        @TableColumn
        private List<String> values;

        public Group() {
            this.name = "Group-张三";
            this.values = ListUtil.newArrayList(15, index -> RandomStringUtil.nextUpper(index + 3));
        }
    }

    public static class Group2 {

        @TableColumnFlatten
        private Group group = new Group();

        @TableColumn
        private String currentValue = "currentValue";
    }

    @ParameterizedTest
    @CsvSource("false,")
    void testGroupedListable(boolean deleteAfterExported, String exportDirectory) {
        String targetFile = getFilepath(exportDirectory);
        ExcelUtil.xlsx().sheet("第一个SHEET Listable", sheetFactory -> {
            // sheetFactory.table(tableFactory -> {
            //     tableFactory.renderBody(new Group());
            // });
            // sheetFactory.row(2);
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new Group2());
            });
        }).write2Filepath(targetFile);
        if (deleteAfterExported) {
            FileUtil.delete(new File(targetFile));
        }
        System.out.println(targetFile);
    }

    @ParameterizedTest
    @CsvSource("false,")
    void testExportGroupedBody(boolean deleteAfterExported, String exportDirectory) {
        String targetFile = getFilepath(exportDirectory);
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new GroupedBody());
            }).row();
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(create(5, GroupedBody::new));
            });
        }).sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new GroupedDetail());
            }).row();
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(create(6, GroupedDetail::new));
            });
        }).sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new GroupedInformation());
            }).row();
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(create(7, GroupedInformation::new));
            });
        }).write2Filepath(targetFile);
        if (deleteAfterExported) {
            System.out.println(targetFile);
            FileUtil.delete(new File(targetFile));
        }
    }

    @ToString
    public static class SimpleBodyExport {

        private String name = "张三";

        private final int index;

        public SimpleBodyExport(int index) {
            this.index = index;
        }
    }

    WorkbookFactory render() {
        List<SimpleBodyExport> exports = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            exports.add(new SimpleBodyExport(i));
        }
        return ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(exports);
            });
        });
    }

    /**
     * 如果类上一个列注解都没有，默认取所有基础数据类型字段为列
     *
     * @throws Exception
     */
    @Test
    void testExportSingle() throws Exception {
        String rootPath = getFilepath();
        render().write2Filepath(rootPath);
        System.out.println(rootPath);
    }

    public static class DuplicatedColumn {

        @TableColumn
        @TableColumnFlatten
        public String name;
        public String value;
    }

    public static class DuplicatedColumn1 {

        @TableColumn
        public String name;
        public String value;

        @TableColumnFlatten
        public String getName() {
            return name;
        }
    }

    /**
     * 同一字段不能同时注解 TableColumnFlatten TableColumn
     * <p>
     * 包括在自身字段和对应 getter setter 方法上
     * <p>
     * 但可以同时注解相同类型，如：字段自身和 getter 方法都注解 @TableColumnFlatten
     * 或字段自身和 getter 方法都注解 @TableColumn（对应 setter 方法一样）
     * <p>
     * 同时注解相同类型优先以 method 上的注解为准
     * <p>
     * <p>
     * getter 上的注解只用于导出；
     * setter 上的注解只用于导入；
     * field 上的注解可同时用于导出或导入；
     * <p>
     * 由于优先以 getter setter 方法上的注解为准，所以如上字段{@link DuplicatedColumn1#name}
     * 字段同时在 getter 方法和字段上注解，而 setter 方法不注解，导出将以 getter 方法为准
     * 导入将以 field 注解为准
     *
     * @throws Exception
     */
    @Test
    void testDuplicatedTableColumn() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                parse(DuplicatedColumn1.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
        assertThrows(Exception.class, () -> {
            try {
                parse(DuplicatedColumn.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    /*
     最多只能有一个集合字段
     */

    public static class Listable1 {

        public List<String> names;
        public List<String> values;
    }

    /**
     * 没有注解的集合字段永远不会被解析到
     * <p>
     * 其他集合字段包括还有：Iterable、Iterator、Map、数组 等
     *
     * @throws Exception
     */
    @Test
    void testListable1() throws Exception {
        Renderer renderer = parse(Listable1.class);
        assertNotNull(renderer);
        assertTrue(renderer instanceof MarkColumnGroup);

        MarkColumnGroup group = (MarkColumnGroup) renderer;
        assertEquals(0, group.getColumns().length);
    }

    public static class Listable2 {

        @TableColumn
        public List<String> names;
        @TableColumn
        public List<String> values;
    }

    /**
     * 同一个类里面最多只能有一个集合字段
     * <p>
     * 其他集合字段包括还有：Iterable、Iterator、Map、数组 等
     *
     * @throws Exception
     */
    @Test
    void testListable2() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable2.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class Listable3Child {

        @TableColumn
        public List<String> names;
        @TableColumn
        public List<String> values;
    }

    public static class Listable3 {

        @TableColumn
        public List<String> names;
        @TableColumnFlatten
        public Listable3Child child;
    }

    /**
     * 集合字段字需要字段自身具有"集合"性质即可
     * <p>
     * 如：Iterable、Iterator、Map、数组 等
     * <p>
     * 其中 Map 默认取 value
     * <p>
     * <p>
     * 同一个类最多只能有一个集合字段包括：
     * <p>
     * 1. 自身最多只能有一个集合字段
     * <p>
     * 2. 自身及自身所有后代一共最多只能有一个集合字段
     * （这里的后代指的是集合的实际类型是另一个类）
     *
     * @throws Exception
     */
    @Test
    void testListable3() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable3.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class Listable4Child {

        @TableColumn
        public List<String> names;
        @TableColumn
        public String value;
    }

    public static class Listable4 {

        @TableColumn
        public List<String> names;
        @TableColumnFlatten
        public Listable4Child child;
    }

    @Test
    void testListable4() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable4.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class Listable5Grandson {

        @TableColumn
        public List<Integer> scores;
        @TableColumn
        public String value;
    }

    public static class Listable5Child {

        @TableColumn
        public int names;
        @TableColumn
        public String value;
        @TableColumnFlatten
        public Listable5Grandson grandson;
    }

    public static class Listable5 {

        @TableColumn
        public List<String> names;
        @TableColumnFlatten
        public Listable5Child child;
    }

    @Test
    void testListable5() throws Exception {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(Listable5.class);
                System.out.println();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }

    public static class UnknownActualListableType {

        @TableColumn
        private String value;
        @TableColumn
        private List names;
    }

    @Test
    void testUnknownActualType() {
        assertThrows(Exception.class, () -> {
            try {
                Renderer renderer = parse(UnknownActualListableType.class);
                System.out.println();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        });
    }
}