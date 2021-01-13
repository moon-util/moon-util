package com.moon.processor.file;

import com.moon.processor.manager.Importer;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.String2;

/**
 * @author benshaoye
 */
public class DeclMethod {

    public final static DeclMethod EMPTY = new Empty();

    private final Importer importer;
    private final DeclParams params;
    private final String name;
    private String returnType;

    public DeclMethod(Importer importer, String name, DeclParams params) {
        this.importer = importer;
        this.params = params;
        this.name = name;
    }

    public static DeclMethod ofGetter(Importer importer, String name, String type) {
        return new DeclMethod(importer, String2.toGetterName(name, type), DeclParams.of()).returnTypeof(type);
    }

    public static DeclMethod ofSetter(Importer importer, String name, String type) {
        return new DeclMethod(importer, String2.toSetterName(name), DeclParams.of(name, type));
    }

    public DeclMethod returnTypeof(String typePattern, Object... values) {
        this.returnType = Formatter2.toTypedFormatted(importer, typePattern, values);
        return this;
    }

    public DeclMethod paramsOf(DeclParams params) {
        return this;
    }

    public DeclMethod scriptOf(String script, String...values) {
        return this;
    }

    public DeclMethod returning(String script) {
        return this;
    }

    public String[] getScripts() {
        return Const2.EMPTY;
    }

    private final static class Empty extends DeclMethod {

        public Empty() { super(null, null, null); }

        @Override
        public String[] getScripts() { return Const2.EMPTY; }
    }
}
