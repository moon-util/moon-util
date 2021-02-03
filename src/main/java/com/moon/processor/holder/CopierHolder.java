package com.moon.processor.holder;

import com.moon.mapper.BeanCopier;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefBeanCopier;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class CopierHolder implements JavaFileWriteable {

    private final NameHolder nameHolder;
    private final PojoHolder pojoHolder;
    private final Map<String, DefBeanCopier> definedCopierMap = new LinkedHashMap<>();

    public CopierHolder(PojoHolder pojoHolder, NameHolder nameHolder) {
        this.nameHolder = nameHolder;
        this.pojoHolder = pojoHolder;
    }

    public DefBeanCopier with(Class<?> thisClass, Class<?> thatClass) {
        return with(thisClass.getCanonicalName(), thatClass.getCanonicalName());
    }

    public DefBeanCopier with(TypeElement thisElem, TypeElement thatElem) {
        String thisClassname = Element2.getQualifiedName(thisElem);
        String thatClassname = Element2.getQualifiedName(thatElem);
        String copierKey = String2.keyOf(thisClassname, thatClassname);
        DefBeanCopier copier = definedCopierMap.get(copierKey);
        if (copier == null) {
            DeclaredPojo thisClass = pojoHolder.with(thisElem);
            DeclaredPojo thatClass = pojoHolder.with(thatElem);
            copier = new DefBeanCopier(thisClass, thatClass, nameHolder);
            definedCopierMap.put(copierKey, copier);
        }
        return copier;
    }

    public DefBeanCopier with(String thisClass, String thatClass) {
        Elements elements = Environment2.getUtils();
        return with(elements.getTypeElement(thisClass), elements.getTypeElement(thatClass));
    }

    private final static String PKG = BeanCopier.class.getPackage().getName();

    @Override
    public void writeJavaFile(JavaWriter javaWriter) {
        definedCopierMap.forEach((key, copier) -> copier.writeJavaFile(javaWriter));
        // DeclJavaFile copier2 = DeclJavaFile.enumOf(PKG, "Copier2");
        // TypeElement elem = Environment2.getUtils().getTypeElement(copier2.getCanonicalName());
        // if (elem != null) {
        //     return;
        // }
        // Object[] values = {Map.class, String.class, BeanCopier.class};
        // DeclField field = copier2.privateConstField("COPIERS", "{}<{}, {}<?, ?>>", values);
        // field.valueOf("new {}<>()", copier2.onImported(HashMap.class));
        // definedCopierMap.forEach((name, copier) -> {
        //     Object[] objects = {name, copier2.onImported(copier.getClassname()), Const2.INSTANCE};
        //     copier2.addStaticBlock("COPIERS.put(\"{}\", {}.{})", objects);
        // });
        // DeclParams params = DeclParams.of("thisClass", Class.class).add("thatClass", Class.class);
        // copier2.publicMethod("get", params).withStatic().returnTypeof("{}", BeanCopier.class).returning("null");
        // copier2.writeJavaFile(javaWriter);
    }
}