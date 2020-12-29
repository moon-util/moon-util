package com.moon.processor.model;

import com.moon.processor.utils.Holder;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.Imported;
import com.moon.processor.utils.String2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;

import static com.moon.processor.utils.String2.newLine;

/**
 * @author benshaoye
 */
public class DefMethod extends ArrayList<String> {

    private final String declare;
    private final Importer importer;
    private boolean override;
    private boolean autowired;
    private boolean autowiredRequired;
    private String qualifierName;

    public DefMethod(String declare, Importer importer) {
        this.declare = declare;
        this.importer = importer;
    }

    public boolean isOverride() { return override; }

    public DefMethod override(boolean override) {
        this.override = override;
        return this;
    }

    public boolean isAutowired() { return autowired; }

    public DefMethod setAutowired(boolean autowired) {
        this.autowired = autowired;
        return this;
    }

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

    public void returning(String script) {
        this.add(Holder.var.on("return {var};", script));
    }

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
        for (String script : this) {
            newLine(sb, space).append(space).append(script);
        }
        return newLine(sb, space).toString();
    }

    @Override
    public String toString() { return toString(4); }
}
