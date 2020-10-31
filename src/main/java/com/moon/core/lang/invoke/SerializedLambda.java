package com.moon.core.lang.invoke;

import com.moon.core.io.IOUtil;
import com.moon.core.lang.ClassUtil;
import com.moon.core.util.function.SerializableFunction;

import java.io.*;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public class SerializedLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;

    private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;

    public static SerializedLambda resolve(SerializableFunction<?, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new IllegalStateException("仅支持传入 lambda 表达式");
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(IOUtil.serialize(lambda));
        try (ObjectInputStream inStream = new LambdaObjectInputStream(stream)) {
            return (SerializedLambda) inStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException("解析 Lambda 表达式错误");
        }
    }

    public String getFunctionalInterfaceClassName() { return functionalInterfaceClass; }

    public String getImplClassName() { return implClass; }

    public Class getImplClass() {
        return ClassUtil.forName(formatClassName(getImplClassName()));
    }

    public String getImplMethodName() { return implMethodName; }

    private String formatClassName(String classpath) {
        return classpath.replace('/', '.');
    }

    @Override
    public String toString() {
        String interfaceName = getFunctionalInterfaceClassName();
        String implName = getImplClassName();
        return String.format("%s -> %s::%s",
            interfaceName.substring(interfaceName.lastIndexOf('.') + 1),
            implName.substring(implName.lastIndexOf('.') + 1),
            implMethodName);
    }

    private static class LambdaObjectInputStream extends ObjectInputStream {

        public LambdaObjectInputStream(InputStream in) throws IOException { super(in); }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            Class<?> clazz;
            try {
                clazz = ClassUtil.forName(desc.getName());
            } catch (Exception ex) {
                clazz = super.resolveClass(desc);
            }
            return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
        }
    }
}
