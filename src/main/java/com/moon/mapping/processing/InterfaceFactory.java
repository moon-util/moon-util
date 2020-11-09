package com.moon.mapping.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author benshaoye
 */
class InterfaceFactory {

    @SuppressWarnings("all")
    public InterfaceFactory(TypeElement interfaceElem) {
        DetectUtils.assertInterface(interfaceElem);
        List<Element> filtered = new ArrayList<>();
        for (Element element : interfaceElem.getEnclosedElements()) {
            if (DetectUtils.isAny(element, DEFAULT, STATIC)) {
                continue;
            }
            if (!DetectUtils.isMethod(element)) {
                continue;
            }
            if (DetectUtils.isSetterMethod(element) || DetectUtils.isGetterMethod(element)) {
                filtered.add(element);
            }
        }
    }
}
