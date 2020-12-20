package com.moon.data.jdbc.processing;

import com.moon.data.jdbc.annotation.Accessor;
import com.moon.data.jdbc.annotation.Provided;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.lang.model.element.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
final class AccessorFactory {

    AccessorFactory() { }

    public void implementAll(Map<TypeElement, Accessor> elements) {
        elements.forEach((elem, accessor) -> {
            if (TestUtils.isElemKind(elem, ElementKind.INTERFACE)) {
                this.implInterface(elem, accessor);
            } else if (TestUtils.isAbstractClass(elem)) {
                this.implAbstract(elem, accessor);
            } else if (isNeedImpl(elem)) {
                String superClass = StringUtils.getQualifiedName(elem);
                writeJavaFile(newWriter(elem, accessor).extend(superClass));
            }
        });
    }

    private void implAbstract(TypeElement element, Accessor accessor) {
        String superClass = StringUtils.getQualifiedName(element);
        AccessorWriter writer = newWriter(element, accessor).extend(superClass);
        implAbstractMethods(writer, element, accessor);
        writeJavaFile(writer);
    }

    private void implInterface(TypeElement element, Accessor accessor) {
        String interfaceName = StringUtils.getQualifiedName(element);
        AccessorWriter writer = newWriter(element, accessor).impl(interfaceName);
        implAbstractMethods(writer, element, accessor);
        writeJavaFile(writer);
    }

    private static void implAbstractMethods(AccessorWriter writer, TypeElement element, Accessor accessor) {
        List<ExecutableElement> elements = findAbstractMethods(element);
        toMethodDeclaration(writer, elements, accessor);
    }

    private static void toMethodDeclaration(AccessorWriter writer, List<ExecutableElement> methods, Accessor accessor) {
        for (ExecutableElement method : methods) {
            Provided provided = method.getAnnotation(Provided.class);
            if (provided == null) {
                writer.addImplMethod(new MethodModel(method));
            } else {
                Assert.assertProvidedMethod(method, provided);
                writer.addProvidedMethod(new ProvidedMethod(method, provided));
            }
        }
    }

    /**
     * 返回所有抽象方法
     *
     * @param element 接口或抽象类
     *
     * @return 所有抽象方法
     */
    private static List<ExecutableElement> findAbstractMethods(TypeElement element) {
        List<ExecutableElement> elements = new ArrayList<>();
        for (Element elem : element.getEnclosedElements()) {
            if (TestUtils.isMethod(elem) && TestUtils.isAny(elem, Modifier.ABSTRACT)) {
                elements.add((ExecutableElement) elem);
            }
        }
        return elements;
    }

    private static void writeJavaFile(AccessorWriter writer) {
        try {
            writer.write(EnvUtils.getFiler());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static AccessorWriter newWriter(TypeElement element, Accessor accessor) {
        AccessorWriter writer = new AccessorWriter();
        String packageName = StringUtils.getPackageName(element);
        String simpleName = StringUtils.getSimpleName(element);
        String cls = Replacer.INTERFACE.replace(accessor.classname(), simpleName);
        return writer.pkg(packageName).classname(cls).comment(toString(accessor));
    }

    private static boolean isNeedImpl(TypeElement elem) {
        if (TestUtils.isAny(elem, Modifier.FINAL)) {
            return false;
        }
        boolean needImpl = true;
        if (Imported.REPOSITORY) {
            needImpl = elem.getAnnotation(Repository.class) == null;
            if (needImpl) {
                needImpl = elem.getAnnotation(Service.class) == null;
            }
            if (needImpl) {
                needImpl = elem.getAnnotation(Component.class) == null;
            }
        }
        return needImpl;
    }

    private static String toString(Accessor accessor) {
        String str = accessor.toString();
        int idx = str.indexOf("(");
        int start = str.lastIndexOf('.', idx) + 1;
        return '@' + str.substring(start);
    }
}
