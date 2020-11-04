package com.moon.mapping.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public class Implementation {

    private final ProcessingEnvironment env;
    private BasicStrategy strategy = new BasicStrategy();

    Implementation(ProcessingEnvironment env) { this.env = env; }

    private final static String newThatAsEmpty = "" +//
        "@Override " +//
        "public Object newThat(Object thisObject) {" +//
        "    if (thisObject == null) { return null; }" +//
        "    {thatType} that = new {thatType}();" +//
        "    safeToThat(({thisType}) thisObject, that);" +//
        "    return that;" +//
        "}";

    final static String newThatOnEmptyConstructor(String thisType, String thatType) {
        return repaceTypeAndName(newThatAsEmpty, thisType, thatType);
    }

    final static String newThisAsEmpty = "" +//
        "@Override " +//
        "public Object newThis(Object thatObject) {" +//
        "    if (thatObject == null) { return null; }" +//
        "    {thisType} self = new {thisType}();" +//
        "    safeFromThat(self, ({thatType}) thatObject);" +//
        "    return self;" +//
        "}";

    final static String newThisOnEmptyConstructor(String thisType, String thatType) {
        return repaceTypeAndName(newThisAsEmpty, thisType, thatType);
    }

    final static String fromThat = "" +//
        "@Override " +//
        "public Object fromThat(Object thisObject, Object thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { rethrn thisObject; }" +//
        "    safeFromThat(({thisType}) thisObject, ({thatType}) thatObject);" +//
        "    return thisObject;" +//
        "}";

    final String fromThatField(){
        throw new UnsupportedOperationException();
    }

    final String fromThatMethod(String thisType, String thatType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(fromThat, thisType);
        result = Replacer.thatType.replace(fromMap, thatType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final static String toThat = "" +//
        "@Override " +//
        "public Object toThat(Object thisObject, Object thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { rethrn thatObject; }" +//
        "    safeToThat(({thisType}) thisObject, ({thatType}) thatObject);" +//
        "    return thatObject;" +//
        "}";

    final String toThatField(String setterName, String getterName, String setterType, String getterType) {
        String result = "that.{setterName}(self.{getterName}());";
        if (Objects.equals(setterType, "java.lang.String")) {
            result = strategy.ifThatSetString();
        }
        result = Replacer.setterName.replace(result, setterName);
        result = Replacer.getterName.replace(result, getterName);
        return result;
    }

    final String toThatMethod(String thisType, String thatType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(toThat, thisType);
        result = Replacer.thatType.replace(fromMap, thatType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    private final static String fromMap = "" +//
        "@Override " +//
        "public Object fromMap(Object thisObject, java.util.Map thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { rethrn thisObject; }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    java.util.Map that = thatObject;" +//
        "    {MAPPINGS}" +//
        "    return thisObject;" +//
        "}";

    final String fromMapField(String name, String setterName, String setterType) {
        // TODO 还需要考虑设置基本数据类型的情况
        String t0 = "self.{setterName}(({setterType}) that.get(\"{name}\"));";
        String result = Replacer.setterName.replace(t0, setterName);
        result = Replacer.setterType.replace(t0, setterType);
        return Replacer.name.replace(t0, name);
    }

    final String fromMapMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(fromMap, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    private final static String toMap = "" +//
        "@Override " +//
        "public java.lang.Map toMap(Object thisObject, java.util.Map thatObject) {" +//
        "    if (thisObject == null || thatObject == null) { rethrn thatObject; }" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    java.util.Map that = thatObject;" +//
        "    {MAPPINGS}" +//
        "    return thatObject;" +//
        "}";

    final String toMapField(String name) {
        String t0 = "that.put(\"{name}\", self.{getterName}());";
        return Replacer.name.replace(t0, name);
    }

    final String toMapMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(toMap, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    private final static String toString = "" +//
        "@Override " +//
        "public String toString(Object thisObject) {" +//
        "    if (thisObject == null) { return \"null\"; }" +//
        "    if (!(thisObject instanceof {thisType})) { return thisObject.toString(); }" +//
        "    StringBuilder builder = new StringBuilder().append(\"{thisName}{\");" +//
        "    {thisType} self = ({thisType}) thisObject;" +//
        "    {MAPPINGS}" +//
        "    return builder.append('}').toString();" +//
        "}";

    private Elements elements() { return env.getElementUtils(); }

    final String toStringField(PropertyModel model, boolean first) {
        return toStringField(model.getName(), model.getGetterName(), first);
    }

    final String toStringField(String name, String getterName, boolean first) {
        String t0 = "builder.append(\"{name}='\").append(self.{getterName}()).append(\"'\");";
        String t1 = "builder.append(\", {name}='\").append(self.{getterName}()).append(\"'\");";
        String template = Replacer.name.replace(first ? t0 : t1, name);
        return Replacer.getterName.replace(template, getterName);
    }

    final String toStringMethod(String thisType, Iterable<String> fields) {
        String result = Replacer.thisType.replace(toString, thisType);
        result = Replacer.thisName.replace(result, thisType);
        return Replacer.MAPPINGS.replace(result, String.join("", fields));
    }

    final static String safeToThat = "" +//
        "private static void safeToThat({thisType} self, {thatType} that) { {MAPPINGS} }";

    final static String safeFromThat = "" +//
        "private static void safeFromThat({thisType} self, {thatType} that) { {MAPPINGS} }";

    private static String repaceTypeAndName(String template, String thisType, String thatType) {
        String result = Replacer.thisType.replace(template, thisType);
        result = Replacer.thatType.replace(result, thatType);
        result = Replacer.thisName.replace(result, thisType);
        result = Replacer.thatName.replace(result, thatType);
        return result;
    }

    private final static Replacer[] REPLACERS = Replacer.values();

    private enum Replacer {
        MAPPINGS("\\{MAPPINGS\\}"),
        var("\\{var\\}"),
        name("\\{name\\}"),
        getterName("\\{getterName\\}"),
        setterName("\\{setterName\\}"),
        getterType("\\{getterType\\}"),
        setterType("\\{setterType\\}"),
        thisType("\\{thisType\\}"),
        thatType("\\{thatType\\}"),
        thisName("\\{thisName\\}") {
            @Override
            String toReplacement(String value) { return toName(value); }
        },
        thatName("\\{thatName\\}") {
            @Override
            String toReplacement(String value) { return toName(value); }
        },
        ;

        private final String pattern;

        Replacer(String pattern) { this.pattern = pattern; }

        String toReplacement(String value) { return value; }

        public String replace(String template, String type) {
            return template.replaceAll(pattern, toReplacement(type));
        }

        private static String toName(String value) {
            int index = value.lastIndexOf('.');
            return index < 0 ? value : value.substring(index + 1);
        }
    }

    private final static class BasicStrategy {

        private final AtomicInteger indexer = new AtomicInteger();

        private final String ifThatSetString() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "Object {var}=self.{getterName}();" +//
                "if ({var} != null) { that.{setterName}({var}.toString()); }";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSetString() { return fromThat(ifThatSetString()); }

        private final String ifThatSet$ByteByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Byte.parseByte({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$ByteByCharSequence() {
            return fromThat(ifThatSet$ByteByCharSequence());
        }

        private final String ifThatSet$ShortByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Short.parseShort({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$ShortByCharSequence() {
            return fromThat(ifThatSet$ShortByCharSequence());
        }

        private final String ifThatSet$IntByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Integer.parseInt({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$IntByCharSequence() {
            return fromThat(ifThatSet$IntByCharSequence());
        }

        private final String ifThatSet$LongByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Long.parseLong({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$LongByCharSequence() {
            return fromThat(ifThatSet$LongByCharSequence());
        }

        private final String ifThatSet$FloatByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Float.parseFloat({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$FloatByCharSequence() {
            return fromThat(ifThatSet$FloatByCharSequence());
        }

        private final String ifThatSet$DoubleByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Double.parseDouble({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$DoubleByCharSequence() {
            return fromThat(ifThatSet$DoubleByCharSequence());
        }

        private final String ifThatSet$BooleanByCharSequence() {
            String var = "var" + (indexer.getAndIncrement());
            String t0 = "CharSequence {var}=self.{getterName}();" +//
                "if ({var} != null) { " +//
                "    that.{setterName}(Boolean.parseBoolean({var}.toString(), 10));" +//
                "}";
            return Replacer.var.replace(t0, var);
        }

        private final String ifThisSet$BooleanByCharSequence() {
            return fromThat(ifThatSet$BooleanByCharSequence());
        }

        private static String fromThat(String template) {
            return template.replaceAll("self", "PLACEHOLDER")
                .replaceAll("that", "self")
                .replaceAll("PLACEHOLDER", "that");
        }
    }
}
