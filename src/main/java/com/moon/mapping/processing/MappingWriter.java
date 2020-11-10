package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.Map;

/**
 * @author moonsky
 */
final class MappingWriter implements JavaFileWritable {

    private final BaseDefinition thisDefined;
    private final Map<String, ? extends BaseDefinition> forAllDefined;

    public MappingWriter(BaseDefinition thisDefined, Map<String, ? extends BaseDefinition> forAllDefined) {
        this.forAllDefined = forAllDefined;
        this.thisDefined = thisDefined;
    }

    public BaseDefinition getThisDefined() { return thisDefined; }

    public Map<String, ? extends BaseDefinition> getForAllDefined() { return forAllDefined; }

    @Override
    public void writeJavaFile(Filer filer) throws IOException {
        final BaseDefinition def = getThisDefined();
        EnvUtils.newJavaFile(filer, toMappingSourceName(def), () -> {
            StringAdder adder = new StringAdder();
            adder.add("package com.moon.mapping;");
            adder.add("enum ").add(toMappingEnumClassname(def)).implement(BeanMapping.class).add("{TO,");
            for (Map.Entry<String, ? extends BaseDefinition> defEntry : getForAllDefined().entrySet()) {
                def.addBeanMapping(adder, defEntry.getValue());
            }
            adder.add(";").add(def.implMappingSharedMethods());
            return adder.add("}").toString();
        });
    }

    @SuppressWarnings("all")
    private String toMappingSourceName(final BaseDefinition def) {
        return ElementUtils.concat(//
            MappingUtil.class.getPackage().getName(),//
            ".", toMappingEnumClassname(def));
    }

    private String toMappingEnumClassname(final BaseDefinition def) {
        return StringUtils.toMappingClassname(def.getQualifiedName());
    }
}
