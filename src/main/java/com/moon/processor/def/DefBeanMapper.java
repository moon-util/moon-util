package com.moon.processor.def;

import com.moon.mapper.BeanMapper;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclMarked;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.holder.NameHolder;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefBeanMapper implements JavaFileWriteable {

    private final DeclaredPojo thisPojo, thatPojo;
    private final String pkg, classname, simpleClassname;
    private final DefBeanCopier forward, backward;

    private final Set<String> orders = new LinkedHashSet<>();

    { orders.add("id"); }

    public DefBeanMapper(
        DefBeanCopier forward,
        DefBeanCopier backward,
        DeclaredPojo thisPojo,
        DeclaredPojo thatPojo,
        NameHolder registry
    ) {
        this.forward = forward;
        this.backward = backward;
        this.thisPojo = thisPojo;
        this.thatPojo = thatPojo;
        this.pkg = Element2.getPackageName(thisPojo.getDeclareElement());
        TypeElement thisElem = thisPojo.getDeclareElement();
        TypeElement thatElem = thatPojo.getDeclareElement();
        this.classname = registry.getMapperClassname(thisElem, thatElem);
        this.simpleClassname = Element2.getSimpleName(this.classname);
    }

    public DeclaredPojo getThisPojo() { return thisPojo; }

    public DeclaredPojo getThatPojo() { return thatPojo; }

    public DefBeanCopier getForward() { return forward; }

    public DefBeanCopier getBackward() { return backward; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        getDeclJavaFile().writeJavaFile(writer);
    }

    private DeclJavaFile getDeclJavaFile() {
        DeclJavaFile filer = DeclJavaFile.classOf(getPkg(), simpleClassname);
        filer.implement(getInterfaceDecl()).enumNamesOf(Const2.INSTANCE).markedOf(DeclMarked::ofComponent);

        // copier
        filer.privateEnumRef(Const2.FORWARD, getForward().getClassname(), Const2.INSTANCE);
        filer.privateEnumRef(Const2.BACKWARD, getBackward().getClassname(), Const2.INSTANCE);

        // methods
        buildUnsafeConvertMethods(filer);
        return filer;
    }

    private void buildUnsafeConvertMethods(DeclJavaFile file) {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        DeclParams params = DeclParams.of("self", thisName).add("that", thatName);

        // forward
        DeclMethod forward = file.publicMethod("unsafeForward", params).returnTypeof(thatName);
        forward.override().returning("{}.unsafeCopy(self, that)", Const2.FORWARD);
        // backward
        DeclMethod backward = file.publicMethod("unsafeBackward", params).returnTypeof(thisName);
        backward.override().returning("{}.unsafeCopy(that, self)", Const2.BACKWARD);

        // doForward
        DeclMethod doForward = file.publicMethod("doForward", params).returnTypeof(thatName);
        doForward.override().returning("{}.copy(self, that)", Const2.FORWARD);
        // doBackward
        DeclMethod doBackward = file.publicMethod("doBackward", params).returnTypeof(thisName);
        doBackward.override().returning("{}.copy(that, self)", Const2.BACKWARD);

        // forward
        DeclParams forwardParams = DeclParams.of("self", thisName);
        DeclMethod cForward = file.publicMethod("doForward", forwardParams).returnTypeof(thatName);
        cForward.override().returning("{}.convert(self)", Const2.FORWARD);
        // backward
        DeclParams backwardParams = DeclParams.of("that", thatName);
        DeclMethod cBackward = file.publicMethod("doBackward", backwardParams).returnTypeof(thisName);
        cBackward.override().returning("{}.convert(that)", Const2.BACKWARD);
    }

    private String getInterfaceDecl() {
        String thisName = getThisPojo().getThisClassname();
        String thatName = getThatPojo().getThisClassname();
        return String.format("%s<%s, %s>", BeanMapper.class.getCanonicalName(), thisName, thatName);
    }
}
