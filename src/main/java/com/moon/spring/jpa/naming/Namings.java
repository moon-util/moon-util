package com.moon.spring.jpa.naming;

import com.moon.core.lang.JoinerUtil;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitConstraintNameSource;
import org.hibernate.boot.spi.MetadataBuildingContext;

import java.util.List;

import static com.moon.core.util.ListUtil.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * @author moonsky
 */
class Namings {

    public static String toLowerUnderscore(String val) {
        return val == null ? null : val.replaceAll("([A-Z][\\w\\d])", "_$1").toLowerCase();
    }

    public static Identifier toIdentifier(String key, ImplicitConstraintNameSource source) {
        return toIdentifier(key,
            source.getUserProvidedIdentifier(),
            source.getTableName(),
            source.getColumnNames(),
            source.getBuildingContext());
    }

    public static Identifier toIdentifier(
        String key, Identifier user, Identifier table, List<Identifier> columns, MetadataBuildingContext context
    ) {
        if (user == null) {
            List<Identifier> identifiers = newArrayList(table);
            identifiers.addAll(columns);
            List<String> names = identifiers.stream().map(id -> id.toString()).collect(toList());
            return toIdentifier(key + "_" + JoinerUtil.join(names, "_"), context);
        }
        return user;
    }

    public static Identifier toIdentifier(String name, MetadataBuildingContext context) {
        return context.getMetadataCollector()
            .getDatabase()
            .getJdbcEnvironment()
            .getIdentifierHelper()
            .toIdentifier(name);
    }
}
