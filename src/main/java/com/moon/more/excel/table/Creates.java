package com.moon.more.excel.table;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author benshaoye
 */
final class Creates {

    static final Creator GETTER = new CreateGet();
    static final Creator SETTER = new CreateSet();

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * for getter
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class CreateGet implements Creator {

        @Override
        public Method getMethod(PropertyDescriptor descriptor) {
            return descriptor.getReadMethod();
        }

        @Override
        public Operation getOperation(Method method) {
            return null;
        }

        @Override
        public Operation getOperation(Field field) {
            return null;
        }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * for setter
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class CreateSet implements Creator {

        @Override
        public Method getMethod(PropertyDescriptor descriptor) {
            return descriptor.getWriteMethod();
        }

        @Override
        public Operation getOperation(Method method) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Operation getOperation(Field field) {
            throw new UnsupportedOperationException();
        }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * utils
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static boolean isPublic(Member member) { return Modifier.isPublic(member.getModifiers()); }
}
