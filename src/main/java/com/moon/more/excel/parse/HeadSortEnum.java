package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.Priority;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
enum HeadSortEnum implements Comparator<HeadSortable> {
    /**
     * 组内优先按{@link TableColumn#order()}排序，同序号按名称自然排序
     * <p>
     * 组间按第一列（最小序号列）排序
     * <p>
     * 同行同名相邻列合并
     */
    GROUP(Priority.GROUP) {
        @Override
        public int compare(HeadSortable o1, HeadSortable o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                int compare = o1.getOrder() - o2.getOrder();
                return compare == 0 ? o1.getName().compareTo(o2.getName()) : compare;
            }
        }
    },
    /**
     * 先按序号排序，然后按名称自然排序，同行同名相邻列合并
     */
    ORDER(Priority.ORDER) {
        @Override
        public int compare(HeadSortable o1, HeadSortable o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                int compare = o1.getOrder() - o2.getOrder();
                if (compare == 0) {
                    Property d1 = (Property) o1;
                    Property d2 = (Property) o2;
                    String[] ls1 = d1.getHeadLabels();
                    String[] ls2 = d2.getHeadLabels();
                    int len1 = ls1.length;
                    int len2 = ls2.length;
                    int res = len1 - len2;
                    // 标题“短”的在前面
                    if (res != 0) {
                        return compare;
                    }
                    // 标题一样长按标题自然排序
                    int len = Math.min(len1, len2);
                    for (int i = 0; i < len; i++) {
                        String label1 = ls1[i];
                        String label2 = ls2[i];
                        if ((res = label1.compareTo(label2)) != 0) {
                            return res;
                        }
                    }
                    // 最后按字段名自然排序，字段名因 java 语言限制肯定唯一
                    return o1.getName().compareTo(o2.getName());
                }
            }
            return 0;
        }
    };

    private final Priority priority;

    private static class Cached {

        final static Map<Priority, HeadSortEnum> CACHE = new HashMap<>();
    }

    HeadSortEnum(Priority priority) {
        this.priority = priority;
        Cached.CACHE.put(priority, this);
    }

    static HeadSortEnum getSortStrategy(Priority priority) { return Cached.CACHE.getOrDefault(priority, GROUP); }
}
