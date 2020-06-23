package com.moon.core.util.bug;

import com.moon.core.util.function.ThrowingRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public final class IssueUtil {

    /**
     * 可以提交 issue，提交后会得到一个编号，而当前版本肯定是没有修复这个{@code issue}的；
     * <p>
     * 所以当前版本会执行这段代码
     * <p>
     * <p>
     * 在后续哪个版本会修复也不一定，但每发布一个版本会加入已修复的{@code issue}编号，
     * <p>
     * 随着编号的加入，新的版本如果修复了这个 issue，那么这段代码在新的版本中便不再被执行
     *
     * @param issue  issue 编号，你可以先提交，把得到的编号写到这里
     * @param runner 待执行代码片段
     */
    public static void onMissing(int issue, ThrowingRunnable runner) {
        if (!ISSUE_SERIAL_NUMBER.contains(issue)) {
            runner.uncheckedRun();
        }
    }

    /**
     * 提供另一种方式，在提交 issue 的时候，可以自己设置一个 message，相当于一个“唯一键”，
     * <p>
     * 代表自己希望怎么修改这个 issue，如果修复的结果是按你希望的方式修改的，那么在发布的时候
     * <p>
     * 就会在 messages 里加入这个 message，否则即使修复了也不会加入；
     * <p>
     * 那么你就可以这样用：如果是按你期望的方式修复的，新版本发布后，这段代码就不再被执行；
     * 否则就会被执行
     *
     * @param message 消息内容
     * @param runner  执行代码片段
     */
    public static void onMissingExpectedMessage(String message, ThrowingRunnable runner) {
        if (!ISSUE_ASSIGN_MESSAGES.contains(message)) {
            runner.uncheckedRun();
        }
    }

    private final static Set<Integer> ISSUE_SERIAL_NUMBER;
    private final static Set<String> ISSUE_ASSIGN_MESSAGES;

    static {
        Set<Integer> numbers = new HashSet<>();
        Set<String> messages = new HashSet<>();
        ISSUE_SERIAL_NUMBER = Collections.unmodifiableSet(numbers);
        ISSUE_ASSIGN_MESSAGES = Collections.unmodifiableSet(messages);
    }
}
