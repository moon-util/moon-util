package com.moon.processor.holder;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.parser.PojoParser;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class PojoHolder implements JavaFileWriteable {

    private final NameHolder nameHolder;
    private final SessionManager sessionManager;
    private final Map<String, DeclaredPojo> classMap = new HashMap<>();

    public PojoHolder(NameHolder nameHolder, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.nameHolder = nameHolder;
    }

    private static int computeFieldsCount(DeclaredPojo pojo) {
        int count = 0;
        for (DeclareProperty value : pojo.values()) {
            VariableElement field = value.getField();
            if (field != null) {
                count++;
            }
        }
        return count;
    }

    public DeclaredPojo with(TypeElement typeElement) {
        return with(typeElement, true);
    }

    public DeclaredPojo with(TypeElement typeElement, boolean canGenerate) {
        String classname = Element2.getQualifiedName(typeElement);
        DeclaredPojo declared = classMap.get(classname);
        if (declared == null) {
            synchronized (classMap) {
                declared = classMap.get(classname);
                if (declared == null) {
                    declared = PojoParser.parse(typeElement, nameHolder, canGenerate);
                    sessionManager.setMaxLevelIfLessThan(computeFieldsCount(declared));
                    classMap.put(classname, declared);
                }
            }
        } else if (canGenerate) {
            declared.withCanGenerate();
        }
        return declared;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        classMap.forEach((classname, declared) -> {
            DeclJavaFile filer = declared.getDeclJavaFile();
            if (declared.isAbstract() && filer != null) {
                filer.writeJavaFile(writer);
            }
        });
    }
}
