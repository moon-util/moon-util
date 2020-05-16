package com.moon.more.excel.parse;

import com.moon.core.enums.SystemProps;
import com.moon.core.util.DateUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.WorkbookFactory;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class MarkCollectTestTest extends ParseUtil {

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
        File rootPath = new File(SystemProps.user_home.get(), "test" + DateUtil.format() + ".xlsx");
        render().write2File(rootPath);
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
     *
     * 包括在自身字段和对应 getter setter 方法上
     *
     * 但可以同时注解相同类型，如：字段自身和 getter 方法都注解 @TableColumnFlatten
     * 或字段自身和 getter 方法都注解 @TableColumn（对应 setter 方法一样）
     *
     * 同时注解相同类型优先以 method 上的注解为准
     *
     *
     * getter 上的注解只用于导出；
     * setter 上的注解只用于导入；
     * field 上的注解可同时用于导出或导入；
     *
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
     *
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
     *
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
     *
     * 如：Iterable、Iterator、Map、数组 等
     *
     * 其中 Map 默认取 value
     *
     *
     * 同一个类最多只能有一个集合字段包括：
     *
     * 1. 自身最多只能有一个集合字段
     *
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
}