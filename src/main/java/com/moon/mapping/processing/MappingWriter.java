package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (!DetectUtils.IMPORTED_CONFIGURATION) {
            return;
        }
        final String configName = "MappingConfiguration";
        final NameGenerator generator = new NameGenerator();
        final StringAdder adder = new StringAdder().add("package ").pkg(BeanMapping.class).add(";");
        adder.imports(getAllImportsNameForConfiguration(writers));
        // @Configuration
        adder.add("@").simpleCls(Configuration.class).add("@SuppressWarnings({\"all\",\"unchecked\"})");
        adder.add(" public class ").add(configName).add(" {");
        for (MappingWriter writer : writers) {
            String thisCls = writer.getThisDefined().getCanonicalName();
            generator.atFromClass(thisCls);
            Set entries = writer.getForAllDefined().entrySet();
            for (Object entry : entries) {
                Map.Entry<String, BaseDefinition> thatDef = (Map.Entry) entry;
                BaseDefinition definition = thatDef.getValue();
                // 类全名
                String thatCls = definition.getCanonicalName();
                // bean name
                String thisBeanName = generator.get(thatCls);
                // @Bean @ConditionalOnMissingBean
                conditionalOnMissingBean(adder, thisBeanName);
                adder.add(" public ").simpleCls(BeanMapping.class);
                adder.add("<").add(thisCls).add(',').add(thatCls).add("> ").add(thisBeanName).add("() {");
                adder.add("return ").add(toEnumClass(thisCls)).dot();
                adder.add("TO_").add(thatCls.replace('.', '_')).add(";}");
            }
        }
        adder.add("}");
        StringAdder src = new StringAdder().pkg(MappingUtil.class).dot().add(configName);
        EnvUtils.newJavaFile(filer, src.toString(), adder);
    }

    private StringAdder impl(final String classname) {
        final BaseDefinition def = getThisDefined();
        StringAdder adder = new StringAdder().add("package ").pkg(BeanMapping.class).add(';');
        adder.imports(getAllCanonicalName());
        adder.add("@SuppressWarnings({\"all\",\"unchecked\"}) enum ").add(getSimpleName(classname));
        adder.impl(BeanMapping.class).add("{TO,");
        StaticManager staticManager = new StaticManager();
        for (BaseDefinition value : getForAllDefined().values()) {
            def.addBeanMapping(adder, value, staticManager);
        }
        adder.add(';').add(staticManager);
        return adder.add(def.implMappingSharedMethods()).add("}");
    }

    private List<String> getAllCanonicalName() {
        List<CanonicalNameable> nameableArr = new ArrayList<>();
        nameableArr.add(getThisDefined());
        nameableArr.addAll(getForAllDefined().values());
        return nameableArr.stream().map(CanonicalNameable::getCanonicalName).collect(Collectors.toList());
    }

    private static Set<String> getAllImportsNameForConfiguration(List<MappingWriter> writers) {
        List<Class<?>> classes = new ArrayList<>();
        if (DetectUtils.IMPORTED_MISSING_BEAN) { classes.add(ConditionalOnMissingBean.class); }
        if (DetectUtils.IMPORTED_CONFIGURATION) { classes.add(Configuration.class); }
        if (DetectUtils.IMPORTED_BEAN) { classes.add(Bean.class); }
        classes.add(BeanMapping.class);
        Set<String> names = classes.stream().map(Class::getCanonicalName).collect(Collectors.toSet());
        return names;
    }

    @SuppressWarnings("all")
    private static String toSourceName(final BaseDefinition def) {
        return new StringAdder().pkg(MappingUtil.class).dot().add(toEnumClass(def)).toString();
    }

    private static void conditionalOnMissingBean(StringAdder adder, String thisBeanName) {
        if (DetectUtils.IMPORTED_MISSING_BEAN) {
            adder.add("@").simpleCls(ConditionalOnMissingBean.class);
            adder.add("(name=\"").add(thisBeanName).add("\")");
        }
        if (DetectUtils.IMPORTED_BEAN) {
            adder.add("@").simpleCls(Bean.class);
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
