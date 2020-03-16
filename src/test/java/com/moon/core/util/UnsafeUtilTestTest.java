package com.moon.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class UnsafeUtilTestTest {

    @Test
    void testGetUnsafe() {
        Assertions.assertTrue(UnsafeUtil.getUnsafe().isPresent());
        UnsafeUtil.getUnsafe().ifPresent(unsafe -> {
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