package com.moon.processor.def;

import com.moon.mapper.BeanCopier;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.*;
import com.moon.processor.holder.NameHolder;
import com.moon.processor.mapping.MappingMerged;
import com.moon.processor.mapping.MappingType;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefBeanCopier implements JavaFileWriteable {

    private final DeclaredPojo thisPojo, thatPojo;
    private final String pkg, classname, simpleName;

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefBeanCopier(DeclaredPojo thisPojo, DeclaredPojo thatPojo, NameHolder registry) {
        this.thisPojo = thisPojo;
        this.thatPojo = thatPojo;
        this.pkg = Element2.getPackageName(thisPojo.getDeclareElement());
        TypeElement thisElem = thisPojo.getDeclareElement();
        TypeElement thatElem = thatPojo.getDeclareElement();
        this.classname = registry.getCopierClassname(thisElem, thatElem);
        this.simpleName = Element2.getSimpleName(this.classname);
    }

    public DeclaredPojo getThisPojo() { return thisPojo; }

    public DeclaredPojo getThatPojo() { return thatPojo; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    public Set<String> getOrders() { return orders; }

    private DeclJavaFile getDeclJavaFile() {
        DeclJavaFile filer = DeclJavaFile.classOf(getPkg(), simpleName);
        filer.markedOf(DeclMarked::ofComponent);
        filer.implement(getInterfaceDecl()).enumNamesOf(Const2.INSTANCE);
        buildUnsafeCopyMethods(filer);
        buildConvertMethods(filer);
        return filer;
    }

    private DeclMethod unsafeCopy2(DeclMethod method) {
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

    private void buildUnsafeCopyMethods(DeclJavaFile file) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();

        // unsafeCopy
        DeclParams params = DeclParams.of("self", thisName).add("that", thatName);
        DeclMethod unsafeCopy = file.publicMethod("unsafeCopy", params).override();
        unsafeCopy2(unsafeCopy).returnTypeof(thatName).returning("that");
    }

    private void buildConvertMethods(DeclJavaFile file) {
        DeclaredPojo thatClass = getThatPojo();
        String thisName = getThisPojo().getThisClassname();
        String thatName = thatClass.getThisClassname();
        String thatImpl = thatClass.getImplClassname();

        // convert
        DeclParams parameters = DeclParams.of("self", thisName);
        String template = "self == null ? null : unsafeCopy(self, new {}())";
        DeclMethod convert = file.publicMethod("convert", parameters).override();
        convert.returnTypeof(thatName).returning(template, convert.onImported(thatImpl));
    }

    @Override
    public void writeJavaFile(JavaWriter writer) { getDeclJavaFile().writeJavaFile(writer); }

    private String getInterfaceDecl() {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        return String.format("%s<%s, %s>", BeanCopier.class.getCanonicalName(), thisName, thatName);
    }
}
