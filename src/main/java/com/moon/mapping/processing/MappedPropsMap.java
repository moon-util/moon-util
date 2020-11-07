package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;

/**
 * @author benshaoye
 */
public class MappedPropsMap extends LinkedHashMap<String, PropertyModel> {

    private final String thisClassname;
    private final String declareClassname;
    private final TypeElement thisElement;
    private final TypeElement declareElement;
    private ExecutableElement constructor;

    public MappedPropsMap(TypeElement thisElement, TypeElement declareElement) {
        this.declareClassname = ElementUtils.getQualifiedName(declareElement);
        this.thisClassname = ElementUtils.getQualifiedName(thisElement);
        this.declareElement = declareElement;
        this.thisElement = thisElement;
    }

    final MappedPropsMap reversed(){
        MappedPropsMap propsMap = CollectUtils.reduceReversed(entrySet(), (resultMap, entry) -> {
            resultMap.put(entry.getKey(), entry.getValue());
            return resultMap;
        }, new MappedPropsMap(getThisElement(), getDeclareElement()));
        propsMap.setConstructor(getConstructor());
        return propsMap;
    }

    public String getThisClassname() { return thisClassname; }

    public String getDeclareClassname() { return declareClassname; }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getDeclareElement() { return declareElement; }

    public ExecutableElement getConstructor() { return constructor; }

    public void setConstructor(ExecutableElement constructor) {
        this.constructor = constructor;
    }
}
