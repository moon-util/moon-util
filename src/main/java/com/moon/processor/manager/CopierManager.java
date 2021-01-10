package com.moon.processor.manager;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.def.DefBeanCopier;
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
public class CopierManager implements JavaFileWriteable {

    private final NameManager nameManager;
    private final PojoManager pojoManager;
    private final Map<String, DefBeanCopier> definedCopierMap = new LinkedHashMap<>();

    public CopierManager(PojoManager pojoManager, NameManager nameManager) {
        this.nameManager = nameManager;
        this.pojoManager = pojoManager;
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
            DeclaredPojo thisClass = pojoManager.with(thisElem);
            DeclaredPojo thatClass = pojoManager.with(thatElem);
            copier = new DefBeanCopier(thisClass, thatClass, nameManager);
            definedCopierMap.put(copierKey, copier);
        }
        return copier;
    }

    public DefBeanCopier with(String thisClass, String thatClass) {
        Elements elements = Environment2.getUtils();
        return with(elements.getTypeElement(thisClass), elements.getTypeElement(thatClass));
    }

    @Override
    public void writeJavaFile(JavaWriter javaWriter) {
        definedCopierMap.forEach((key, copier) -> copier.writeJavaFile(javaWriter));
    }
}
