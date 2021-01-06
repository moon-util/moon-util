package com.moon.processor.model;

import com.moon.mapper.BeanMapper;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.NameManager;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Holder;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefBeanMapper implements JavaFileWriteable {

    private final DeclaredPojo thisPojo, thatPojo;
    private final String pkg, classname;
    private final DefBeanCopier forward, backward;

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefBeanMapper(DefBeanCopier forward,DefBeanCopier backward,DeclaredPojo thisPojo, DeclaredPojo thatPojo, NameManager registry) {
        this.forward = forward;
        this.backward = backward;
        this.thisPojo = thisPojo;
        this.thatPojo = thatPojo;
        this.pkg = Element2.getPackageName(thisPojo.getDeclareElement());
        TypeElement thisElem = thisPojo.getDeclareElement();
        TypeElement thatElem = thatPojo.getDeclareElement();
        this.classname = registry.getMapperClassname(thisElem, thatElem);
    }

    public DeclaredPojo getThisPojo() { return thisPojo; }

    public DeclaredPojo getThatPojo() { return thatPojo; }

    public DefBeanCopier getForward() { return forward; }

    public DefBeanCopier getBackward() { return backward; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    private DefJavaFiler getDefJavaFiler() {
        DefJavaFiler filer = DefJavaFiler.enumOf(getPkg(), getClassname());
        filer.implement(getInterfaceDecl()).enumsOf(Const2.INSTANCE);

        // copier
        filer.enumRef(getForward().getClassname(), Const2.FORWARD, Const2.INSTANCE);
        filer.enumRef(getBackward().getClassname(), Const2.BACKWARD, Const2.INSTANCE);

        // methods
        buildUnsafeConvertMethods(filer);
        return filer;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        getDefJavaFiler().writeJavaFile(writer);
    }

    private void buildUnsafeConvertMethods(DefJavaFiler filer) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        DefParameters parameters = DefParameters.of("self", thisName).add("that", thatName);

        // forward
        DefMethod forward = filer.publicMethod("unsafeForward", thatName, parameters).override();
        forward.returning(Holder.name.on("{name}.unsafeCopy(self, that)", Const2.FORWARD));
        // backward
        DefMethod backward = filer.publicMethod("unsafeBackward", thisName, parameters).override();
        backward.returning(Holder.name.on("{name}.unsafeCopy(that, self)", Const2.BACKWARD));

        // doForward
        DefMethod doForward = filer.publicMethod("doForward", thatName, parameters).override();
        doForward.returning(Holder.name.on("{name}.copy(self, that)", Const2.FORWARD));
        // doBackward
        DefMethod doBackward = filer.publicMethod("doBackward", thisName, parameters).override();
        doBackward.returning(Holder.name.on("{name}.copy(that, self)", Const2.BACKWARD));

        // forward
        DefParameters forwardParams = DefParameters.of("self", thisName);
        DefMethod cForward = filer.publicMethod("doForward", thatName, forwardParams).override();
        cForward.returning(Holder.name.on("{name}.convert(self)", Const2.FORWARD));
        // backward
        DefParameters backwardParams = DefParameters.of("that", thatName);
        DefMethod cBackward = filer.publicMethod("doBackward", thisName, backwardParams).override();
        cBackward.returning(Holder.name.on("{name}.convert(that)", Const2.BACKWARD));
    }

    private String getInterfaceDecl() {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        return String.format("%s<%s, %s>", BeanMapper.class.getCanonicalName(), thisName, thatName);
    }
}
