package com.moon.processor.model;

import com.moon.mapper.BeanConverter;
import com.moon.processor.manager.Importer;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.ClassnameManager;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Holder;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefBeanMapper implements JavaFileWriteable {

    private final DeclareClass thisClass, thatClass;
    private final String pkg, classname;
    private final Importer importer = new Importer();

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefBeanMapper(DeclareClass thisClass, DeclareClass thatClass, ClassnameManager registry) {
        this.thisClass = thisClass;
        this.thatClass = thatClass;
        this.pkg = Element2.getPackageName(thisClass.getDeclareElement());
        TypeElement thisElem = thisClass.getDeclareElement();
        TypeElement thatElem = thatClass.getDeclareElement();
        this.classname = registry.getMapperClassname(thisElem, thatElem);
    }

    public DeclareClass getThisClass() { return thisClass; }

    public DeclareClass getThatClass() { return thatClass; }

    public String getPkg() { return pkg; }

    public boolean isWritten() { return written; }

    public String getClassname() { return classname; }

    @Override
    public void writeJavaFile(JavaWriter filer) throws IOException {
        thisClass.writeJavaFile(filer);
        thatClass.writeJavaFile(filer);
        if (!isWritten()) {
            this.doWriteJavaFile(filer);
        }
    }

    private boolean written = false;

    private void doWriteJavaFile(JavaWriter writer) throws IOException {
        DefJavaFiler filer = DefJavaFiler.enumOf(getPkg(), getClassname());
        filer.implement(getInterfaceDecl()).enumsOf("INSTANCE");
        buildUnsafeConvertMethods(filer);
        buildConvertMethods(filer);
        // write
        filer.writeJavaFile(writer);
        written = true;
    }

    private void buildUnsafeConvertMethods(DefJavaFiler filer) {
        String thisName = getThisClass().getThisClassname();
        String thatName = getThatClass().getThisClassname();
        DefParameters parameters = DefParameters.of("self", thisName).add("that", thatName);

        // forward
        DefMethod forward = filer.publicMethod("unsafeForward", thatName, parameters).override(true);
        unsafeForward(forward).returning("that");

        // backward
        DefMethod backward = filer.publicMethod("unsafeBackward", thatName, parameters).override(true);
        unsafeBackward(backward).returning("self");
    }

    private DefMethod unsafeForward(DefMethod forward) {
        return forward;
    }

    private DefMethod unsafeBackward(DefMethod backward) {
        return backward;
    }

    private void buildConvertMethods(DefJavaFiler filer) {
        DeclareClass thisClass = getThisClass();
        DeclareClass thatClass = getThatClass();
        String thisName = thisClass.getThisClassname();
        String thatName = thatClass.getThisClassname();
        String thisImpl = thisClass.getImplClassname();
        String thatImpl = thatClass.getImplClassname();

        // forward
        DefParameters forwardParams = DefParameters.of("self", thisName);
        String forwardTemplate = "self == null ? null : unsafeForward(self, new {name}())";
        DefMethod forward = filer.publicMethod("doForward", thatName, forwardParams).override(true);
        forward.returning(Holder.name.on(forwardTemplate, filer.getImporter().onImported(thatImpl)));

        // backward
        DefParameters backwardParams = DefParameters.of("that", thatName);
        String backwardTemplate = "that == null ? null : unsafeBackward(new {name}(), that)";
        DefMethod backward = filer.publicMethod("doBackward", thisName, backwardParams).override(true);
        backward.returning(Holder.name.on(backwardTemplate, filer.getImporter().onImported(thisImpl)));
    }

    private String getInterfaceDecl() {
        String thisName = getThisClass().getThisClassname();
        String thatName = getThatClass().getThisClassname();
        return String.format("%s<%s, %s>", BeanConverter.class.getCanonicalName(), thisName, thatName);
    }
}
