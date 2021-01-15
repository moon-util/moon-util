package com.moon.processor.model;

import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMarked;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.holder.NameHolder;
import com.moon.processor.mapping.MappingMerged;
import com.moon.processor.utils.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static javax.lang.model.element.ElementKind.INTERFACE;

/**
 * POJO 定义
 * <p>
 * 描述只有 field、setter、getter、hashCode、toString、equals 这些方法的类
 * 如：Entity、DTO、DO、BO、VO
 * <p>
 * key: property name
 * value: property declaration (field + getter + setter)
 *
 * @author benshaoye
 */
public class DeclaredPojo extends LinkedHashMap<String, DeclareProperty> implements Completable {

    /**
     * 声明类
     */
    private final TypeElement declareElement;
    /**
     * {@link TypeElement#getQualifiedName()}
     * {@link #declareElement}
     */
    private final String thisClassname;
    /**
     * 实现类
     */
    private final String implClassname;
    /**
     * 是否是抽象类
     */
    private final boolean abstracted;
    private final boolean interfaced;

    private Map<String, MappingMerged> pushMappings;

    public DeclaredPojo(TypeElement declareElement, NameHolder registry) {
        this.declareElement = declareElement;
        // 这里的 thisClassname 没考虑泛型
        this.thisClassname = Element2.getQualifiedName(declareElement);
        this.interfaced = Test2.isElemKind(declareElement, INTERFACE);
        this.abstracted = Test2.isAny(declareElement, Modifier.ABSTRACT);
        String name = this.getThisClassname();
        if (abstracted) {
            name = registry.getImplClassname(declareElement);
        }
        this.implClassname = name;
    }

    public Map<String, MappingMerged> getPushMappings() {
        return pushMappings == null ? Collections.emptyMap() : pushMappings;
    }

    public MappingMerged findPushMapping(String field) { return findPushMappingAssigned(Const2.PUBLIC, field); }

    public MappingMerged findPushMappingAssigned(String targetCls, String field) {
        Map<String, MappingMerged> mappings = getPushMappings();
        MappingMerged mapping = mappings.get(String2.keyOf(targetCls, field, Const2.GETTER));
        if (mapping == null) {
            String key = String2.keyOf(targetCls, field, Const2.FIELD);
            return mappings.get(key);
        }
        return mapping;
    }

    public TypeElement getDeclareElement() { return declareElement; }

    public String getThisClassname() { return thisClassname; }

    public String getImplClassname() { return implClassname; }

    public boolean isAbstract() { return isAbstracted() || isInterfaced(); }

    public boolean isAbstracted() { return abstracted; }

    public boolean isInterfaced() { return interfaced; }

    @Override
    public void onCompleted() {
        values().forEach(Completable::onCompleted);
        this.pushMappings = pushMappingAssigned(this);
    }

    private static Map<String, MappingMerged> pushMappingAssigned(DeclaredPojo thisPojo) {
        Map<String, MappingMerged> mappingDeclared = new HashMap<>(4);
        thisPojo.forEach((name, propDecl) -> {
            propDecl.getGetterMappings().forEach((targetCls, mapping) -> {
                String pushFor = mapping.getField(name);
                String key = String2.keyOf(targetCls, pushFor, Const2.GETTER);
                mappingDeclared.put(key, new MappingMerged(mapping, propDecl));
            });
            propDecl.getFieldMappings().forEach((targetCls, mapping) -> {
                String pushFor = mapping.getField(name);
                String key = String2.keyOf(targetCls, pushFor, Const2.FIELD);
                mappingDeclared.putIfAbsent(key, new MappingMerged(mapping, propDecl));
            });
        });
        return Collections.unmodifiableMap(mappingDeclared);
    }

    public DeclJavaFile getDeclJavaFile() {
        if (isAbstracted()) {
            String implName = getImplClassname();
            String simpleName, pkg;
            int lastIdx = implName.lastIndexOf('.');
            if (lastIdx >= 0) {
                simpleName = implClassname.substring(lastIdx + 1);
                pkg = implName.substring(0, lastIdx);
            } else {
                simpleName = implName;
                pkg = "";
            }
            DeclJavaFile filer = DeclJavaFile.classOf(pkg, simpleName);
            if (isInterfaced()) {
                implementForInterface(filer.implement(getThisClassname()));
            } else if (isAbstracted()) {
                extendForAbstractCls(filer.extend(getThisClassname()));
            }
            return filer;
        }
        return null;
    }

    private final static Consumer<DeclMethod> NOTHING = m -> {};
    private final static Consumer<DeclMethod> OVERRIDE = DeclMethod::override;

    private void extendForAbstractCls(DeclJavaFile filer) {
        for (Map.Entry<String, DeclareProperty> propertyEntry : entrySet()) {
            DeclareProperty prop = propertyEntry.getValue();
            String name = propertyEntry.getKey();
            DeclareMethod getter = prop.getGetter();
            DeclareMethod setter = prop.getSetter();
            if (getter == null) {
                if (setter != null && setter.isAbstractMethod()) {
                    String type = prop.getActualType();
                    filer.privateField(name, type).withGetterMethod(NOTHING).withSetterMethod(OVERRIDE);
                }
            } else if (getter.isAbstractMethod()) {
                DeclField field = filer.privateField(name, getter.getActualType());
                field.withGetterMethod(NOTHING);
                if (setter == null) {
                    field.withSetterMethod(NOTHING);
                } else if (Assert2.assertAbstractMethod(setter, "setter")) {
                    field.withSetterMethod(OVERRIDE);
                }
            } else if (setter != null) {
                Assert2.assertNonAbstractMethod(setter, "setter");
            }
        }
    }

    private void implementForInterface(DeclJavaFile filer) {
        for (Map.Entry<String, DeclareProperty> propertyEntry : entrySet()) {
            DeclareProperty prop = propertyEntry.getValue();
            String name = propertyEntry.getKey();
            DeclareMethod getter = prop.getGetter();
            DeclareMethod setter = prop.getSetter();
            if (getter == null && setter != null && !setter.isDefaultMethod()) {
                String type = setter.getActualType();
                filer.privateField(name, type).withGetterMethod(NOTHING).withSetterMethod(OVERRIDE);
                Assert2.assertNonSetters(prop.getSetters(), type);
            } else if (getter != null && Assert2.assertEffectMethod(getter, setter)) {
                String type = getter.getActualType();
                DeclField field = filer.privateField(name, type).withGetterMethod(OVERRIDE);
                if (setter != null) {
                    field.withSetterMethod(OVERRIDE);
                } else {
                    field.withSetterMethod(NOTHING);
                }
                Assert2.assertNonSetters(prop.getSetters(), type);
            }
        }
    }
}
