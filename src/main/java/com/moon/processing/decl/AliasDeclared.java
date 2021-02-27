package com.moon.processing.decl;

import com.moon.accessor.annotation.domain.TableModel;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

/**
 * @author benshaoye
 */
public class AliasDeclared {

    public final static AliasDeclared NULLS = new NullAlias();

    private final String group;
    private final String name;

    protected AliasDeclared(AliasDeclared alias) { this(alias.group, alias.name); }

    public AliasDeclared(String group, String name) {
        this.group = group;
        this.name = name;
    }

    public final String getAliasGroup() { return group; }

    public final String getAliasName() { return name; }

    public String toAliasRef(String name) {
        return group + '.' + this.name + '.' + name;
    }

    public static AliasDeclared parseAlias(TableModel model) {
        String alias = model.alias().trim();
        if (String2.isBlank(alias)) {
            return AliasDeclared.NULLS;
        }
        int idx = alias.indexOf('.');
        if (idx < 0) {
            String name = toQualifiedAlias(alias, alias);
            return new AliasDeclared("Aliases", name);
        } else {
            requiredAlias(alias.indexOf('.', idx + 1) > 0, alias);
            String namespace = toQualifiedAlias(alias.substring(0, idx), alias);
            String aliasName = toQualifiedAlias(alias.substring(idx + 1), alias);
            return new AliasDeclared(namespace, aliasName);
        }
    }

    private static String toQualifiedAlias(String name, String source) {
        String resultName = name.trim();
        requiredAlias(!Test2.isVar(resultName), source);
        return resultName.trim();
    }

    private static void requiredAlias(boolean tested, String alias) {
        if (tested) {
            String info = String2.format("错误表别名: {}", alias);
            throw new IllegalStateException(info);
        }
    }

    private final static class NullAlias extends AliasDeclared {

        public NullAlias() { super(null, null); }

        @Override
        public String toAliasRef(String name) { return null; }
    }
}
