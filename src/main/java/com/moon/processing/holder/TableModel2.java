package com.moon.processing.holder;

import com.moon.accessor.annotation.TableModel;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

/**
 * @author benshaoye
 */
public enum TableModel2 {
    ;

    public static TableAlias parseAlias(TableModel model) {
        String alias = model.alias().trim();
        if (String2.isBlank(alias)) {
            return TableAlias.NULLS;
        }
        int idx = alias.indexOf('.');
        if (idx < 0) {
            String name = toQualifiedAlias(alias, alias);
            return new TableAlias("Aliases", name);
        } else {
            requiredAlias(alias.indexOf('.', idx + 1) > 0, alias);
            String namespace = toQualifiedAlias(alias.substring(0, idx), alias);
            String aliasName = toQualifiedAlias(alias.substring(idx + 1), alias);
            return new TableAlias(namespace, aliasName);
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
}
