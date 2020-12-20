package com.moon.mapper.processing;

/**
 * @author benshaoye
 */
final class PropertyModel {

    private Mappable fromProp;
    private Mappable toProp;
    private String fromName;
    private String toName;
    private PropertyAttr attr;

    public PropertyModel() {}

    public PropertyModel onConvert(Mappable thisProp, Mappable thatProp, PropertyAttr attr, ConvertStrategy strategy) {
        return strategy == ConvertStrategy.FORWARD//
               ? forward(thisProp, thatProp, attr)//
               : backward(thisProp, thatProp, attr);
    }

    private PropertyModel forward(Mappable thisProp, Mappable thatProp, PropertyAttr attr) {
        this.fromProp = thisProp;
        this.toProp = thatProp;
        this.fromName = "self";
        this.toName = "that";
        this.attr = attr;
        return this;
    }

    private PropertyModel backward(Mappable thisProp, Mappable thatProp, PropertyAttr attr) {
        this.fromProp = thatProp;
        this.toProp = thisProp;
        this.fromName = "that";
        this.toName = "self";
        this.attr = attr;
        return this;
    }

    /**
     * 所有可用性都是基于这个方法的，所以只有这里有空判断，后面的其他方法都没有空判断
     *
     * @return
     */
    public boolean isUsable() {
        Mappable from = fromProp, to = toProp;
        boolean hasGetter = from != null && from.hasGetterMethod();
        boolean hasSetter = to != null && to.hasSetterMethod();
        return hasGetter && hasSetter && from.isGetterDeclared();
    }

    public Mappable getFromProp() { return fromProp; }

    public Mappable getToProp() { return toProp; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    public PropertyAttr getAttr() { return attr; }

    public String getFromClassname() {
        return getFromProp().getThisClassname();
    }

    public String getToClassname() {
        return getToProp().getThisClassname();
    }

    public boolean isGetterGeneric() {
        return getFromProp().isGetterGenerify();
    }

    public boolean isSetterGeneric() {
        return getToProp().isSetterGenerify();
    }

    public String getGetterDeclareType() {
        return getFromProp().getGetterDeclareType();
    }

    public String getSetterDeclareType() {
        return getToProp().getSetterDeclareType();
    }

    public String getGetterActualType() {
        return getFromProp().getGetterActualType();
    }

    public String getSetterActualType() {
        return getToProp().getSetterActualType();
    }

    public String getGetterFinalType() {
        return getFromProp().getGetterFinalType();
    }

    public String getSetterFinalType() {
        return getToProp().getSetterFinalType();
    }

    public String getGetterName() {
        return getFromProp().getGetterName();
    }

    public String getSetterName() {
        return getToProp().getSetterName();
    }
}
