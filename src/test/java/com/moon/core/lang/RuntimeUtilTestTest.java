package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class RuntimeUtilTestTest {

    @Test
    void testGetRuntime() throws Exception {
        System.out.println(RuntimeUtil.getCurrentPID());
        System.out.println(ThreadUtil.getCurrentThreadId());
    }

    @Test
    void testGetRuntime1() throws Exception {
        System.out.println(RuntimeUtil.getCurrentPID());
        System.out.println(ThreadUtil.getCurrentThreadId());

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        int slackSize = topGroup.activeCount() * 2;
        Thread[] slackThreads = new Thread[slackSize];
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        int actualSize = topGroup.enumerate(slackThreads);
        Thread[] atualThreads = new Thread[actualSize];
        // 复制slackThreads中有效的值到atualThreads
        System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
        System.out.println("Threads size is " + atualThreads.length);
        for (Thread thread : atualThreads) {
            System.out.println("Thread name : " + thread.getName());
        }
    }
}