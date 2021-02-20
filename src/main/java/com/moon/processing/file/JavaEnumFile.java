package com.moon.processing.file;

import com.moon.processor.utils.Collect2;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaEnumFile extends BaseImplFile {

    private final Set<String> enums = new LinkedHashSet<>();

    public JavaEnumFile(String packageName, String simpleName) {
        super(packageName, simpleName);
    }

    @Override
    protected boolean inInterface() { return false; }

    public JavaEnumFile enumOf(String... enums) {
        Collect2.addAll(this.enums, enums);
        return this;
    }

    @Override
    public String getJavaContent() {
        JavaAddr addr = newPackagedJavaAddr();
        JavaAddr.Mark importMark = addr.mark();
        super.appendTo(addr.next());
        addr.newAdd("public enum ").add(getSimpleName()).add(getGenericDeclared())
            .add(getInterfacesWillImplemented("implements")).add(" {").start();
        addEnumValues(addr);

        appendFieldsAndBlock(addr);
        appendMethods(addr);
        appendGetterSetterMethods(addr);
        return returning(addr, importMark);
    }

    private void addEnumValues(JavaAddr addr) {
        Set<String> enums = this.enums;
        if (enums.isEmpty()) {
            addr.newAdd(";");
        } else {
            addr.newAdd(String.join(", ", enums)).add(";");
        }
    }
}
