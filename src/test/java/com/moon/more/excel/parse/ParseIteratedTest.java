package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author benshaoye
 */

public class ParseIteratedTest extends ParseUtil {

    public ParseIteratedTest() {
    }

    @Test
    void test1LayIterated() throws Exception {
        parse(Test1LayBean.class);
    }

    public static class Test1LayBean {

        @TableColumn
        private int age;

        @TableColumn
        private String sex;

        @TableColumn
        private List<String> names;
    }

    @Test
    void test1LayIteratedEntity() throws Exception {
        parse(Test1LayBeanEntity.class);
    }

    public static class Test1LayBeanEntity {

        @TableColumn
        private int age;

        @TableColumn
        private String sex;

        @TableColumn
        private List<Test2LayChild> names;
    }

    public static class Test2LayChild {

        private String schoolName;

        private String address;
    }

    @Test
    void test2LayIteratedEntity() throws Exception {
        parse(Test2LayBeanEntity.class);
    }

    public static class Test2LayBeanEntity {

        @TableColumn
        private int age;

        @TableColumn
        private String sex;

        @TableColumnFlatten
        private List<Test2LayChild> children;
    }
}
