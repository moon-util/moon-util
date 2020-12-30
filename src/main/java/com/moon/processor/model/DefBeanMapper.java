package com.moon.processor.model;

import com.moon.mapper.BeanConverter;
import com.moon.mapper.annotation.IgnoreMode;
import com.moon.processor.manager.Importer;
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
public class DefBeanMapper {

    private final DeclaredPojo thisPojo, thatPojo;
    private final String pkg, classname;
    private final Importer importer = new Importer();

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefBeanMapper(DeclaredPojo thisPojo, DeclaredPojo thatPojo, NameManager registry) {
        this.thisPojo = thisPojo;
        this.thatPojo = thatPojo;
        this.pkg = Element2.getPackageName(thisPojo.getDeclareElement());
        TypeElement thisElem = thisPojo.getDeclareElement();
        TypeElement thatElem = thatPojo.getDeclareElement();
        this.classname = registry.getMapperClassname(thisElem, thatElem);
    }

    public DeclaredPojo getThisPojo() { return thisPojo; }

    public DeclaredPojo getThatPojo() { return thatPojo; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    public DefJavaFiler getDefJavaFiler() {
        DefJavaFiler filer = DefJavaFiler.enumOf(getPkg(), getClassname());
        filer.implement(getInterfaceDecl()).enumsOf(Const2.INSTANCE);
        buildUnsafeConvertMethods(filer);
        buildConvertMethods(filer);
        return filer;
    }

    private DefMethod unsafeForward(DefMethod forward) {
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
            DefMapping propMapping = DefMapping.forward(self, that, mapping);
            forward.put(propertyEntry.getKey(), propMapping);
        }
        return forward;
    }

    private DefMethod unsafeBackward(DefMethod backward) {
        DeclaredPojo thisPojo = getThisPojo();
        DeclaredPojo thatPojo = getThatPojo();
        String thatClassname = thatPojo.getThisClassname();
        for (Map.Entry<String, DeclareProperty> propertyEntry : thisPojo.entrySet()) {
            DeclareProperty self = propertyEntry.getValue();
            DeclareMapping mapping = self.getBackwardMapping(thatClassname);
            if (mapping.isIgnoreBackward()) {
                continue;
            }
            DeclareProperty that = thatPojo.get(mapping.getField(propertyEntry.getKey()));
            DefMapping propMapping = DefMapping.backward(self, that, mapping);
            backward.put(propertyEntry.getKey(), propMapping);
        }
        return backward;
    }

    private void buildUnsafeConvertMethods(DefJavaFiler filer) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        DefParameters parameters = DefParameters.of("self", thisName).add("that", thatName);

        // forward
        DefMethod forward = filer.publicMethod("unsafeForward", thatName, parameters).override();
        unsafeForward(forward).returning("that");

        // backward
        DefMethod backward = filer.publicMethod("unsafeBackward", thisName, parameters).override();
        unsafeBackward(backward).returning("self");
    }

    private void buildConvertMethods(DefJavaFiler filer) {
        DeclaredPojo thisClass = getThisPojo();
        DeclaredPojo thatClass = getThatPojo();
        String thisName = thisClass.getThisClassname();
        String thatName = thatClass.getThisClassname();
        String thisImpl = thisClass.getImplClassname();
        String thatImpl = thatClass.getImplClassname();

        // forward
        DefParameters forwardParams = DefParameters.of("self", thisName);
        String forwardTemplate = "self == null ? null : unsafeForward(self, new {name}())";
        DefMethod forward = filer.publicMethod("doForward", thatName, forwardParams).override();
        forward.returning(Holder.name.on(forwardTemplate, filer.getImporter().onImported(thatImpl)));

        // backward
        DefParameters backwardParams = DefParameters.of("that", thatName);
        String backwardTemplate = "that == null ? null : unsafeBackward(new {name}(), that)";
        DefMethod backward = filer.publicMethod("doBackward", thisName, backwardParams).override();
        backward.returning(Holder.name.on(backwardTemplate, filer.getImporter().onImported(thisImpl)));
    }

    private String getInterfaceDecl() {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        return String.format("%s<%s, %s>", BeanConverter.class.getCanonicalName(), thisName, thatName);
    }
}
