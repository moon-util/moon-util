package com.moon.processor.model;

import com.moon.processor.manager.ConstManager;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.Imported;
import com.moon.processor.utils.String2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.moon.processor.utils.String2.newLine;

/**
 * property name : property mapping
 *
 * @author benshaoye
 */
public class DefMethod {

    private final String declare;
    private final Importer importer;
    private final ConstManager constManager;
    private boolean override;
    private boolean autowired;
    private boolean autowiredRequired;
    private String qualifierName;

    private final LinkedHashMap<String, DefMapping> mappings = new LinkedHashMap<>();

    public DefMethod(String declare, Importer importer, ConstManager constManager) {
        this.constManager = constManager;
        this.declare = declare;
        this.importer = importer;
    }

    public boolean isOverride() { return override; }

    public DefMethod override() { return override(true); }

    public DefMethod override(boolean override) {
        setOverride(override);
        return this;
    }

    public void setOverride(boolean override) { this.override = override; }

    public DefMethod autowired() { return autowired(true); }

    public DefMethod autowired(boolean autowired) {
        this.setAutowired(autowired);
        return this;
    }

    public boolean isAutowired() { return autowired; }

    public void setAutowired(boolean autowired) { this.autowired = autowired; }

    public Importer getImporter() { return importer; }

    public String getDeclare() { return declare; }

    public boolean isAutowiredRequired() { return autowiredRequired; }

    public DefMethod setAutowiredRequired(boolean autowiredRequired) {
        this.autowiredRequired = autowiredRequired;
        return this;
    }

    public String getQualifierName() { return qualifierName; }

    public DefMethod setQualifierName(String qualifierName) {
        this.qualifierName = qualifierName;
        return this;
    }

    public DefMapping forward(
        String name, DeclareProperty thisProp, DeclareProperty thatProp, DeclareMapping mapping
    ) {
        DefMapping def = DefMapping.forward(thisProp, thatProp, mapping, constManager);
        mappings.put(name, def);
        return def;
    }

    public DefMapping backward(
        String name, DeclareProperty thisProp, DeclareProperty thatProp, DeclareMapping mapping
    ) {
        DefMapping def = DefMapping.backward(thisProp, thatProp, mapping, constManager);
        mappings.put(name, def);
        return def;
    }

    public void returning(String script) { mappings.put(null, DefMapping.returning(script)); }

    public String toString(int indent) {
        String space = String2.indent(indent);
        StringBuilder sb = new StringBuilder();
        if (isOverride()) {
            newLine(sb, space).append('@').append(getImporter().onImported(Override.class));
        }
        if (isAutowired() && Imported.AUTOWIRED) {
            newLine(sb, space).append('@').append(getImporter().onImported(Autowired.class));
            if (isAutowiredRequired()) {
                sb.append("(required = true");
            }
        }
        if (isAutowired() && Imported.QUALIFIER) {
            newLine(sb, space).append('@').append(getImporter().onImported(Qualifier.class));
            if (String2.isNotEmpty(getQualifierName())) {
                sb.append("(name = \"").append(getQualifierName()).append("\")");
            }
        }
        newLine(sb, space).append(declare).append(" {");
        // mappings
        for (Map.Entry<String, DefMapping> mappingEntry : mappings.entrySet()) {
            if (mappingEntry.getKey() == null) {
                // ignore return script
                continue;
            }
            appendScript(sb, space, mappingEntry.getValue());
        }
        // return script
        appendScript(sb, space, mappings.get(null));
        return newLine(sb, space).append('}').toString();
    }

    @Override
    public String toString() { return toString(4); }

    private static void appendScript(StringBuilder sb, String space, DefMapping mapping) {
        String[] scripts = mapping.getScripts();
        if (scripts == null) {
            return;
        }
        for (String script : scripts) {
            newLine(sb, space).append(space).append(script);
        }
    }
}
