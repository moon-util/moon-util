package com.moon.processing.file;

import com.moon.processor.utils.String2;

/**
 * 用于生成普通类
 *
 * @author benshaoye
 */
public class JavaClassFile extends BaseImplFile {

    private String superclass;

    public JavaClassFile(String packageName, String simpleName) {
        super(packageName, simpleName);
    }

    @Override
    protected boolean inInterface() { return false; }

    public JavaClassFile extend(String superclass, Object... types) {
        this.superclass = String2.format(superclass, types);
        return this;
    }

    @Override
    public String getJavaContent() {
        JavaAddr addr = newPackagedJavaAddr();
        JavaAddr.Mark importMark = addr.mark();
        super.appendTo(addr.next());
        addr.newAdd("public class ").add(getSimpleName()).add(getGenericDeclared()).add(getSuperclassDeclared())
            .add(getInterfacesWillImplemented("implements")).add(" {").start();

        appendFieldsAndBlock(addr);
        appendMethods(addr);
        appendGetterSetterMethods(addr);
        return returning(addr, importMark);
    }

    private String getSuperclassDeclared() {
        return superclass == null ? "" : " extends " + onImported(superclass);
    }
}
