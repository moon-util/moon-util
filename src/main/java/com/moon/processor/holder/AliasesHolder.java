package com.moon.processor.holder;

import com.moon.accessor.meta.Table;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefEntityModel;
import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class AliasesHolder implements JavaFileWriteable {

    private final static String PKG = Table.class.getPackage().getName() + ".alias";
    private final Map<String, DeclJavaFile> javaMapped = new HashMap<>();

    public AliasesHolder() {}

    @SuppressWarnings("all")
    public void with(TypeElement element, DefEntityModel model) {
        final String aliasGroup = model.getAliasGroup();
        String aliasName = model.getAlias();
        if (String2.isBlank(aliasGroup) || String2.isBlank(aliasName)) {
            return;
        }
        DeclJavaFile java = javaMapped.get(aliasGroup);
        if (java == null) {
            java = DeclJavaFile.enumOf(PKG, aliasGroup);
            javaMapped.put(aliasGroup, java);
        }
        String tableType = model.getClassname();
        String tableField = model.getTableField();
        DeclField field = java.publicConstField(aliasName.trim(), tableType);
        field.valueOf("{}.{}", java.onImported(tableType), tableField);
    }

    private List<DeclJavaFile> getDeclJavaFiles() {
        return javaMapped.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        getDeclJavaFiles().forEach(java -> java.writeJavaFile(writer));
    }
}
