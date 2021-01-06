package com.moon.mapper.processing;

import com.moon.mapper.BeanCopier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.*;

import static com.moon.mapper.processing.ElemUtils.getSimpleName;
import static com.moon.mapper.processing.StringUtils.toMappingClassname;

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
        final String classname = toSourceName(getThisDefined());
        EnvUtils.newJavaFile(filer, classname, impl(classname));
    }

    public static void forAutoConfig(Filer filer, List<MappingWriter> writers) throws IOException {
        if (!Imported.CONFIGURATION) {
            return;
        }
        final Manager manager = getAllImportsNameForConfiguration();
        final Set<String> classes = new TreeSet<>();
        // beans
        final StringAdder beansAddr = getBeansDefinition(writers, manager, classes);
        if (classes.isEmpty()) {
            return;
        }

        // 类名: BeanMappingConfigurationXXXXAtXXXX
        final String configName = getBeanMappingConfigName(classes);

        // 类内容: public class BeanMappingConfigurationXXXXAtXXXX { beans }
        final StringAdder configurationAdder = new StringAdder();
        annotationGenerated(configurationAdder, manager);
        configurationAdder.add("@").add(manager.onImported(Configuration.class));
        configurationAdder.add("@SuppressWarnings({\"all\",\"unchecked\"})");
        configurationAdder.add(" public class ").add(configName).add(" {");
        configurationAdder.add(beansAddr).add("}");

        // 类文件: 包名 + import + 类内容
        StringAdder configAdder = new StringAdder().add("package ").pkg(BeanCopier.class).add(";");
        configAdder.add(manager.toStringForImports()).add(configurationAdder);
        StringAdder src = new StringAdder().pkg(BeanCopier.class).dot().add(configName);

        // 生成文件
        EnvUtils.newJavaFile(filer, src.toString(), configAdder);
    }

    private static String getBeanMappingConfigName(Collection<String> fullClasses) {
        // 所包含映射器的 hash 码
        // int hashCode = Arrays.hashCode(fullClasses.toArray(new String[0]));
        // 通过这样计算保证新编译的代码始终在旧编译代码前面
        long timestamp = Long.MAX_VALUE - System.currentTimeMillis();
        String hash = Long.toString(timestamp, 36);
        return "BeanMappingConfiguration" + hash;
    }

    private static StringAdder getBeansDefinition(
        List<MappingWriter> writers, Manager manager, Collection<String> fullClasses
    ) {
        final StringAdder beansAdder = new StringAdder();
        final NameGenerator generator = new NameGenerator();
        for (MappingWriter writer : writers) {
            final String thisCls = writer.getThisDefined().getCanonicalName();
            final String thisSimpleName = manager.onImported(thisCls);
            fullClasses.add(thisCls);
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
                beansAdder.add(" public ").add(manager.onImported(BeanCopier.class));
                beansAdder.add("<").add(thisSimpleName).add(',').add(thatSimpleName).add("> ");
                beansAdder.add(thisBeanName).add("() {");
                beansAdder.add("return ").add(toEnumClass(thisCls)).dot();
                beansAdder.add("TO_").add(thatCls.replace('.', '_')).add(";}");
            }
        }
        return beansAdder;
    }

    private static Manager getAllImportsNameForConfiguration() {
        Manager manager = new Manager();
        if (Imported.CONDITIONAL_ON_MISSING_BEAN) { manager.onImported(ConditionalOnMissingBean.class); }
        if (Imported.CONFIGURATION) { manager.onImported(Configuration.class); }
        if (Imported.BEAN) { manager.onImported(Bean.class); }
        manager.onImported(BeanCopier.class);
        return manager;
    }

    private static void conditionalOnMissingBean(StringAdder adder, String thisBeanName, final Manager manager) {
        if (Imported.BEAN) {
            adder.add("@").add(manager.onImported(Bean.class));
        }
        if (Imported.CONDITIONAL_ON_MISSING_BEAN) {
            adder.add("@").add(manager.onImported(ConditionalOnMissingBean.class));
            adder.add("(name=\"").add(thisBeanName).add("\")");
        }
        adder.space();
    }

    private StringAdder impl(final String classname) {
        final BaseDefinition def = getThisDefined();
        final Manager manager = new Manager();
        StringAdder enumAdder = new StringAdder();
        for (BaseDefinition value : getForAllDefined().values()) {
            def.addBeanMapping(enumAdder, value, manager);
        }
        // 暂时不实现 MapMapping 和 ObjectMapping 了
        // StringAdder shardAdder = def.implMappingSharedMethods(manager);
        StringAdder adder = new StringAdder();
        adder.add("package ").pkg(BeanCopier.class).add(';').add(manager.toStringForImports());
        annotationGenerated(adder, manager);
        adder.add("@SuppressWarnings({\"all\",\"unchecked\"}) enum ").add(getSimpleName(classname));
        adder.impl(BeanCopier.class).add("{TO,").add(enumAdder).add(';').add(manager.toStringForStaticVars());
        return adder.add("}");
    }

    private static void annotationGenerated(StringAdder adder, Manager manager){
        adder.add("@")
            .add(manager.onImported(Generated.class))
            .add("(value = \"")
            .add(MappingProcessor.class.getCanonicalName())
            .add("\", date = \"")
            .add(new Date())
            .add("\")");
    }

    private String toSourceName(final BaseDefinition def) {
        return new StringAdder().pkg(BeanCopier.class).dot().add(toEnumClass(def)).toString();
    }

    private static String toEnumClass(final BaseDefinition def) { return toEnumClass(def.getQualifiedName()); }

    private static String toEnumClass(final String classname) { return toMappingClassname(classname); }
}
