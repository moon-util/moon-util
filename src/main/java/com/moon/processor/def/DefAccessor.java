package com.moon.processor.def;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.holder.CopierHolder;
import com.moon.processor.holder.ModelHolder;
import com.moon.processor.holder.NameHolder;
import com.moon.processor.holder.PojoHolder;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Imported;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.*;

/**
 * @author benshaoye
 */
public class DefAccessor implements JavaFileWriteable {


    private final CopierHolder copierHolder;
    private final ModelHolder modelHolder;
    private final PojoHolder pojoHolder;
    private final NameHolder nameHolder;

    private final TypeElement thisAccessor;
    private final String packageName;
    private final String simpleName;

    public DefAccessor(
        CopierHolder copierHolder,
        ModelHolder modelHolder,
        PojoHolder pojoHolder,
        NameHolder nameHolder,
        TypeElement thisAccessor
    ) {
        this.copierHolder = copierHolder;
        this.modelHolder = modelHolder;
        this.pojoHolder = pojoHolder;
        this.nameHolder = nameHolder;
        this.thisAccessor = thisAccessor;
        this.packageName = Element2.getPackageName(thisAccessor);
        String accessorImpl = nameHolder.getImplClassname(thisAccessor);
        this.simpleName = Element2.getSimpleName(accessorImpl);
    }

    private DeclJavaFile getDeclJavaFile() {
        TypeElement accessor = this.thisAccessor;
        ElementKind kind = accessor.getKind();
        if (kind == ElementKind.CLASS && Test2.isNotAny(accessor, Modifier.ABSTRACT)) {
            if (Imported.isComponent(accessor)) {
                return null;
            }
        }
        DeclJavaFile impl = DeclJavaFile.classOf(packageName, simpleName);

        if (accessor.getKind() == ElementKind.INTERFACE) {
            implInterface(impl, accessor);
        } else if (Test2.isAny(accessor, Modifier.ABSTRACT)) {
            implAbstract(impl, accessor);
        } else {
            implNormal(impl, accessor);
        }

        return impl;
    }

    private void implInterface(DeclJavaFile impl, TypeElement accessor) {
        impl.implement(Element2.getQualifiedName(accessor));

        for (Element enclosedElement : accessor.getEnclosedElements()) {
            if (!isImplCapable(enclosedElement)) {
                continue;
            }

        }
    }

    private void implNormal(DeclJavaFile impl, TypeElement accessor) {
    }

    private void implAbstract(DeclJavaFile impl, TypeElement accessor) {
    }

    private static boolean isImplCapable(Element enclosedElement) {
        // 不是构造器/方法, 跳过
        if (!(enclosedElement instanceof ExecutableElement)) {
            return false;
        }
        // 静态方法不处理、私有方法不处理
        if (Test2.isAny(enclosedElement, Modifier.STATIC)) {
            return false;
        }
        if (Test2.isAny(enclosedElement, Modifier.PRIVATE) && Test2.isAny(enclosedElement, Modifier.ABSTRACT)) {
            throw new IllegalStateException("不能实现私有抽象方法: " + enclosedElement);
        }
        return Test2.isAny(enclosedElement, Modifier.ABSTRACT);
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        DeclJavaFile file = getDeclJavaFile();
        if (file != null) {
            file.writeJavaFile(writer);
        }
    }
}
