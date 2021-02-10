package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaMethod extends JavaAnnotable {

    private final JavaParameters parameters;

    public JavaMethod(Importer importer) {
        super(importer);
        this.parameters = new JavaParameters(importer);
    }



    @Override
    public Set<Modifier> getAllowedModifiers() {
        return Modifier2.FOR_METHOD;
    }
}
