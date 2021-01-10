package com.moon.processor.def;

import com.moon.mapper.BeanCopier;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.NameManager;
import com.moon.processor.mapping.MappingMerged;
import com.moon.processor.mapping.MappingType;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Holder;
import com.moon.processor.utils.Log2;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefBeanCopier implements JavaFileWriteable {

    private final DeclaredPojo thisPojo, thatPojo;
    private final String pkg, classname;

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefBeanCopier(DeclaredPojo thisPojo, DeclaredPojo thatPojo, NameManager registry) {
        this.thisPojo = thisPojo;
        this.thatPojo = thatPojo;
        this.pkg = Element2.getPackageName(thisPojo.getDeclareElement());
        TypeElement thisElem = thisPojo.getDeclareElement();
        TypeElement thatElem = thatPojo.getDeclareElement();
        this.classname = registry.getCopierClassname(thisElem, thatElem);
    }

    public DeclaredPojo getThisPojo() { return thisPojo; }

    public DeclaredPojo getThatPojo() { return thatPojo; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    public Set<String> getOrders() { return orders; }

    private DefJavaFiler getDefJavaFiler() {
        DefJavaFiler filer = DefJavaFiler.classOf(getPkg(), getClassname());
        filer.implement(getInterfaceDecl()).enumsOf(Const2.INSTANCE);
        buildUnsafeCopyMethods(filer);
        buildConvertMethods(filer);
        return filer;
    }

    private DefMethod unsafeCopy2(DefMethod method) {
        DeclaredPojo thisPojo = getThisPojo();
        DeclaredPojo thatPojo = getThatPojo();
        String thisClassname = thisPojo.getThisClassname();
        String thatClassname = thatPojo.getThisClassname();
        for (Map.Entry<String, DeclareProperty> propertyEntry : thatPojo.entrySet()) {
            String field = propertyEntry.getKey();
            DeclareProperty thatProp = propertyEntry.getValue();
            // 1. 选择注入方声明的指定 @Mapping
            DeclareMapping mapping = thatProp.getPullMapping(thisClassname);
            if (!mapping.isDefaultMapping()) {
                DeclareProperty self = thisPojo.get(mapping.getField(field));
                method.convert(field, self, thatProp, mapping, MappingType.SETTER);
                continue;
            }
            // 2. 选择输出方声明的指定 @Mapping
            MappingMerged merged = thisPojo.findPushMappingAssigned(thatClassname, field);
            if (merged != null && !merged.isDefaultMapping()) {
                method.convert(field, merged.getProperty(), thatProp, merged.getMapping(), MappingType.GETTER);
                continue;
            }
            // 3. 选择注入方公共 @Mapping
            mapping = thatProp.getPullMapping();
            if (!mapping.isDefaultMapping()) {
                DeclareProperty self = thisPojo.get(mapping.getField(field));
                method.convert(field, self, thatProp, mapping, MappingType.SETTER);
                continue;
            }
            // 4. 选择输出方公共 @Mapping
            merged = thisPojo.findPushMapping(field);
            if (merged == null || merged.isDefaultMapping()) {
                // 不存在输出方公共 mapping 或也是默认 mapping
                DeclareProperty self = thisPojo.get(mapping.getField(field));
                method.convert(field, self, thatProp, mapping, MappingType.GETTER);
            } else {
                // 存在输出方公共 mapping
                method.convert(field, merged.getProperty(), thatProp, merged.getMapping(), MappingType.GETTER);
            }
        }
        return method;
    }

    private void buildUnsafeCopyMethods(DefJavaFiler filer) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();

        // unsafeCopy
        DefParameters parameters = DefParameters.of("self", thisName).add("that", thatName);
        DefMethod forward = filer.publicMethod("unsafeCopy", thatName, parameters).override();
        unsafeCopy2(forward).returning("that");
    }

    private void buildConvertMethods(DefJavaFiler filer) {
        DeclaredPojo thatClass = getThatPojo();
        String thisName = getThisPojo().getThisClassname();
        String thatName = thatClass.getThisClassname();
        String thatImpl = thatClass.getImplClassname();

        // convert
        DefParameters parameters = DefParameters.of("self", thisName);
        String template = "self == null ? null : unsafeCopy(self, new {name}())";
        DefMethod convert = filer.publicMethod("convert", thatName, parameters).override();
        convert.returning(Holder.name.on(template, filer.onImported(thatImpl)));
    }

    @Override
    public void writeJavaFile(JavaWriter writer) { getDefJavaFiler().writeJavaFile(writer); }

    private String getInterfaceDecl() {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        return String.format("%s<%s, %s>", BeanCopier.class.getCanonicalName(), thisName, thatName);
    }
}
