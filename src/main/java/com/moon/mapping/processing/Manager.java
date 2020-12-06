package com.moon.mapping.processing;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.YearMonth;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
final class Manager {

    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final Map<String, String> defaultFormats = new HashMap<>();

    {
        defaultFormats.put(java.time.LocalDate.class.getName(), "yyyy-MM-dd");
        defaultFormats.put(java.time.LocalTime.class.getName(), "HH:mm:ss");
        defaultFormats.put(java.time.YearMonth.class.getName(), "yyyy-MM");
        defaultFormats.put(java.time.Year.class.getName(), "yyyy");
        defaultFormats.put(java.time.MonthDay.class.getName(), "MM-dd");
        if (Imported.JODA_TIME) {
            defaultFormats.put(MonthDay.class.getName(), "MM-dd");
            defaultFormats.put(YearMonth.class.getName(), "yyyy-MM");
            defaultFormats.put(LocalTime.class.getName(), "HH:mm:ss");
            defaultFormats.put(LocalDate.class.getName(), "yyyy-MM-dd");
        }
    }

    private final ImportManager importManager;
    private final StaticManager staticManager;
    private final MappingManager mappingManager;
    private final CallerManager callerManager;

    public Manager() {
        final PropertyModel model = new PropertyModel();
        final ImportManager importManager = new ImportManager();
        final StaticManager staticManager = new StaticManager(importManager);
        final MappingManager mappingManager = new MappingManager(model, importManager);
        this.callerManager = new CallerManager(importManager);
        this.mappingManager = mappingManager;
        this.importManager = importManager;
        this.staticManager = staticManager;
    }

    public String findProviderMethod() {
        PropertyModel model = getModel();
        Mappable to = model.getToProp();
        Mappable from = model.getFromProp();
        String setterType = ElemUtils.toSimpleGenericTypename(getMapping().getSetterType());
        return doFindMethod(to.getThisClass(), from, setterType, to.getName());
    }

    public String findConverterMethod() {
        PropertyModel model = getModel();
        Mappable to = model.getToProp();
        Mappable from = model.getFromProp();
        String getterType = ElemUtils.toSimpleGenericTypename(getMapping().getGetterType());
        return doFindMethod(from.getThisClass(), to, getterType, to.getName());
    }

    private final String voidClassname = void.class.getCanonicalName();

    private String doFindMethod(TypeElement element, Mappable mappable, String targetType, String propName) {
        Types types = EnvUtils.getTypes();
        do {
            String classname = ElemUtils.getQualifiedName(element);
            if (DetectUtils.isTypeof(classname, Object.class)) {
                return null;
            }
            String convertMethodName = getMethodName(mappable, classname, targetType, propName);
            if (convertMethodName != null) {
                return convertMethodName;
            }
            List<? extends TypeMirror> interfaces = element.getInterfaces();
            if (interfaces != null) {
                for (TypeMirror anInterface : interfaces) {
                    TypeElement interElem = (TypeElement) types.asElement(anInterface);
                    String interClass = ElemUtils.getQualifiedName(interElem);
                    convertMethodName = getMethodName(mappable, interClass, targetType, propName);
                    if (convertMethodName != null) {
                        return convertMethodName;
                    }
                }
            }
            element = (TypeElement) types.asElement(element.getSuperclass());
        } while (true);
    }

    private String getMethodName(Mappable mappable, String classname, String getterType, String propName) {
        String key = ElemUtils.toConvertKey(classname, getterType, propName);
        String convertMethodName = mappable.findConverterMethod(key);
        if (convertMethodName == null) {
            key = ElemUtils.toConvertKey(voidClassname, getterType, propName);
            return mappable.findConverterMethod(key);
        }
        return convertMethodName;
    }

    public boolean isModelUsable(Mappable thisProp, Mappable thatProp, PropertyAttr attr, ConvertStrategy strategy) {
        return getModel().onConvert(thisProp, thatProp, attr, strategy).isUsable();
    }

    public PropertyModel getModel() { return getMapping().getModel(); }

    public CallerManager getCaller() { return callerManager; }

    public MappingManager getMapping() { return mappingManager; }

    private StaticManager ofStatic() { return staticManager; }

    private ImportManager ofImport() { return importManager; }

    public String onImported(String classname) { return ofImport().onImported(classname); }

    public String onImported(Class<?> classname) { return ofImport().onImported(classname); }

    public String toStringForImports() { return ofImport().toString(); }

    public String toStringForStaticVars() { return ofStatic().toString(); }

    public String getFormatPatternVal() {
        return getModel().getAttr().formatValue();
    }

    public String getFormatPatternVal(String type, boolean isDefaultDate) {
        String pattern = getModel().getAttr().formatValue();
        return pattern == null//
            ? (isDefaultDate//
            ? defaultFormats.getOrDefault(type, DATE_FORMAT)//
            : defaultFormats.get(type))//
            : pattern;
    }

    /*
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ~~~ 静态变量管理
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public String staticForEnumValues() {
        return staticForEnumValues(setterType());
    }

    public String staticForEnumValues(String enumClassname) {
        return ofStatic().onEnumValues(enumClassname);
    }

    public String staticForJodaDateTimeFormatter(String formatVal) {
        return ofStatic().onJodaDateTimeFormat(formatVal);
    }

    public String staticForDateTimeFormatter(String formatVal) {
        return ofStatic().onDateTimeFormatter(formatVal);
    }

    public String staticForDefaultBigInteger() {
        String defaultValue = defaultVal();
        if (defaultValue != null) {
            return ofStatic().onDefaultBigInteger(defaultValue);
        }
        return ofStatic().defaultNull();
    }

    public String staticForDefaultBigDecimal() {
        String defaultValue = defaultVal();
        if (defaultValue != null) {
            return ofStatic().onDefaultBigDecimal(defaultValue);
        }
        return ofStatic().defaultNull();
    }

    public String staticForDefaultChar() {
        return ofStatic().onDefaultChar(defaultVal());
    }

    public String staticForDefaultBoolean() {
        return staticForDefaultBoolean(setterType());
    }

    public String staticForDefaultBoolean(String classname) {
        return ofStatic().onDefaultBoolean(classname, defaultVal());
    }

    public String staticForDefaultNumber() {
        return staticForDefaultNumber(setterType());
    }

    public String staticForDefaultNumber(String expectedClass) {
        String defaultValue = defaultVal();
        if (defaultValue != null) {
            return ofStatic().onDefaultNumber(expectedClass, defaultValue);
        }
        return ofStatic().defaultNull();
    }

    public String staticForDefaultNumberValueOf(String classname, String value) {
        return ofStatic().onDefaultNumber(classname, value);
    }

    public String staticForDefaultString() {
        return ofStatic().onString(defaultVal());
    }

    public String staticForDefaultEnumValue() {
        return doStaticVarForDefaultEnum(setterType());
    }

    private String doStaticVarForDefaultEnum(String enumClassname) {
        String dftValue = defaultVal();
        if (DetectUtils.isDigit(dftValue)) {
            return ofStatic().onEnumIndexed(enumClassname, dftValue);
        } else if (DetectUtils.isVar(dftValue)) {
            return ofStatic().onEnumNamed(enumClassname, dftValue);
        } else {
            return ofStatic().defaultNull();
        }
    }

    private String setterType() { return getMapping().getSetterType(); }

    private String defaultVal() { return getModel().getAttr().defaultValue(); }
}
