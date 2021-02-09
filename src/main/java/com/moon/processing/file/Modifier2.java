package com.moon.processing.file;

import javax.lang.model.element.Modifier;
import java.util.*;

/**
 * @author benshaoye
 */
enum Modifier2 {
    ;

    public static final Set<Modifier> FOR_CLASS;
    public static final Set<Modifier> FOR_INTERFACE;
    public static final Set<Modifier> FOR_PARAMETER;
    public static final Set<Modifier> FOR_METHOD;
    public static final Set<Modifier> FOR_FIELD;

    private static final Map<Modifier, Set<Modifier>> MUTUALLY_EXCLUSIVE = new HashMap<>();

    static {
        EnumSet<Modifier> forField = EnumSet.of(Modifier.PUBLIC,
            Modifier.PROTECTED,
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL,
            Modifier.VOLATILE,
            Modifier.TRANSIENT);
        EnumSet<Modifier> forMethod = EnumSet.of(Modifier.PUBLIC,
            Modifier.PROTECTED,
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL,
            Modifier.SYNCHRONIZED);
        EnumSet<Modifier> forClass = EnumSet.of(Modifier.PUBLIC,
            Modifier.PROTECTED,
            Modifier.PUBLIC,
            Modifier.ABSTRACT,
            Modifier.FINAL);

        FOR_METHOD = Collections.unmodifiableSet(forMethod);
        FOR_FIELD = Collections.unmodifiableSet(forField);
        FOR_CLASS = Collections.unmodifiableSet(forClass);

        FOR_INTERFACE = Collections.unmodifiableSet(EnumSet.of(Modifier.PUBLIC));
        FOR_PARAMETER = Collections.unmodifiableSet(EnumSet.of(Modifier.FINAL));

        // 互斥修饰符，只列举了这里可能会用到的一些
        mutuallyExclusive(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE);
        mutuallyExclusive(Modifier.FINAL, Modifier.TRANSIENT, Modifier.ABSTRACT);
        mutuallyExclusive(Modifier.STATIC, Modifier.DEFAULT);
    }

    public static void useModifier(Set<Modifier> modifierSet, Modifier usingModifier) {
        Set<Modifier> exclusive = MUTUALLY_EXCLUSIVE.get(usingModifier);
        if (exclusive != null) {
            modifierSet.removeAll(exclusive);
        }
        modifierSet.add(usingModifier);
    }

    private static void mutuallyExclusive(Modifier modifier, Modifier... modifiers) {
        EnumSet<Modifier> modifierEnumSet = EnumSet.of(modifier, modifiers);
        for (Modifier oneModifier : modifierEnumSet) {
            MUTUALLY_EXCLUSIVE.put(oneModifier, modifierEnumSet);
        }
    }
}
