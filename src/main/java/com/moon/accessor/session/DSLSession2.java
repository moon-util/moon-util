package com.moon.accessor.session;

import com.moon.accessor.config.Configuration;
import com.moon.accessor.config.ConnectionFactory;

import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
enum DSLSession2 {
    ;

    private final static DSLSessionBuilder BUILDER;

    static {
        ServiceLoader<DSLSessionBuilder> builders = ServiceLoader.load(DSLSessionBuilder.class);
        TreeMap<Integer, DSLSessionBuilder> builderMap = new TreeMap<>();
        Pattern pattern = Pattern.compile("\\d+");
        for (DSLSessionBuilder builder : builders) {
            String simpleName = builder.getClass().getSimpleName();
            Matcher matcher = pattern.matcher(simpleName);
            if (matcher.find()) {
                String matched = simpleName.substring(matcher.start(), matcher.end());
                try {
                    builderMap.put(Integer.parseInt(matched), builder);
                } catch (Throwable ignored) {}
            }
        }
        Entry<Integer, DSLSessionBuilder> lastEntry = builderMap.lastEntry();
        BUILDER = lastEntry == null ? DSLSessionImpl::new : lastEntry.getValue();
    }

    private static DSLSessionBuilder getBUILDER() {
        return BUILDER == null ? DSLSessionImpl::new : BUILDER;
    }

    public static <T extends DSLSession> T newDSLSession(Configuration configuration) {
        return (T) getBUILDER().build(configuration);
    }
}
