package com.moon.data.identifier;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import com.moon.data.IdentifierGenerator;
import com.moon.data.Record;
import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.factory.AbstractRepositoryImpl;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public class IdentifierUtil {

    private final static String packageName = IdentifierUtil.class.getPackage().getName();
    private final static Set<Class> USED_IDENTIFIER_TYPES = new HashSet<>();

    protected IdentifierUtil() { }

    private static void assertNot(String classname, Class<?> type) {
        if (type.getName().equals(classname)) {
            throw new IllegalStateException("不允许：" + classname);
        }
    }

    private static String assertClassname(String classname) {
        assertNot(classname, IdentifierUtil.class);
        return classname;
    }

    private static Class toClassOrNull(String classname) {
        return ClassUtil.forNameOrNull(assertClassname(classname));
    }

    private static Class toIdentifierClass(String classname) {
        // 类全名
        Class type = toClassOrNull(classname);
        if (type == null) {
            // 类简写
            classname = packageName + "." + classname;
            type = toClassOrNull(classname);
        }
        if (type == null) {
            // 省略后缀的简写
            classname = classname + "Identifier";
            type = toClassOrNull(classname);
        }
        return Objects.requireNonNull(type);
    }

    /*
     * **********************************************************************************************
     * * default methods ****************************************************************************
     * **********************************************************************************************
     */
    /**
     * 这是给缓存主键用的，KEY 是将要保存对象的内存地址，VALUE 是对象预设的 ID 值
     * <p>
     * {@link AbstractRepositoryImpl#persist(JpaRecord)}这个方法是给手动设置主键用的
     * <p>
     * 手动设置的主键值会先缓存在这里，并设为 null，后续通过 IdentifierGenerator 获取主键时，
     * <p>
     * 通过{@link #returnIfRecordIdNotEmpty(Object, Object, BiFunction)}优先获取预设的主键
     * <p>
     * <p>
     * 一般预设主键都是极少数情况，也不建议这样做，通常也只是在启动时添加一些基础数据等，此种情况系统基本处于稳定状态
     * <p>
     * 整个系统运行期间也不会很多，故这里未考虑超大数据量、并发以及异常（异常会产生少量垃圾数据堆积）等情况，如确有必要，请自行处理
     */
    private final static Map<Integer, Object> temporaryIdMap = new ConcurrentHashMap<>();

    protected final static <T extends Record<?>> T putRecordPresetPrimaryKey(T record) {
        if (record != null) {
            Object id = record.getId();
            if (id instanceof CharSequence) {
                if (!id.toString().isEmpty()) {
                    temporaryIdMap.put(System.identityHashCode(record), id);
                    record.setId(null);
                }
            } else if (id != null) {
                temporaryIdMap.put(System.identityHashCode(record), id);
                record.setId(null);
            }
        }
        return record;
    }

    private final static Object obtainRecordId(Object entity) {
        return temporaryIdMap.remove(System.identityHashCode(entity));
    }

    /**
     * 当 Record 字符串形式主键不为空（null或 ""）时返回已存在的主键，否则按主键策略生成主键
     *
     * @param entity
     * @param o
     * @param generator
     *
     * @return
     */
    protected final static <ID, METADATA> ID returnIfRecordIdNotEmpty(
        Object entity, METADATA o, BiFunction<Object, METADATA, ID> generator
    ) {
        Object id = obtainRecordId(entity);
        if (id instanceof CharSequence) {
            return ((CharSequence) id).length() > 0 ? (ID) id : generator.apply(entity, o);
        }
        return id == null ? generator.apply(entity, o) : (ID) id;
    }

    /**
     * 添加已使用的{@code id}类型
     *
     * @param idType {@code id}类型
     */
    protected static void addUsedIdentifierType(Class idType) { USED_IDENTIFIER_TYPES.add(idType); }

    /*
     * **********************************************************************************************
     * * public methods *****************************************************************************
     * **********************************************************************************************
     */

    /**
     * 创建 id 生成器
     *
     * @param description 描述
     * @param key         spring properties key
     *
     * @return ID 生成器
     */
    public static IdentifierGenerator<? extends Serializable, Object> newIdentifierGenerator(
        String description, String key
    ) {
        // 默认雪花算法
        if (StringUtil.isBlank(description)) {
            Set<Class> types = USED_IDENTIFIER_TYPES;
            if (types.size() == 1) {
                Class type = SetUtil.requireGet(types, 0);
                if (type == Long.class) {
                    return new LongSequenceIdentifier();
                }
                if (type == String.class) {
                    return new StringSequenceIdentifier();
                }
            }
            throw new IllegalStateException("Unknown identifier type, you must assign spring property of: " + key);
        }
        // 自定义主键策略
        String[] descriptions = description.split(":");
        Class type = toIdentifierClass(descriptions[0]);
        final int length = descriptions.length;
        if (length == 1) {
            return (IdentifierGenerator) ConstructorUtil.newInstance(type);
        }
        final int argsLen = length - 1;
        TypeCaster caster = TypeUtil.cast();
        for (Constructor constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == argsLen) {
                try {
                    Object[] args = new Object[argsLen];
                    Class[] types = constructor.getParameterTypes();
                    for (int i = 0; i < argsLen; i++) {
                        Class argType = types[i];
                        String param = descriptions[i + 1];
                        args[i] = caster.toType(param, argType);
                    }
                    return (IdentifierGenerator) ConstructorUtil.newInstance(type, args);
                } catch (Throwable ignored) {
                }
            }
        }
        throw new IllegalStateException(description);
    }
}
