package com.moon.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sun.instrument.InstrumentationImpl;
import sun.misc.Unsafe;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class UnsafeUtilTestTest {

    @Test
    void testAtomicIntegerArray() {
        AtomicIntegerArray array = new AtomicIntegerArray(5);
    }

    static class TestInstance {

        TestInstance() {
            System.out.println("====");
        }

        void doSomething() {
            System.out.println("------------------");
        }
    }

    @Test
    void testNewInstance() {
        TestInstance instance0 = new TestInstance();
        instance0.doSomething();

        Optional<Unsafe> optional = UnsafeUtil.get();
        optional.ifPresent(unsafe -> {
            try {
                TestInstance instance1 = (TestInstance) unsafe.allocateInstance(TestInstance.class);
                instance1.doSomething();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });

        optional.ifPresent(unsafe -> {
            try {
                UnsafeUtil util0 = (UnsafeUtil) unsafe.allocateInstance(UnsafeUtil.class);
                System.out.println(util0);
                UnsafeUtil util1 = (UnsafeUtil) unsafe.allocateInstance(UnsafeUtil.class);
                System.out.println(util1);
                assertNotSame(util0,util1);
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testGetUnsafe() {
        Assertions.assertTrue(UnsafeUtil.get().isPresent());
        UnsafeUtil.get().ifPresent(unsafe -> {
            if (false) {
                // 释放内存
                unsafe.freeMemory(1000);
                // 分配内存
                unsafe.allocateMemory(1000);
                // 重新分配内存，扩充内存
                unsafe.reallocateMemory(1000, 1000);
                // 给指定内存设置值
                unsafe.setMemory(null, 0, 0, Byte.valueOf("0"));
            }
        });
    }
}