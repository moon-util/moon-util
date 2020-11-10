package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        int index = 0;
        String configName = "MappingConfiguration";
        StringAdder adder = new StringAdder().add("package ").pkg(MappingUtil.class).add(";");
        if (DetectUtils.IMPORTED_CONFIGURATION) {
            adder.add("@").cls(Configuration.class);
        }
        if (DetectUtils.IMPORTED_MISSING_BEAN) {
            adder.add("@").cls(ConditionalOnMissingBean.class).add("(").add(configName).add(".class)");
        }
        adder.add(" public class ").add(configName).add(" {");
        for (MappingWriter writer : writers) {
            String thisCls = writer.getThisDefined().getQualifiedName();
            for (String thatCls : writer.getForAllDefined().keySet()) {
                adder.add("@org.springframework.context.annotation.Bean public ");
                adder.cls(BeanMapping.class).add("<").add(thisCls).add(',');
                adder.add(thatCls).add("> moonBeanMapping").add(index++).add("() {");
                adder.add("return ").add(toEnumClass(thisCls)).dot().add("TO_");
                adder.add(thatCls.replace('.', '_'));
                adder.add(";}");
            }
        }
        adder.add("}");
        StringAdder src = new StringAdder().pkg(MappingUtil.class).dot().add(configName);
        EnvUtils.newJavaFile(filer,src.toString(), adder);
    }

    private StringAdder impl(final String classname) {
        final BaseDefinition def = getThisDefined();
        StringAdder adder = new StringAdder().add("package ").pkg(MappingUtil.class).add(';');
        adder.add("enum ").add(getSimpleName(classname)).impl(BeanMapping.class).add("{TO,");
        for (BaseDefinition value : getForAllDefined().values()) {
            def.addBeanMapping(adder, value);
        }
        return adder.add(';').add(def.implMappingSharedMethods()).add("}");
    }

    @SuppressWarnings("all")
    private static String toSourceName(final BaseDefinition def) {
        return new StringAdder().pkg(MappingUtil.class).dot().add(toEnumClass(def)).toString();
    }

    private static String toEnumClass(final BaseDefinition def) {
        return StringUtils.toMappingClassname(def.getQualifiedName());
    }

    private static String toEnumClass(final String classname) {
        return StringUtils.toMappingClassname(classname);
    }
}
