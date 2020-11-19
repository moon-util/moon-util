package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.moon.mapping.processing.ElementUtils.getSimpleName;

/**
 * @author moonsky
 */
@ConditionalOnMissingBean(MappingWriter.class)
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
        final String classname = toSourceName(getThisDefined());
        EnvUtils.newJavaFile(filer, classname, impl(classname));
    }

    public static void forAutoConfig(Filer filer, List<MappingWriter> writers) throws IOException {
        if (!Imported.CONFIGURATION) {
            return;
        }
        final Manager manager = getAllImportsNameForConfiguration();
        final String configName = "BeanMappingConfiguration";
        final NameGenerator generator = new NameGenerator();
        final StringAdder beansAdder = new StringAdder();
        beansAdder.add("@").add(manager.onImported(Configuration.class));
        beansAdder.add("@SuppressWarnings({\"all\",\"unchecked\"})");
        beansAdder.add(" public class ").add(configName).add(" {");
        for (MappingWriter writer : writers) {
            String thisCls = writer.getThisDefined().getCanonicalName();
            String thisSimpleName = manager.onImported(thisCls);
            generator.atFromClass(thisCls);
            Set entries = writer.getForAllDefined().entrySet();
            for (Object entry : entries) {
                Map.Entry<String, BaseDefinition> thatDef = (Map.Entry) entry;
                BaseDefinition definition = thatDef.getValue();
                // 类全名
                String thatCls = definition.getCanonicalName();
                String thatSimpleName = manager.onImported(thatCls);
                // bean name
                String thisBeanName = generator.get(thatCls);
                // @Bean @ConditionalOnMissingBean
                conditionalOnMissingBean(beansAdder, thisBeanName, manager);
                beansAdder.add(" public ").add(manager.onImported(BeanMapping.class));
                beansAdder.add("<").add(thisSimpleName).add(',').add(thatSimpleName).add("> ");
                beansAdder.add(thisBeanName).add("() {");
                beansAdder.add("return ").add(toEnumClass(thisCls)).dot();
                beansAdder.add("TO_").add(thatCls.replace('.', '_')).add(";}");
            }
        }
        beansAdder.add("}");
        StringAdder configAdder = new StringAdder().add("package ").pkg(BeanMapping.class).add(";");
        configAdder.add(manager.getImports()).add(beansAdder);
        StringAdder src = new StringAdder().pkg(MappingUtil.class).dot().add(configName);
        EnvUtils.newJavaFile(filer, src.toString(), configAdder);
    }

    private StringAdder impl(final String classname) {
        final BaseDefinition def = getThisDefined();
        final Manager manager = new Manager();
        StringAdder enumAdder = new StringAdder();
        for (BaseDefinition value : getForAllDefined().values()) {
            def.addBeanMapping(enumAdder, value, manager);
        }
        // 暂时不是想 MapMapping 和 ObjectMapping 了
        // StringAdder shardAdder = def.implMappingSharedMethods(manager);
        StringAdder adder = new StringAdder();
        adder.add("package ").pkg(BeanMapping.class).add(';').add(manager.getImports());
        adder.add("@SuppressWarnings({\"all\",\"unchecked\"}) enum ").add(getSimpleName(classname));
        adder.impl(BeanMapping.class).add("{TO,").add(enumAdder).add(';').add(manager.ofStatic());
        return adder.add("}");
    }

    private static Manager getAllImportsNameForConfiguration() {
        Manager manager = new Manager();
        if (Imported.CONDITIONAL_ON_MISSING_BEAN) { manager.onImported(ConditionalOnMissingBean.class); }
        if (Imported.CONFIGURATION) { manager.onImported(Configuration.class); }
        if (Imported.BEAN) { manager.onImported(Bean.class); }
        manager.onImported(BeanMapping.class);
        return manager;
    }

    @SuppressWarnings("all")
    private static String toSourceName(final BaseDefinition def) {
        return new StringAdder().pkg(MappingUtil.class).dot().add(toEnumClass(def)).toString();
    }

    private static void conditionalOnMissingBean(StringAdder adder, String thisBeanName,final Manager manager) {
        if (Imported.CONDITIONAL_ON_MISSING_BEAN) {
            adder.add("@").add(manager.onImported(ConditionalOnMissingBean.class));
            adder.add("(name=\"").add(thisBeanName).add("\")");
        }
        if (Imported.BEAN) {
            adder.add("@").add(manager.onImported(Bean.class));
        }
        adder.space();
    }

    private static String toEnumClass(final BaseDefinition def) {
        return StringUtils.toMappingClassname(def.getQualifiedName());
    }

    private static String toEnumClass(final String classname) {
        return StringUtils.toMappingClassname(classname);
    }
}
