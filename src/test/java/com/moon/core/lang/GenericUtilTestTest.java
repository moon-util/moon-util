package com.moon.core.lang;

import com.moon.core.util.StringKeyHashMap;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author benshaoye
 */
class GenericUtilTestTest {

    private static class ThisArrayList extends ArrayList<String> implements List<String>, Collection<String> {}

    @Test
    void testGetGenericType() throws Exception {
        Class listClass = ThisArrayList.class;

        Type type = listClass.getGenericSuperclass();

        System.out.println(type);



        System.out.println(Arrays.toString(listClass.getGenericInterfaces()));


        System.out.println(Arrays.toString(ArrayList.class.getTypeParameters()));
    }
    @Test
    void testGetGenericType1() throws Exception {
        Class listClass = StringKeyHashMap.class;

        Type type = listClass.getGenericSuperclass();

        System.out.println(type);
        if (type instanceof ParameterizedType) {
            System.out.println("================================================");
            ParameterizedType parameterType = (ParameterizedType) type;
            System.out.println(parameterType.getTypeName());
            System.out.println(parameterType.getRawType());
            System.out.println(parameterType.getOwnerType());
            Type[] types = parameterType.getActualTypeArguments();
            System.out.println(Arrays.toString(types));


            System.out.println("------------------------------------------------");
        }


        System.out.println(Arrays.toString(listClass.getGenericInterfaces()));


        System.out.println(Arrays.toString(ArrayList.class.getTypeParameters()));
    }
}