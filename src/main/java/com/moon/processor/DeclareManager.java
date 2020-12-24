package com.moon.processor;

import com.moon.processor.model.DeclareClass;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DeclareManager {

    private final Map<String, DeclareClass> typeDeclareMap = new HashMap<>();

    public DeclareManager() {
    }

    public DeclareClass by(TypeElement element) {
        String classname = String2.getQualifiedName(element);
        DeclareClass declared = typeDeclareMap.get(classname);
        if (declared == null) {

        }
        return declared;
    }
}
