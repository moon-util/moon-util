package com.moon.mapping.processing;

import java.util.Objects;

/**
 * @author benshaoye
 */
final class MapFieldFactory {

    private final static String CHAR = "char";
    private final static String INT = "int";

    public MapFieldFactory() {}

    public String doForwardField(Mappable thisProp, Mappable thatProp) {
        String t0 = null;

        if (isString(thatProp.getSetterDeclareType())) {
            if (isString(thisProp.getGetterDeclareType())) {
                t0 = "that.{setterName}(self.{getterName}());";
            } else if (isString(thisProp.getGetterFinalType())) {
                t0 = "that.{setterName}((String)self.{getterName}());";
            } else if (thisProp.isPrimitiveGetter()) {
                t0 = "that.{setterName}(String.valueOf(self.{getterName}()));";
            } else {
                // 打印警告: 已忽略不兼容类型字段，或请提供字段 {} 到 {} 的类型转换器
                t0 = "";
            }
        } else if (StringUtils.isPrimitiveNumber(thatProp.getSetterDeclareType())) {
            final String setterType = thatProp.getSetterDeclareType();
            final String getterType = thatProp.getGetterDeclareType();
            if (StringUtils.isPrimitiveNumber(getterType)) {
                if (isCompatible(setterType, getterType)) {
                    t0 = "that.{setterName}(self.{getterName}());";
                } else {
                    t0 = "that.{setterName}((setterType)self.{getterName}());";
                }
            } else if (CHAR.equals(getterType)) {
                if (isCompatible(setterType, INT)) {
                    t0 = "that.{setterName}(self.{getterName}());";
                } else {
                    t0 = "that.{setterName}((char)self.{getterName}());";
                }
            }else {
                // getterType == boolean: 不兼容，胡烈并打印警告
                t0 = "";
            }
        }

        if (thatProp.isPrimitiveSetter() && thisProp.isPrimitiveGetter()) {

            String setterType = thatProp.getSetterDeclareType();
            String getterType = thatProp.getGetterDeclareType();
            if (Objects.equals(setterType, getterType)) {
                t0 = "that.{setterName}(self.{getterName}());";
            }
        }
        return null;
    }

    private static boolean isCompatible(String thisType, String thatType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisType);
        int thatIdx = all.indexOf(thatType);
        return thisIdx >= thatIdx;
    }

    private static boolean isString(String value) {
        return String.class.getName().equals(value);
    }
}
