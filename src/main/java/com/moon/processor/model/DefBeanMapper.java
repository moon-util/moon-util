package com.moon.processor.model;

import com.moon.mapper.BeanConverter;
import com.moon.processor.manager.Importer;
import com.moon.processor.manager.NameManager;
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

    private DefMethod unsafeConvert(DefMethod method, ConvertType type) {
        DeclaredPojo thisPojo = getThisPojo();
        DeclaredPojo thatPojo = getThatPojo();
        String thatClassname = thatPojo.getThisClassname();
        for (Map.Entry<String, DeclareProperty> propertyEntry : thisPojo.entrySet()) {
            DeclareProperty self = propertyEntry.getValue();
            DeclareMapping mapping = type.getMappingFor(self, thatClassname);
            Log2.warning(propertyEntry.getKey() + "\t" + mapping.toString());
            if (type.isIgnored(mapping)) {
                continue;
            }
            DeclareProperty that = thatPojo.get(mapping.getField(propertyEntry.getKey()));
            if (that != null) {
                switch (type) {
                    case FORWARD:
                        method.forward(propertyEntry.getKey(), self, that, mapping);
                        break;
                    case BACKWARD:
                        method.backward(propertyEntry.getKey(), self, that, mapping);
                        break;
                    default:
                        break;
                }
            }
        }
        return method;
    }

    private void buildUnsafeConvertMethods(DefJavaFiler filer) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        DefParameters parameters = DefParameters.of("self", thisName).add("that", thatName);

        // forward
        DefMethod forward = filer.publicMethod("unsafeForward", thatName, parameters).override();
        unsafeConvert(forward, ConvertType.FORWARD).returning("that");

        // backward
        DefMethod backward = filer.publicMethod("unsafeBackward", thisName, parameters).override();
        unsafeConvert(backward, ConvertType.BACKWARD).returning("self");
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
