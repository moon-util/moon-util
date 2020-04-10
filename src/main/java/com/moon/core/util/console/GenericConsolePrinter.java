package com.moon.core.util.console;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import static com.moon.core.enums.Arrays2.getOrObjects;

/**
 * @author benshaoye
 */
public class GenericConsolePrinter extends BaseConsolePrinter {

    public GenericConsolePrinter() {
    }

    public GenericConsolePrinter(Class topClass) {
        super(topClass);
    }

    /**
     * 换行
     *
     * @param level level
     */
    @Override
    public void println(Level level) {
        executePrintln(getLevelStream(level));
    }

    /**
     * 输出指定级别信息
     *
     * @param level level
     * @param val   值
     */
    @Override
    public void println(Level level, Object val) {
        PrintStream stream = getLevelStream(level);
        if (stream != null) {
            if (val instanceof Collection) {
                this.executePrintlnOutput(stream, JoinerUtil.join((Collection) val));
            } else if (val == null) {
                this.executePrintlnOutput(stream, null);
            } else {
                Class clazz = val.getClass();
                if (clazz.isArray()) {
                    this.executePrintlnOutput(stream, getOrObjects(clazz).stringify(val));
                } else {
                    executePrintlnOutput(stream, val);
                }
            }
        }
    }

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param value    value
     */
    @Override
    public void println(Level level, String template, Object value) {
        PrintStream stream = getLevelStream(level);
        if (stream != null) {
            executePrintlnOutput(stream, StringUtil.format(template, value));
        }
    }

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     */
    @Override
    public void println(Level level, String template, Object value1, Object value2) {
        PrintStream stream = getLevelStream(level);
        if (stream != null) {
            executePrintlnOutput(stream, StringUtil.format(template, value1, value2));
        }
    }

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     * @param value3   the third value
     */
    @Override
    public void println(Level level, String template, Object value1, Object value2, Object value3) {
        PrintStream stream = getLevelStream(level);
        if (stream != null) {
            executePrintlnOutput(stream,
                StringUtil.format(template, value1, value2, value3));
        }
    }

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param values   values
     */
    @Override
    public void println(Level level, String template, Object... values) {
        PrintStream stream = getLevelStream(level);
        if (stream != null) {
            executePrintlnOutput(stream, StringUtil.format(template, values));
        }
    }

    /**
     * 输出指定级别信息
     *
     * @param level  level
     * @param values values
     */
    @Override
    public void println(Level level, Object... values) {
        PrintStream stream = getLevelStream(level);
        if (stream != null) {
            executePrintlnOutput(stream, Arrays.deepToString(values));
        }
    }

    private StringBuilder timerBuilder;
    private long previousTiming;
    private String template;
    private boolean contains = false;

    /**
     * 开始计时
     */
    @Override
    public void time() {
        this.time(null);
    }

    /**
     * 开始计时
     *
     * @param template
     */
    @Override
    public void time(String template) {
        timerBuilder = new StringBuilder();
        this.setTemplate(template);
        previousTiming = timing();
    }

    /**
     * 计次计时
     */
    @Override
    public void timeNext() {
        long current = timing();
        append(this.template, current);
        previousTiming = timing();
    }

    /**
     * 计次计时
     *
     * @param template
     */
    @Override
    public void timeNext(String template) {
        long current = timing();
        append(this.template, current);
        this.setTemplate(template);
        previousTiming = timing();
    }

    /**
     * 打印当前所经过的时间
     */
    @Override
    public void timeEnd() {
        long current = timing();
        append(this.template, current);
        StringBuilder timer = this.getTimerBuilder();
        timer.setCharAt(timer.length() - 1, (char) 0);
        this.debug(timer);
    }

    private long timing() {
        return System.currentTimeMillis();
    }

    private void setTemplate(String template) {
        this.template = template = (template == null ? "{} ms" : template);
        contains = template.contains("{}");
    }

    private void append(String old, long current) {
        long value = (current - previousTiming);
        StringBuilder timer = this.getTimerBuilder();
        if (contains) {
            timer.append(StringUtil.format(old, value));
        } else {
            timer.append(old).append(value).append(" ms");
        }
        timer.append('\n');
    }

    private StringBuilder getTimerBuilder() {
        StringBuilder timer = this.timerBuilder;
        if (timer == null) {
            timerBuilder = timer = new StringBuilder();
        }
        return timer;
    }
}
