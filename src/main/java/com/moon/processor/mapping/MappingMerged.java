package com.moon.processor.mapping;

import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareProperty;

/**
 * @author benshaoye
 */
public class MappingMerged {

    private final DeclareMapping mapping;
    private final DeclareProperty property;

    public MappingMerged(DeclareMapping mapping, DeclareProperty property) {
        this.mapping = mapping;
        this.property = property;
    }

    public boolean isDefaultMapping() { return mapping.isDefaultMapping(); }

    public DeclareMapping getMapping() { return mapping; }

    public DeclareProperty getProperty() { return property; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MappingMerged{");
        sb.append("mapping=").append(mapping);
        sb.append(", property=").append(property);
        sb.append('}');
        return sb.toString();
    }
}
