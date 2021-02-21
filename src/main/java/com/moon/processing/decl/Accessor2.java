package com.moon.processing.decl;

import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Imported;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author benshaoye
 */
enum Accessor2 {
    ;

    private static final Set<String> TYPES = new HashSet<>();

    private static void add(Class<?>... classes) {
        for (Class<?> klass : classes) {
            TYPES.add(klass.getCanonicalName());
        }
    }

    static {
        add(char.class, boolean.class, Character.class, Boolean.class);
        add(double.class, float.class, long.class, int.class, short.class, byte.class);
        add(Double.class, Float.class, Long.class, Integer.class, Short.class, Byte.class);
        add(String.class, Date.class, java.sql.Date.class, Time.class, Timestamp.class);
        add(LocalTime.class, LocalDate.class, LocalDateTime.class,OffsetDateTime.class);
        if (Imported.JODA_TIME) {
            // TODO
        }
    }

    private static final Map<Object, Boolean> PROPERTY_TYPES = new ConcurrentHashMap<>();

    private static boolean test(String classname) {
        if (TYPES.contains(classname)) {
            return true;
        }
        TypeElement elem = Processing2.getUtils().getTypeElement(classname);
        return elem.getKind() == ElementKind.ENUM;
    }

    public static boolean isPropertyType(String classname) {
        Boolean wasTested = PROPERTY_TYPES.get(classname);
        if (wasTested != null) {
            return wasTested;
        }
        boolean wasProperty = test(classname);
        PROPERTY_TYPES.put(classname, wasProperty);
        return wasProperty;
    }

    public static boolean isPropertyType(TypeElement element) {
        Boolean wasTested = PROPERTY_TYPES.get(element);
        if (wasTested != null) {
            return wasTested;
        }
        String classname = Element2.getQualifiedName(element);
        boolean wasProperty = isPropertyType(classname);
        PROPERTY_TYPES.put(element, wasProperty);
        return wasProperty;
    }
}
