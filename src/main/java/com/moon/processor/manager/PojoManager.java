package com.moon.processor.manager;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.model.DefJavaFiler;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.ProcessClass2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class PojoManager implements JavaFileWriteable {

    private final NameManager nameManager;
    private final Map<String, DeclaredPojo> classMap = new HashMap<>();

    public PojoManager(NameManager nameManager) {
        this.nameManager = nameManager;
    }

    public DeclaredPojo with(TypeElement typeElement) {
        String classname = Element2.getQualifiedName(typeElement);
        DeclaredPojo declared = classMap.get(classname);
        if (declared == null) {
            synchronized (classMap) {
                declared = classMap.get(classname);
                if (declared == null) {
                    declared = ProcessClass2.toPropertiesMap(typeElement, nameManager);
                    classMap.put(classname, declared);
                }
            }
        }
        return declared;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        classMap.forEach((classname, declared) -> {
            DefJavaFiler filer = declared.getDefJavaFiler();
            if (declared.isAbstracted() && filer != null) {
                filer.writeJavaFile(writer);
            }
        });
    }
}
