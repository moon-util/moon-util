package com.moon.mapping.processing;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author moonsky
 */
final class NameGenerator {

    private final static String PREFIX = "map";

    private final AtomicInteger indexer = new AtomicInteger();

    private final Set<String> generated = new HashSet<>();

    private String thisSimpleName;

    public NameGenerator() { }

    public NameGenerator atFromClass(String thisClass) {
        this.thisSimpleName = ElementUtils.getSimpleName(thisClass);
        return this;
    }

    public String get(String targetClass) {
        String thatSimpleName = ElementUtils.getSimpleName(targetClass);
        final StringBuilder builder = defaultName(new StringBuilder(), thatSimpleName);
        String beanName = builder.toString();
        // 保证 bean name 的唯一性
        if (generated.contains(beanName)) {
            builder.setLength(0);
            defaultName(builder, thatSimpleName);
            beanName = builder.append(indexer.getAndIncrement()).toString();
        }
        generated.add(beanName);
        return beanName;
    }

    private StringBuilder defaultName(StringBuilder builder, String thatSimpleName) {
        builder.append(PREFIX).append(thisSimpleName).append("To").append(thatSimpleName);
        return builder;
    }
}
