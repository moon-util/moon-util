package com.moon.processor.model;

import com.moon.processor.manager.ClassnameManager;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.utils.Element2;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
public class DefMapper implements JavaFileWriteable {

    private final DeclareClass thisClass, thatClass;
    private final String pkg, classname;

    public DefMapper(DeclareClass thisClass, DeclareClass thatClass, ClassnameManager registry) {
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

    public String getClassname() { return classname; }

    @Override
    public void writeJavaFile(Filer filer) {
        thisClass.writeJavaFile(filer);
        thatClass.writeJavaFile(filer);
        if (!written) {
            this.doWriteJavaFile(filer);
        }
        // TODO write mapper java file
    }

    private boolean written = false;

    private void doWriteJavaFile(Filer filer) {
        DefClassWriter implementor = new DefClassWriter(DefClassWriter.Type.ENUM, getPkg(), getClassname());
        // TODO write mapper java file
        written = true;
    }
}
