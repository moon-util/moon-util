package com.moon.more.excel.table;

import com.moon.more.excel.PropertyControl;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author benshaoye
 */
final class ValueGetter implements PropertyControl {

    private final PropertyControl getter;

    protected ValueGetter(PropertyControl getter) { this.getter = getter; }

    static PropertyControl of(Method method, Field field) { return method == null ? of(field) : of(method); }

    private static PropertyControl of(Field field) { return newGetter(field); }

    private static PropertyControl of(Method method) { return newGetter(method); }

    @Override
    public final Object control(Object data) { return getter.control(data); }

    private static boolean isPublic(Member member) { return Modifier.isPublic(member.getModifiers()); }

    private static PropertyControl newGetter(Field field) {
        return isPublic(field) ? new PubFieldGetter(field) : new DftFieldGetter(field);
    }

    @SuppressWarnings("all")
    private final static class PubFieldGetter implements PropertyControl {

        private final Field field;

        PubFieldGetter(Field field) { this.field = field; }

        @Override
        public Object control(Object data) {
            Field field = this.field;
            try {
                return field.get(data);
            } catch (IllegalAccessException e) {
                try {
                    return field.get(data);
                } catch (RuntimeException | Error exeception) {
                    throw exeception;
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }

    @SuppressWarnings("all")
    private final static class DftFieldGetter implements PropertyControl {

        private final Field field;

        DftFieldGetter(Field field) { this.field = field; }

        @Override
        public Object control(Object data) {
            Field field = this.field;
            try {
                field.setAccessible(true);
                Object val = field.get(data);
                return val;
            } catch (RuntimeException | Error exeception) {
                throw exeception;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }

    private static PropertyControl newGetter(Method method) {
        return isPublic(method) ? new PubMethodGetter(method) : new DftMethodGetter(method);
    }

    private final static class PubMethodGetter implements PropertyControl {

        private final Method getter;

        PubMethodGetter(Method getter) {this.getter = getter;}

        @Override
        public Object control(Object data) {
            try {
                return getter.invoke(data);
            } catch (RuntimeException | Error exception) {
                throw exception;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private final static class DftMethodGetter implements PropertyControl {

        private final Method getter;

        DftMethodGetter(Method getter) {this.getter = getter;}

        @Override
        public Object control(Object data) {
            try {
                getter.setAccessible(true);
                Object val = getter.invoke(data);
                getter.setAccessible(false);
                return val;
            } catch (RuntimeException | Error exception) {
                throw exception;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}



