package com.moon.processor.model;

import com.moon.mapper.Converter;
import com.moon.processor.Holder;
import com.moon.processor.Importer;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.manager.ClassnameManager;
import com.moon.processor.utils.Element2;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefConverter implements JavaFileWriteable {

    private final DeclareClass thisClass, thatClass;
    private final String pkg, classname;
    private final Importer importer = new Importer();

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefConverter(DeclareClass thisClass, DeclareClass thatClass, ClassnameManager registry) {
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
    public void writeJavaFile(Filer filer) throws IOException {
        thisClass.writeJavaFile(filer);
        thatClass.writeJavaFile(filer);
        if (!isWritten()) {
            this.doWriteJavaFile(filer);
        }
    }

    private boolean written = false;

    private void doWriteJavaFile(Filer filer) throws IOException {
        DefClassWriter writer = new DefClassWriter(DefClassWriter.Type.ENUM, getPkg(), getClassname());
        DeclareClass thisClass = getThisClass();
        DeclareClass thatClass = getThatClass();
        String thisName = thisClass.getThisClassname();
        String thatName = thatClass.getThisClassname();
        String thatImpl = thatClass.getImplClassname();
        writer.implement(getInterfaceDecl(thisName, thatName));
        DefParameters params = DefParameters.of("thisObject", thisName);

        // convert
        DefMethod cvt = writer.publicMethod("convert", thatName, params);
        cvt.setOverride(true);
        String convert = "thisObject == null ? null : unsafeConvert(thisObject, new {name}())";
        cvt.returning(Holder.name.on(convert, writer.getImporter().onImported(thatImpl)));

        // unsafe
        DefMethod unsafe = writer.publicMethod("unsafeConvert", thatName, params.add("thatObject", thatName));
        unsafe.setOverride(true);
        mapDeclaredClass(thisClass, thatClass);
        unsafe.returning("thatObject");

        // write
        writer.writeJavaFile(filer);
        written = true;
    }

    private void mapDeclaredClass(DeclareClass thisClass, DeclareClass thatClass) {

    }

    private String getInterfaceDecl(String thisName, String thatName) {
        return String.format("%s<%s, %s>", Converter.class.getCanonicalName(), thisName, thatName);
    }
}
