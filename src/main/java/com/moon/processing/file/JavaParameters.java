package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaParameters extends JavaAnnotable {

    private final Map<String, JavaParameter> parameters = new LinkedHashMap<>();

    JavaParameters(Importer importer) { super(importer); }

    public JavaParameters add(String name, String typeTemplate, Object... types) {
        parameters.put(name, new JavaParameter(getImporter(), name, typeTemplate, types));
        return this;
    }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_PARAMETER; }
}
