package com.moon.processor.model;

import com.moon.processor.manager.ClassnameManager;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;

/**
 * POJO 定义
 * <p>
 * 描述只有 field、setter、getter、hashCode、toString、equals 这些方法的类
 * 如：Entity、DTO、DO、BO、VO
 * <p>
 * key: property name
 * value: property declaration (field + getter + setter)
 *
 * @author benshaoye
 */
public class DeclareClass extends LinkedHashMap<String, DeclareProperty> implements JavaFileWriteable {

    /**
     * 声明类
     */
    private final TypeElement declareElement;
    /**
     * {@link TypeElement#getQualifiedName()}
     * {@link #declareElement}
     */
    private final String thisClassname;
    /**
     * 实现类
     */
    private final String implClassname;
    /**
     * 是否是抽象类
     */
    private final boolean abstracted;

    public DeclareClass(TypeElement declareElement, ClassnameManager registry) {
        this.declareElement = declareElement;
        this.thisClassname = Element2.getQualifiedName(declareElement);
        this.abstracted = Test2.isAbstractClass(declareElement);
        String name = this.getThisClassname();
        if (abstracted) {
            name = registry.getImplClassname(declareElement);
        }
        this.implClassname = name;
    }

    public TypeElement getDeclareElement() { return declareElement; }

    public String getThisClassname() { return thisClassname; }

    public String getImplClassname() { return implClassname; }

    public boolean isAbstract() { return abstracted; }


    @Override
    public void writeJavaFile(Filer filer) {
        if (isAbstract() && !written) {
            this.doWriteJavaFile(filer);
        }
    }
    private boolean written = false;

    private void doWriteJavaFile(Filer filer) {

        // TODO write implementation java file
        written = true;
    }
}
