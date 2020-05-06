package com.moon.more.excel.parse;

import com.moon.more.excel.PropertyGetter;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author benshaoye
 */
abstract class ValueGetter implements PropertyGetter {

    private final PropertyGetter getter;

    protected ValueGetter(PropertyGetter getter) { this.getter = getter; }

    static PropertyGetter of(Method method, Field field) { return method == null ? of(field) : of(method); }

    private static PropertyGetter of(Field field) { return newGetter(field); }

    private static PropertyGetter of(Method method) { return newGetter(method); }

    @Override
    public final Object getValue(Object data) { return getter.getValue(data); }

    private static boolean isPublic(Member member) { return Modifier.isPublic(member.getModifiers()); }

    private static PropertyGetter newGetter(Field field) {
        return isPublic(field) ? new PubFieldGetter(field) : new DftFieldGetter(field);
    }

    @SuppressWarnings("all")
    private static class PubFieldGetter implements PropertyGetter {

        private final Field field;

        PubFieldGetter(Field field) { this.field = field; }

        @Override
        public Object getValue(Object data) {
            Field field = this.field;
            try {
                return field.get(field);
            } catch (IllegalAccessException e) {
                try {
                    return field.get(data);
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }

    @SuppressWarnings("all")
    private static class DftFieldGetter implements PropertyGetter {

        private final Field field;

        DftFieldGetter(Field field) { this.field = field; }

        @Override
        public Object getValue(Object data) {
            Field field = this.field;
            try {
                field.setAccessible(true);
                Object val = field.get(data);
                return val;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }

    private static PropertyGetter newGetter(Method method) {
        return isPublic(method) ? new PubMethodGetter(method) : new DftMethodGetter(method);
    }

    private static class PubMethodGetter implements PropertyGetter {

        private final Method getter;

        PubMethodGetter(Method getter) {this.getter = getter;}

        @Override
        public Object getValue(Object data) {
            try {
                return getter.invoke(data);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static class DftMethodGetter implements PropertyGetter {

        private final Method getter;

        DftMethodGetter(Method getter) {this.getter = getter;}

        @Override
        public Object getValue(Object data) {
            try {
                getter.setAccessible(true);
                Object val = getter.invoke(data);
                getter.setAccessible(false);
                return val;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}



