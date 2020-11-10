package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * @author moonsky
 */
final class MappingWriter implements JavaFileWritable {

    private final InterfaceWriter interfaceWriter = new InterfaceWriter();
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
        Logger.warn(def);
        if (def.isInterface()) {
            writeInterfaceImpl(filer, (InterDefinition) def);
        }
        EnvUtils.newJavaFile(filer, toMappingSourceName(def), () -> {
            StringAdder adder = new StringAdder();
            adder.add("package com.moon.mapping;");
            adder.add("enum ").add(toMappingEnumClassname(def)).implement(BeanMapping.class).add("{TO,");
            for (Map.Entry<String, ? extends BaseDefinition> defEntry : getForAllDefined().entrySet()) {
                BaseDefinition definition = defEntry.getValue();
                if (definition.isInterface()) {
                    writeInterfaceImpl(filer, (InterDefinition) definition);
                }
                // adder.add();
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

    public void writeInterfaceImpl(Filer filer, InterDefinition def) {
        String pkg = def.getPackageName();
        String simpleName = def.getInterfaceImplSimpleName();
        String interfaceSrcFile = ElementUtils.concat(pkg, ".", simpleName);
        try {
            EnvUtils.newJavaFile(filer, interfaceSrcFile, () -> {
                final StringAdder adder = new StringAdder();
                adder.add("package ").add(pkg).add(";");
                adder.add("public class ").add(simpleName).implement(getInterfaces(def)).add("{");
                adder.add(def.implementation(simpleName));
                return adder.add("}").toString();
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("all")
    private static String[] getInterfaces(InterDefinition def) {
        return new String[]{
            def.getQualifiedName(),//
            Cloneable.class.getCanonicalName(),//
            Serializable.class.getCanonicalName()//
        };
    }
}
