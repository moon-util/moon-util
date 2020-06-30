package com.moon.core.lang.reflect;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.interfaces.ValueSupplier;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

/**
 * @author moonsky
 */
class ProxyUtilTestTest {

    @Test
    void testNewProxyInstance() {
        Student student = new Student();
        ValueSupplier supplier = ProxyUtil.newProxyInstance(student, (proxy, method, args) -> {
            out.println("before");
            Object ret = method.invoke(student, args);
            out.println("after");
            return ret;
        });

        out.println(supplier.getValue());
        ;
    }

    interface Getter<T> extends ValueSupplier<String> {

        T get();
    }

    static class Student implements Getter {

        private final String str = RandomStringUtil.next();

        public Student() {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>  " + str);
        }

        public String getStr() {
            return str;
        }

        @Override
        public Object get() {
            return str;
        }

        @Override
        public Object getValue() {
            return str;
        }
    }

    public static class StudentInvocationHandler implements InvocationHandler {

        private final Student student;

        public StudentInvocationHandler() {this(new Student());}

        public StudentInvocationHandler(Student student) {this.student = student;}

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //            System.out.println("+++++++++++++++++++++++++++++++++++  before");
            Object invoked = method.invoke(student, args);
            //            System.out.println("+++++++++++++++++++++++++++++++++++  after");
            return invoked;
        }
    }

    @Test
    void testJDKProxy() {
        Class[] interfaces = {Getter.class};
        Object o = Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new StudentInvocationHandler());
        Getter getter = (Getter) o;
        Object res = getter.get();
        System.out.println("===================================  " + res);
    }

    @Test
    void testJDKProxy1() {
        Student student = new Student();
        System.out.println(student.get());
        Class[] interfaces = {Getter.class};
        Object o = Proxy.newProxyInstance(getClass().getClassLoader(),
            interfaces,
            new StudentInvocationHandler(student));
        Getter getter = (Getter) o;
        Object res = getter.get();
        System.out.println("===================================  " + res);
    }

    Getter getByJdkProxy() {
        Student student = new Student();
        Class[] interfaces = {Getter.class};
        Object o = Proxy.newProxyInstance(getClass().getClassLoader(),
            interfaces,
            new StudentInvocationHandler(student));
        Getter getter = (Getter) o;
        return getter;
    }

    // public static class StudentMethodInterceptor implements MethodInterceptor {
    //
    //     private final Student student;
    //
    //     public StudentMethodInterceptor() {this.student = new Student();}
    //
    //     @Override
    //     public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    //         //            System.out.println("-----------------------------------  before");
    //         Object invoked = proxy.invoke(student, args);
    //         //            System.out.println("-----------------------------------  after");
    //         return invoked;
    //     }
    // }
    //
    // Getter getByCglib() {
    //     Enhancer enhancer = new Enhancer();
    //     enhancer.setSuperclass(Student.class);
    //     enhancer.setCallback(new StudentMethodInterceptor());
    //     Student student = (Student) enhancer.create();
    //     return student;
    // }

    // @Test
    // void testFirstMethod() {
    //     Getter getter = getByCglib();
    //     Object res = getter.get();
    //     System.out.println("===================================  " + res);
    // }

    @Test
    void testTimeZone() {
        Map<String, String> map = new HashMap<>(64);
        map.put("ACT", "Australia/Darwin");
        map.put("AET", "Australia/Sydney");
        map.put("AGT", "America/Argentina/Buenos_Aires");
        map.put("ART", "Africa/Cairo");
        map.put("AST", "America/Anchorage");
        map.put("BET", "America/Sao_Paulo");
        map.put("BST", "Asia/Dhaka");
        map.put("CAT", "Africa/Harare");
        map.put("CNT", "America/St_Johns");
        map.put("CST", "America/Chicago");
        map.put("CTT", "Asia/Shanghai");
        map.put("EAT", "Africa/Addis_Ababa");
        map.put("ECT", "Europe/Paris");
        map.put("IET", "America/Indiana/Indianapolis");
        map.put("IST", "Asia/Kolkata");
        map.put("JST", "Asia/Tokyo");
        map.put("MIT", "Pacific/Apia");
        map.put("NET", "Asia/Yerevan");
        map.put("NST", "Pacific/Auckland");
        map.put("PLT", "Asia/Karachi");
        map.put("PNT", "America/Phoenix");
        map.put("PRT", "America/Puerto_Rico");
        map.put("PST", "America/Los_Angeles");
        map.put("SST", "Pacific/Guadalcanal");
        map.put("VST", "Asia/Ho_Chi_Minh");

        map.forEach((key, value) -> {
            String name = StringUtil.replace(value, '_', '$');
            name = StringUtil.replace(name, '/', '_');
            String out = "/**\n * " + value + "\n */\n";
            out += name.toUpperCase() + ",";
            String now = "{\n@Override\npublic DateFormat with(String pattern){\n" + "return withTimeZone(pattern, \"" + value + "\");\n" + "}\n},";
            System.out.println(out);
        });
    }

    @Test
    void testName() throws Exception {
        out.println(((char) 48));
        out.println(0x00000001);
    }

    @Test
    void testProxy() {
        Getter getter1 = getByJdkProxy();
        Getter getter2 = getByJdkProxy();
        Getter getter3 = new Student();

        // getter1.get();
        getter2.get();

        final int count = 100000000;
        out.println("==============================");
        //        out.time("cglib:{}ms");
        for (int i = 0; i < count; i++) {
            getter1.get();
        }
        //        out.timeNext("jdk:{}ms");
        for (int i = 0; i < count; i++) {
            getter2.get();
        }
        //        out.timeNext("java:{}ms");
        for (int i = 0; i < count; i++) {
            getter3.get();
        }
        //        out.timeEnd();
        out.println("==============================");
        //        out.time("jdk:{}ms");
        for (int i = 0; i < count; i++) {
            getter2.get();
        }
        //        out.timeNext("java:{}ms");
        for (int i = 0; i < count; i++) {
            getter3.get();
        }
        //        out.timeNext("cglib:{}ms");
        for (int i = 0; i < count; i++) {
            getter1.get();
        }
        //        out.timeEnd();
        out.println("==============================");
        //        out.time("cglib:{}ms");
        for (int i = 0; i < count; i++) {
            getter1.get();
        }
        //        out.timeNext("java:{}ms");
        for (int i = 0; i < count; i++) {
            getter3.get();
        }
        //        out.timeNext("jdk:{}ms");
        for (int i = 0; i < count; i++) {
            getter2.get();
        }
        //        out.timeEnd();
        out.println("==============================");
        //        out.time("java:{}ms");
        for (int i = 0; i < count; i++) {
            getter3.get();
        }
        //        out.timeNext("jdk:{}ms");
        for (int i = 0; i < count; i++) {
            getter2.get();
        }
        //        out.timeNext("cglib:{}ms");
        for (int i = 0; i < count; i++) {
            getter1.get();
        }
        //        out.timeEnd();
    }
}