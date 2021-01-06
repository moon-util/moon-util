package com.moon.processor.model;

import com.moon.mapper.BeanCopier;
import com.moon.mapper.BeanMapper;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.NameManager;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Holder;

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
        DefJavaFiler filer = DefJavaFiler.enumOf(getPkg(), getClassname());
        filer.implement(getInterfaceDecl()).enumsOf(Const2.INSTANCE);
        buildUnsafeCopyMethods(filer);
        buildConvertMethods(filer);
        return filer;
    }

    private DefMethod unsafeCopy(DefMethod method) {
        DeclaredPojo thisPojo = getThisPojo();
        DeclaredPojo thatPojo = getThatPojo();
        String thatClassname = thatPojo.getThisClassname();
        for (Map.Entry<String, DeclareProperty> propertyEntry : thisPojo.entrySet()) {
            DeclareProperty self = propertyEntry.getValue();
            DeclareMapping mapping = self.getForwardMapping(thatClassname);
            if (mapping.isIgnoreForward()) {
                continue;
            }
            DeclareProperty that = thatPojo.get(mapping.getField(propertyEntry.getKey()));
            method.convert(propertyEntry.getKey(), self, that, mapping);
        }
        return method;
    }

    private void buildUnsafeCopyMethods(DefJavaFiler filer) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();

        // unsafeCopy
        DefParameters parameters = DefParameters.of("self", thisName).add("that", thatName);
        DefMethod forward = filer.publicMethod("unsafeCopy", thatName, parameters).override();
        unsafeCopy(forward).returning("that");
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
