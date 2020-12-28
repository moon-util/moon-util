package com.moon.processor.manager;

import com.moon.processor.model.DeclareClass;
import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Generic2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DeclareManager {

    private final ClassnameManager classnameManager = new ClassnameManager();

    private final Map<String, DeclareClass> typeDeclareMap = new HashMap<>();

    public DeclareManager() { }

    public DeclareClass by(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        DeclareClass declared = typeDeclareMap.get(classname);
        if (declared == null) {
            declared = parseDeclareClass(element, classnameManager);
            typeDeclareMap.put(classname, declared);
        }
        return declared;
    }

    private static DeclareClass parseDeclareClass(TypeElement elem, ClassnameManager classnameManager) {
        Map<String, DeclareGeneric> genericMap = Generic2.from(elem);
        DeclareClass declared = new DeclareClass(elem, classnameManager);

        return declared;
    }
}
