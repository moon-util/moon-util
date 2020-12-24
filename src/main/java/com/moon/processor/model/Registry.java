package com.moon.processor.model;

import com.moon.processor.utils.Environment2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class Registry {

    private final Set<String> registeredClasses = new HashSet<>();

    public Registry() {}

    public boolean containsClass(String key) {
        return registeredClasses.contains(key);
    }

    public String nextOrderedImplClass(String interfaceClass) {
        return nextOrderedClass(interfaceClass + "Implementation");
    }

    public String nextOrderedClass(String classname) {
        if (containsClass(classname)) {
            String name;
            Elements utils = Environment2.getUtils();
            for (int i = 1; ; i++) {
                name = classname + i;
                TypeElement elem = utils.getTypeElement(name);
                registeredClasses.add(name);
                if (elem == null) {
                    return name;
                }
            }
        }
        return classname;
    }
}
