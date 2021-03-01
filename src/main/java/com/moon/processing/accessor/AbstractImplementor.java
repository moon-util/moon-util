package com.moon.processing.accessor;

import com.moon.processing.holder.BaseHolder;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author benshaoye
 */
public abstract class AbstractImplementor extends BaseHolder {

    private final static Map<String, String> COLLECTION_TYPES = new HashMap<>();

    static {
        BiConsumer<Class<?>, Class<?>> consumer = (cls1, cls2) -> COLLECTION_TYPES.put(cls1.getCanonicalName(),
            cls2.getCanonicalName());
        consumer.accept(Map.class, HashMap.class);
        consumer.accept(HashMap.class, HashMap.class);
        consumer.accept(TreeMap.class, TreeMap.class);
        consumer.accept(LinkedHashMap.class, LinkedHashMap.class);

        consumer.accept(Set.class, HashSet.class);
        consumer.accept(HashMap.class, HashMap.class);
        consumer.accept(TreeSet.class, TreeSet.class);
        consumer.accept(LinkedHashSet.class, LinkedHashSet.class);

        consumer.accept(List.class, ArrayList.class);
        consumer.accept(ArrayList.class, ArrayList.class);
        consumer.accept(LinkedList.class, LinkedList.class);

        consumer.accept(Collection.class, ArrayList.class);
        consumer.accept(Iterable.class, ArrayList.class);
        consumer.accept(Queue.class, LinkedList.class);
        consumer.accept(Deque.class, LinkedList.class);
    }

    private final static Map<Integer, String> PLACEHOLDERS_MAP = new HashMap<>();

    protected AbstractImplementor(Holders holders) { super(holders); }

    protected static String nullableCollectActualType(String collectionType) {
        return COLLECTION_TYPES.get(collectionType);
    }

    protected static String defaultReturningVal(String actualReturnType) {
        if (Test2.isPrimitiveNumber(actualReturnType)) {
            return "0";
        } else if (Test2.isPrimitiveBool(actualReturnType)) {
            return "false";
        } else if (Test2.isPrimitiveChar(actualReturnType)) {
            return "' '";
        }
        return null;
    }

    protected final String toPlaceholders(int count) {
        String placeholder = PLACEHOLDERS_MAP.get(count);
        if (placeholder == null) {
            String[] holders = new String[count];
            Arrays.fill(holders, "?");
            placeholder = String.join(", ", holders);
            PLACEHOLDERS_MAP.put(count, placeholder);
        }
        return placeholder;
    }

    protected final boolean isSamePropertyType(String actualType, String fieldClass) {
        String generalizableType = String2.toGeneralizableType(actualType);
        if (Objects.equals(generalizableType, fieldClass)) {
            return true;
        }
        return Test2.isSubtypeOf(generalizableType, fieldClass);
    }
}
