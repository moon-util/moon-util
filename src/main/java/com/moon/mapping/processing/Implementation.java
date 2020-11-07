package com.moon.mapping.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public class Implementation {

    private final ProcessingEnvironment env;
    private final AtomicInteger indexer = new AtomicInteger();

    Implementation(ProcessingEnvironment env) { this.env = env; }

    public AtomicInteger getIndexer() { return indexer; }

    final String newThatOnEmptyConstructor(String thisType, String thatType) {
        return repaceTypeAndName(ImplConst.newThatAsEmpty, thisType, thatType);
    }

    final String newThisOnEmptyConstructor(String thisType, String thatType) {
        return repaceTypeAndName(ImplConst.newThisAsEmpty, thisType, thatType);
    }

    final String fromThatField(
        PropertyModel fromProperty, PropertyModel toProperty
    ) {
        return fromThatField(toProperty.getSetterName(),//
            fromProperty.getGetterName(),//
            toProperty.getSetterActualType(),//
            fromProperty.getGetterActualType(),//
            toProperty.isPrimitiveSetterMethod());
    }

    private final String fromThatField(
        String setterName, String getterName, String setterType, String getterType, boolean primitive
    ) {
        String result = "self.{setterName}(({setterType}) that.{getterName}());";
        for (Defaults value : Defaults.values()) {
            if (value.isTypeMatches(getterType, setterType)) {
                result = value.getFromThatTemplate(getIndexer());
                if (primitive) {
                    result += Defaults.toThatNull;
                }
                break;
            }
        }
        result = Replacer.getterType.replace(result, getterType);
        result = Replacer.setterType.replace(result, setterType);
        result = Replacer.setterName.replace(result, setterName);
        return Replacer.getterName.replace(result, getterName);
    }

    final String fromThatMethod(String thisType, String thatType) {
        String result = Replacer.thisType.replace(ImplConst.fromThat, thisType);
        return Replacer.thatType.replace(result, thatType);
    }

    final String toThatField(
        PropertyModel fromProperty, PropertyModel toProperty
    ) {
        return toThatField(toProperty.getSetterName(),//
            fromProperty.getGetterName(),//
            toProperty.getSetterActualType(),//
            fromProperty.getGetterActualType(),//
            toProperty.isPrimitiveSetterMethod());
    }

    private final String toThatField(
        String setterName, String getterName, String setterType, String getterType, boolean primitive
    ) {
        String result = "that.{setterName}(({setterType}) self.{getterName}());";
        for (Defaults value : Defaults.values()) {
            if (value.isTypeMatches(getterType, setterType)) {
                result = value.getToThatTemplate(getIndexer());
                if (primitive) {
                    result += Defaults.toThatNull;
                }
                break;
            }
        }
        result = Replacer.getterType.replace(result, getterType);
        result = Replacer.setterType.replace(result, setterType);
        result = Replacer.setterName.replace(result, setterName);
        return Replacer.getterName.replace(result, getterName);
    }

    final String toThatMethod(String thisType, String thatType) {
        String result = Replacer.thisType.replace(ImplConst.toThat, thisType);
        return Replacer.thatType.replace(result, thatType);
    }

    final String fromMapField(PropertyModel property) {
        String t0 = "self.{setterName}(({setterType}) thatObject.get(\"{name}\"));";
        if (property.isPrimitiveSetterMethod()) {
            t0 = "{setterType} {var} = ({setterType}) thatObject.get(\"{name}\");" +//
                "if ({var} != null) { self.{setterName}({var}); }";
            t0 = Replacer.var.replace(t0, nextVarname());
            t0 = Replacer.setterType.replace(t0, property.getWrappedSetterType());
        } else if (Objects.equals(property.getSetterActualType(), String.class.getName())) {
            t0 = "Object {var} = thatObject.get(\"{name}\");" +//
                "if ({var} == null) { self.{setterName}(null); }" +//
                "else { self.{setterName}({var}.toString()); }";
            t0 = Replacer.var.replace(t0, nextVarname());
        }
        t0 = Replacer.setterType.replace(t0, property.getSetterActualType());
        t0 = Replacer.setterName.replace(t0, property.getSetterName());
        return Replacer.name.replace(t0, property.getName());
    }

    final String fromMapMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(ImplConst.fromMap, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String toMapField(PropertyModel property) {
        return toMapField(property.getName(), property.getGetterName());
    }

    private final String toMapField(String name, String getterName) {
        String t0 = "thatObject.put(\"{name}\", self.{getterName}());";
        String result = Replacer.name.replace(t0, name);
        return Replacer.getterName.replace(result, getterName);
    }

    final String toMapMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(ImplConst.toMap, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String newThisAsMapMethod(String thisType) {
        return Replacer.thisType.replace(ImplConst.newThisAsMap, thisType);
    }

    private Elements elements() { return env.getElementUtils(); }

    final String toStringField(PropertyModel model, boolean first) {
        return toStringField(model.getName(), model.getGetterName(), first);
    }

    private final String toStringField(String name, String getterName, boolean first) {
        String t0 = "builder.append(\"{name}='\").append(self.{getterName}()).append(\"'\");";
        String t1 = "builder.append(\", {name}='\").append(self.{getterName}()).append(\"'\");";
        String template = Replacer.name.replace(first ? t0 : t1, name);
        return Replacer.getterName.replace(template, getterName);
    }

    final String toStringMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(ImplConst.toString, thisType);
        result = Replacer.thisName.replace(result, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String safeToThatMethod(String thisType, String thatType, Iterable fields) {
        String result = Replacer.thisType.replace(ImplConst.safeToThat, thisType);
        result = Replacer.thatType.replace(result, thatType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String safeFromThatMethod(String thisType, String thatType, Iterable fields) {
        String result = Replacer.thisType.replace(ImplConst.safeFromThat, thisType);
        result = Replacer.thatType.replace(result, thatType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final String copyField(PropertyModel property) {
        if (property.hasPublicGetterMethod() && property.hasPublicSetterMethod()) {
            String result = "self.{setterName}(that.{getterName}());";
            result = Replacer.setterName.replace(result, property.getSetterName());
            return Replacer.getterName.replace(result, property.getGetterName());
        }
        return "";
    }

    final String copyMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(ImplConst.copy, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    private static String repaceTypeAndName(String template, String thisType, String thatType) {
        String result = Replacer.thisType.replace(template, thisType);
        result = Replacer.thatType.replace(result, thatType);
        result = Replacer.thisName.replace(result, thisType);
        result = Replacer.thatName.replace(result, thatType);
        return result;
    }

    private final static Replacer[] REPLACERS = Replacer.values();

    private final String nextVarname() {
        return nextVarname(getIndexer());
    }

    private final static String nextVarname(AtomicInteger indexer) {
        return "var" + indexer.getAndIncrement();
    }

    private enum Replacer {
        MAPPINGS,
        var,
        name,
        getterName,
        setterName,
        getterType,
        setterType,
        thisType,
        thatType,
        thisName {
            @Override
            String toReplacement(String value) { return toName(value); }
        },
        thatName {
            @Override
            String toReplacement(String value) { return toName(value); }
        },
        ;

        private final String pattern;

        Replacer() { this.pattern = "\\{" + name() + "\\}"; }

        String toReplacement(String value) { return value; }

        public String replace(String template, String type) {
            return template.replaceAll(pattern, toReplacement(type));
        }

        private static String toName(String value) {
            int index = value.lastIndexOf('.');
            return index < 0 ? value : value.substring(index + 1);
        }
    }

    private enum Defaults {
        character("Object {var}=self.{getterName}();" +//
            "if ({var} != null) { that.{setterName}((java.lang.Character){var});}",//
            "char", "Character", "java.lang.Character") {},
        string("Object {var}=self.{getterName}();" +//
            "if ({var} != null) { that.{setterName}({var}.toString()); }",//
            "String", "java.lang.String") {
            @Override
            boolean isGetterTypeMatches(String getterType) { return true; }
        },
        byteByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Byte.parseByte({var}.toString(), 10));" +//
            "}", "byte", "Byte", "java.lang.Byte"),
        shortByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Short.parseShort({var}.toString(), 10));" +//
            "}", "short", "Short", "java.lang.Short"),
        intByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Integer.parseInt({var}.toString(), 10));" +//
            "}", "int", "Integer", "java.lang.Integer"),
        longByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Long.parseLong({var}.toString(), 10));" +//
            "}", "long", "Long", "java.lang.Long"),
        floatByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Float.parseFloat({var}.toString(), 10));" +//
            "}", "float", "Float", "java.lang.Float"),
        doubleByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Double.parseDouble({var}.toString(), 10));" +//
            "}", "double", "Double", "java.lang.Double"),
        booleanByString("CharSequence {var}=self.{getterName}();" +//
            "if ({var} != null) { " +//
            "    that.{setterName}(Boolean.parseBoolean({var}.toString(), 10));" +//
            "}", "boolean", "Boolean", "java.lang.Boolean");

        private final static String fromThatNull = " else { self.{setterName}(null); }";
        private final static String toThatNull = " else { that.{setterName}(null); }";
        private final String[] setterTypes;
        private final String fromThat;
        private final String toThat;

        Defaults(String template, String... setterTypes) {
            this.setterTypes = setterTypes;
            this.toThat = template;
            Object value = template;
            this.fromThat = template//
                .replaceAll("self", "PLACEHOLDER")//
                .replaceAll("that", "self")//
                .replaceAll("PLACEHOLDER", "that");
        }

        String getToThatTemplate(AtomicInteger indexer) {
            return Replacer.var.replace(toThat, nextVarname(indexer));
        }

        String getFromThatTemplate(AtomicInteger indexer) {
            return Replacer.var.replace(fromThat, nextVarname(indexer));
        }

        boolean isTypeMatches(String getterType, String setterType) {
            return isGetterTypeMatches(getterType) && isSetterTypeMatches(setterType);
        }

        boolean isGetterTypeMatches(String getterType) {
            return strings().contains(getterType);
        }

        boolean isSetterTypeMatches(String setterType) {
            for (String type : setterTypes) {
                if (Objects.equals(type, setterType)) {
                    return true;
                }
            }
            return false;
        }

        static Set<String> strings() {
            Set<String> types = new HashSet<>();
            types.add("java.lang.StringBuilder");
            types.add("java.lang.StringBuffer");
            types.add("java.lang.String");
            types.add("StringBuilder");
            types.add("StringBuffer");
            types.add("String");
            return types;
        }
    }
}
