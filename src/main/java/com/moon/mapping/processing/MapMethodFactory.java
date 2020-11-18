package com.moon.mapping.processing;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.moon.mapping.processing.MapScripts.*;

/**
 * @author moonsky
 */
final class MapMethodFactory {

    private final AtomicInteger indexer = new AtomicInteger();

    MapMethodFactory() { }

    public AtomicInteger getIndexer() { return indexer; }

    final String newThatOnEmptyConstructor(String thisType, String thatType, boolean emptyFields) {
        String template = emptyFields ? newThatAsEmpty4NonFields : newThatAsEmpty;
        return replaceTypeAndName(template, thisType, thatType);
    }

    final String newThisOnEmptyConstructor(String thisType, String thatType, boolean emptyFields) {
        String template = emptyFields ? newThisAsEmpty4NonFields : newThisAsEmpty;
        return replaceTypeAndName(template, thisType, thatType);
    }

    /*
     start ObjectMapping
     */

    @SuppressWarnings("all")
    final String fromMapField(Mappable prop, Manager manager) {
        if (DetectUtils.isAnyNull(prop.getSetterName(), prop.getSetterFinalType())) {
            return "";
        }
        String t0 = "self.{setterName}(({setterType}) thatObject.get(\"{name}\"));";
        if (prop.isPrimitiveSetter()) {
            t0 = "{setterType} {var} = ({setterType}) thatObject.get(\"{name}\");" +//
                "if ({var} != null) { self.{setterName}({var}); }";
            t0 = Replacer.var.replace(t0, nextVarname());
            t0 = Replacer.setterType.replace(t0, manager.onImported(prop.getWrappedSetterType()));
        } else if (Objects.equals(prop.getSetterFinalType(), "java.lang.String")) {
            t0 = "Object {var} = thatObject.get(\"{name}\");" +//
                "if ({var} == null) { self.{setterName}(null); }" +//
                "else { self.{setterName}({var}.toString()); }";
            t0 = Replacer.var.replace(t0, nextVarname());
        }
        t0 = Replacer.setterType.replace(t0, manager.onImported(prop.getSetterFinalType()));
        t0 = Replacer.setterName.replace(t0, prop.getSetterName());
        return Replacer.name.replace(t0, prop.getName());
    }

    final String fromMapMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(MapScripts.fromMap, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String toMapField(Mappable prop) {
        if (DetectUtils.isAnyNull(prop.getGetterName(), prop.getGetterFinalType())) {
            return "";
        }
        return toMapField(prop.getName(), prop.getGetterName());
    }

    private String toMapField(String name, String getterName) {
        String t0 = "thatObject.put(\"{name}\", self.{getterName}());";
        String result = Replacer.name.replace(t0, name);
        return Replacer.getterName.replace(result, getterName);
    }

    final String toMapMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(MapScripts.toMap, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String newThisAsMapMethod(String thisType) {
        return Replacer.thisType.replace(MapScripts.newThisAsMap, thisType);
    }

    final String toStringField(Mappable model, boolean first) {
        return StringUtils.isBlank(model.getGetterName()) ? "" : toStringField(model.getName(),
            model.getGetterName(),
            first);
    }

    private String toStringField(String name, String getterName, boolean first) {
        String t0 = "builder.append(\"{name}=\").append(self.{getterName}());";
        String t1 = "builder.append(\", {name}=\").append(self.{getterName}());";
        String template = Replacer.name.replace(first ? t0 : t1, name);
        return Replacer.getterName.replace(template, getterName);
    }

    final String toStringMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(MapScripts.toString, thisType);
        result = Replacer.thisName.replace(result, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String unsafeForward(String thisType, String thatType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(MapScripts.unsafeForward, thisType);
        result = Replacer.thatType.replace(result, thatType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String unsafeBackward(String thisType, String thatType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(MapScripts.unsafeBackward, thisType);
        result = Replacer.thatType.replace(result, thatType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String cloneField(Mappable property) {
        if (property.hasGetterMethod() && property.hasSetterMethod()) {
            String result = "self.{setterName}(that.{getterName}());";
            result = Replacer.setterName.replace(result, property.getSetterName());
            return Replacer.getterName.replace(result, property.getGetterName());
        }
        return "";
    }

    final String cloneMethod(String thisType, String implType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(MapScripts.clone, thisType);
        result = Replacer.implType.replace(result, implType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    private static String replaceTypeAndName(String template, String thisType, String thatType) {
        String result = Replacer.thisType.replace(template, thisType);
        result = Replacer.thatType.replace(result, thatType);
        result = Replacer.thisName.replace(result, thisType);
        result = Replacer.thatName.replace(result, thatType);
        return result;
    }

    private String nextVarname() {
        return nextVarname(getIndexer());
    }

    private static String nextVarname(AtomicInteger indexer) {
        return "var" + indexer.getAndIncrement();
    }
}
