package com.moon.processing.decl;

import com.moon.accessor.annotation.Accessor;
import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaProvider;
import com.moon.processing.file.JavaClassFile;
import com.moon.processing.holder.NameHolder;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;

/**
 * 数据库访问对象
 *
 * @author benshaoye
 */
public class AccessorDeclared implements JavaProvider {

    private final NameHolder nameHolder;
    private final Accessor accessor;
    /** 数据库访问对象 */
    private final TypeDeclared typeDeclared;
    /** 实体 */
    private final TypeDeclared pojoDeclared;
    /** 数据表 */
    private final TableDeclared tableDeclared;

    private final String packageName, simpleClassName;

    public AccessorDeclared(
        Accessor accessor, NameHolder nameHolder, TypeDeclared typeDeclared, TableDeclared tableDeclared, TypeDeclared pojoDeclared
    ) {
        this.nameHolder = nameHolder;
        this.tableDeclared = tableDeclared;
        this.typeDeclared = typeDeclared;
        this.pojoDeclared = pojoDeclared;
        this.accessor = accessor;

        TypeElement accessorElem = typeDeclared.getTypeElement();
        String accessorImpl = nameHolder.getImplClassname(accessorElem);
        this.packageName = Element2.getPackageName(accessorElem);
        this.simpleClassName = Element2.getSimpleName(accessorImpl);
    }

    @Override
    public JavaDeclarable getJavaDeclare() {
        JavaClassFile classFile = new JavaClassFile(packageName, simpleClassName);

        return classFile;
    }
}
