package com.moon.processing.file;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 类级别方法管理器
 *
 * @author benshaoye
 */
public class ScopedMethods extends BaseScoped<JavaMethod> {

    public ScopedMethods(Importer importer, boolean inInterface) {
        super(importer, inInterface);
    }

    /**
     * 始终定义新的方法
     * <p>
     * 方法参数只有这里可以修改
     * <p>
     * 之后的修改只能增减注解、修改注释以及获取参数的一些信息
     *
     * @param methodName
     * @param parametersBuilder
     *
     * @return
     */
    public JavaMethod declareMethod(String methodName, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = newJavaMethod(methodName, parametersBuilder);
        getMemberMap().put(method.getUniqueKey(), method);
        return method;
    }

    /**
     * 如果已存在已定义的方法，就用已存在的，否则就新定义
     *
     * @param methodName
     * @param parametersBuilder
     *
     * @return
     */
    public JavaMethod useMethod(String methodName, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = newJavaMethod(methodName, parametersBuilder);
        Map<String, JavaMethod> methodsMap = getMemberMap();
        final String signature = method.getUniqueKey();
        JavaMethod present = methodsMap.get(signature);
        if (present != null) {
            return present;
        }
        methodsMap.put(signature, method);
        return method;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        List<JavaMethod> methods = new ArrayList<>(getMemberMap().values());
        methods.sort(JavaMethodComparator.SORTER);
        methods.forEach(method -> method.appendTo(addr));
    }

    private JavaMethod newJavaMethod(
        String methodName, Consumer<JavaParameters> parametersBuilder
    ) {
        JavaParameters parameters = new JavaParameters(getImporter());
        parametersBuilder.accept(parameters);
        return new JavaMethod(getImporter(), methodName, parameters, inInterface());
    }

    private enum JavaMethodComparator implements Comparator<JavaMethod> {
        /** sorter */
        SORTER;

        @Override
        public int compare(JavaMethod o1, JavaMethod o2) {
            boolean p1 = o1.isPropertyMethod(), p2 = o2.isPropertyMethod();
            return p1 == p2 ? compareStatic(o1, o2) : (p1 ? 1 : -1);
        }

        static int compareStatic(JavaMethod o1, JavaMethod o2) {
            boolean s1 = o1.isStatic(), s2 = o2.isStatic();
            return s1 == s2 ? 0 : (s1 ? 1 : -1);
        }
    }
}
