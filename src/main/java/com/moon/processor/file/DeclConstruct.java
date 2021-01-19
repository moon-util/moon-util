package com.moon.processor.file;

import com.moon.processor.holder.ConstManager;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.moon.processor.file.Formatter2.onlyColonTail;
import static com.moon.processor.file.Formatter2.toParamsDeclared;

/**
 * @author benshaoye
 */
public class DeclConstruct implements ScriptsProvider {

    private final String name;
    private final ConstManager importer;
    private final Modifier accessLevel;
    private final DeclParams params;
    private final List<DeclMarked> annotations = new ArrayList<>();
    private final List<String> scripts = new ArrayList<>();

    DeclConstruct(ConstManager importer, Modifier accessLevel, String name, DeclParams params) {
        this.accessLevel = accessLevel;
        this.importer = importer;
        this.params = params;
        this.name = name;
    }

    public ConstManager getImporter() { return importer; }

    public DeclConstruct markedOf(Function<? super ConstManager, ? extends DeclMarked> consumer) {
        return markedOf(consumer.apply(getImporter()));
    }

    public DeclConstruct markedOf(DeclMarked annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public String getUniqueDeclaredKey() { return getUniqueDeclaredKey(true); }

    public String getUniqueDeclaredKey(boolean simplify) {
        return String.format("%s(%s)", name, toParamsDeclared(getImporter(), params, simplify));
    }

    public DeclConstruct scriptOf(String script, Object... values) {
        this.scripts.add(String2.format(script, values).trim());
        return this;
    }

    @Override
    public List<String> getScripts() {
        List<String> methodScripts = new ArrayList<>();
        annotations.forEach(annotation -> methodScripts.addAll(annotation.getScripts()));

        String declared = String2.format("{} {} {", accessLevel, getUniqueDeclaredKey(false));
        List<String> scripts = this.scripts;
        if (scripts.isEmpty()) {
            methodScripts.add(declared + " }");
            return methodScripts;
        }
        if (scripts.size() == 1) {
            methodScripts.add(declared + ' ' + onlyColonTail(scripts.get(0)) + " }");
            return methodScripts;
        }
        methodScripts.add(declared);
        scripts.forEach(it -> methodScripts.add("    " + onlyColonTail(it)));
        methodScripts.add("}");
        return methodScripts;
    }
}
