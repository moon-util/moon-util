package com.moon.data.accessor;

import com.moon.data.annotation.Accessor;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author benshaoye
 */
class DataAccessorImplTestTest {

    public interface UsernameGetter {

        String getId();

        String getUsername();
    }

    @Data
    public static class UserEntity implements UsernameGetter {

        private String id;
        private String username;
        private String address;
    }

    @Accessor
    public interface UserAccessor {

        // select username from t_user where id = ?
        UsernameGetter findById(String id);

        // select username from t_user where id = ?
        String findUsernameById(String id);

        UsernameGetter findByUsernameAndAgeGtAndAddressStartsWith();

        // update t_user set username = ? where id = ?
        void updateById(UsernameGetter getter);


    }

    public static class Selector<T> {

        public static <T> Selector<T> on(Class<T> domainClass) {
            throw new UnsupportedOperationException();
        }

        public T fetch() {
            return null;
        }

        public List<T> list() {
            return null;
        }
    }

    interface SerialFunc<T, R> extends Function<T, R>, Serializable {}

    static class Select<T> {

        public static <T> Select<T> from(Class<T> domainClass) {
            return null;
        }

        public Select<T> column(SerialFunc<T, ?> column) {
            return this;
        }

        @SafeVarargs
        public final Select<T> columns(SerialFunc<T, ?> column, SerialFunc<T, ?>... columns) {
            return this;
        }
    }

    static public class JavaClassObject extends SimpleJavaFileObject {

        /**
         * Compiler编译后的byte数据会存在这个ByteArrayOutputStream对象中，
         * 后面可以取出，加载到JVM中。
         */
        private ByteArrayOutputStream byteArrayOutputStream;

        public JavaClassObject(String className, Kind kind) {
            super(URI.create("string:///" + className.replaceAll("\\.", "/") + kind.extension), kind);
            this.byteArrayOutputStream = new ByteArrayOutputStream();
        }

        /**
         * 覆盖父类SimpleJavaFileObject的方法。
         * 该方法提供给编译器结果输出的OutputStream。
         * <p>
         * 编译器完成编译后，会将编译结果输出到该 OutputStream 中，我们随后需要使用它获取编译结果
         *
         * @return
         *
         * @throws IOException
         */
        @Override
        public OutputStream openOutputStream() throws IOException {
            return this.byteArrayOutputStream;
        }

        /**
         * FileManager会使用该方法获取编译后的byte，然后将类加载到JVM
         */
        public byte[] getBytes() {
            return this.byteArrayOutputStream.toByteArray();
        }
    }

    static public class CharSequenceJavaFileObject extends SimpleJavaFileObject {

        //表示java源代码
        private CharSequence content;

        protected CharSequenceJavaFileObject(String className, String content) {
            super(URI.create("string:///" + className.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        /**
         * 获取需要编译的源代码
         *
         * @param ignoreEncodingErrors
         *
         * @return
         *
         * @throws IOException
         */
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return content;
        }
    }

    static public class ClassFileManager extends ForwardingJavaFileManager {

        /**
         * 存储编译后的代码数据
         */
        private JavaClassObject classJavaFileObject;

        protected ClassFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        /**
         * 编译后加载类
         * <p>
         * 返回一个匿名的SecureClassLoader:
         * 加载由JavaCompiler编译后，保存在ClassJavaFileObject中的byte数组。
         */
        @Override
        public ClassLoader getClassLoader(Location location) {
            return new SecureClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    byte[] bytes = classJavaFileObject.getBytes();
                    return super.defineClass(name, bytes, 0, bytes.length);
                }
            };
        }

        /**
         * 给编译器提供JavaClassObject，编译器会将编译结果写进去
         */
        @Override
        public JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling
        ) throws IOException {
            this.classJavaFileObject = new JavaClassObject(className, kind);
            return this.classJavaFileObject;
        }
    }


    public static class DynamicCompiler {

        private JavaFileManager fileManager;

        public DynamicCompiler() {
            this.fileManager = initManger();
        }

        private JavaFileManager initManger() {
            if (fileManager != null) {
                return fileManager;
            } else {
                JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
                DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
                fileManager = new ClassFileManager(
                    javaCompiler.getStandardFileManager(diagnosticCollector, null, null));
                return fileManager;
            }
        }

        /**
         * 编译源码并加载，获取Class对象
         *
         * @param fullName
         * @param sourceCode
         *
         * @return
         *
         * @throws ClassNotFoundException
         */
        public Class compileAndLoad(String fullName, String sourceCode) throws ClassNotFoundException {
            JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            List<JavaFileObject> javaFileObjectList = new ArrayList<JavaFileObject>();
            javaFileObjectList.add(new CharSequenceJavaFileObject(fullName, sourceCode));
            boolean result = javaCompiler.getTask(null, fileManager, null, null, null, javaFileObjectList).call();
            if (result) {
                return this.fileManager.getClassLoader(null).loadClass(fullName);
            } else {
                return Class.forName(fullName);
            }
        }

        /**
         * 关闭fileManager
         *
         * @throws IOException
         */
        public void closeFileManager() throws IOException {
            this.fileManager.close();
        }
    }

    @Test
    void testDisableById() throws
        IOException,
        ClassNotFoundException,
        NoSuchMethodException,
        IllegalAccessException,
        InvocationTargetException,
        InstantiationException {
        StringBuilder src = new StringBuilder();
        src.append("package win.hgfdodo.compiler;");
        src.append("public class DynaClass {\n");
        src.append("    public String toString() {\n");
        src.append("        return \"Hello, I am \" + ");
        src.append("this.getClass().getSimpleName();\n");
        src.append("    }\n");
        src.append("}\n");

        String fullName = "win.hgfdodo.compiler.DynaClass";

        DynamicCompiler compiler = new DynamicCompiler();
        Class clz = compiler.compileAndLoad(fullName, src.toString());

        System.out.println(clz.getConstructor().newInstance());
        compiler.closeFileManager();

        // JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //
        // System.out.println(getClass().getResource("").getFile());
        // StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        // JavaFileObject testFile = generateTest();
        // Iterable<? extends JavaFileObject> classes = Collections.singletonList(testFile);
        // JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, classes);
        // if (task.call()) {
        //     System.out.println("success");
        // } else {
        //     System.out.println("failure!");
        // }
    }

    private static JavaFileObject generateTest() {
        String contents = new String("package com.moon;" +
            "public class CalcTest {\n" +
            "  public void testMultiply() {\n" +
            "    System.out.println(\"======>  \"+(2+4));\n" +
            "  }\n" +
            "  public static void main(String[] args) {\n" +
            "    CalcTest ct = new CalcTest();\n" +
            "    ct.testMultiply();\n" +
            "  }\n" +
            "}\n");
        Dft so = null;
        try {
            so = new Dft("com.moon.CalcTest", contents);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return so;
    }


    static class Dft extends SimpleJavaFileObject {

        String content;

        protected Dft(String classname, String content) throws URISyntaxException {
            super(new URI(classname), Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return content;
        }
    }
}