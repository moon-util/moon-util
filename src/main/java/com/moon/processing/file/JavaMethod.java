package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaMethod extends JavaAnnotable {

    private final String name;
    private final JavaParameters parameters;

    public JavaMethod(Importer importer,String name, JavaParameters parameters) {
        super(importer);
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public JavaMethod addModifierWith(Modifier modifier) {
        super.addModifierWith(modifier);
        return this;
    }

    public String getUniqueKey() {
        return null;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        addr.next();
        super.appendTo(addr);
        for (Modifier modifier : getModifiers()) {
            addr.newAdd(modifier.name().toLowerCase()).add(' ');
        }
    }

    @Override
    public Set<Modifier> getAllowedModifiers() {
        return Modifier2.FOR_METHOD;
    }
}
