package com.moon.core.enums;

import java.util.Comparator;

/**
 * @author benshaoye
 */
public enum ArraySortsEnum
    implements Sortable {

    /**
     * 基数排序
     */
    Radix {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 桶排序
     */
    Bucket {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 计数排序
     */
    Counting {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 堆排序
     */
    Heap {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 快速排序
     */
    Quick {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 归并排序
     */
    Merge {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 希尔排序
     */
    Shell {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 插入排序
     */
    Insertion {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 选择排序
     */
    Selection {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    },
    /**
     * 冒泡排序
     */
    Bubble {
        @Override
        public Object[] sort(Object[] objects, Comparator comparator) {
            return objects;
        }
    }
}
