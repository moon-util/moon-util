package com.moon.processor.file;

import com.moon.processor.utils.Const2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.moon.processor.file.Formatter2.onlyColonTail;

/**
 * @author benshaoye
 */
public class StringAddr {

    private final List<Mark> marks = new ArrayList<>();
    private final StringBuilder builder;
    private final int indent;

    public StringAddr(int indent) {
        this.builder = new StringBuilder();
        this.indent = indent;
    }

    public static StringAddr of() { return new StringAddr(4); }

    public boolean isEmpty() { return builder.length() < 1; }

    private StringAddr add(char[] scripts) {
        builder.append(scripts);
        return this;
    }

    public StringAddr add(char script) {
        builder.append(script);
        return this;
    }

    public StringAddr add(Object script) {
        builder.append(script);
        return this;
    }

    public StringAddr addAll(int tabUnit, Object... scripts) {
        for (Object script : scripts) {
            next().tab(tabUnit).add(script);
        }
        return this;
    }

    public StringAddr addAll(int tabUnit, Iterable<?> scripts) {
        for (Object script : scripts) {
            next().tab(tabUnit).add(script);
        }
        return this;
    }

    public StringAddr addPackage(String packageName) { return addScript("package " + packageName); }

    public StringAddr addScript(CharSequence script) { return add(onlyColonTail(script.toString().trim())); }

    public StringAddr addBlockComment(int tabUnit, String comment, String... comments) {
        next().tab(tabUnit).add("/*");
        next().tab(tabUnit).add(" * ").add(comment);
        String[] strings = comments == null ? Const2.EMPTY : comments;
        for (String string : strings) {
            next().tab(tabUnit).add(" * ").add(string);
        }
        return next().tab(tabUnit).add(" */").next();
    }

    public StringAddr addDocComment(int tabUnit, String comment, String... comments) {
        next().tab(tabUnit).add("/**");
        if (comments == null || comments.length == 0) {
            return add(' ').add(comment).add(" */");
        } else {
            next().tab(tabUnit).add(" * ").add(comment);
            for (String string : comments) {
                next().tab(tabUnit).add(" * ").add(string);
            }
            return next().tab(tabUnit).add(" */");
        }
    }

    public StringAddr append(Object sequence) { return add(sequence); }

    private StringAddr append(char ch, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, ch);
        return add(chars);
    }

    /*
    换行和缩进
     */

    public StringAddr next() { return add('\n'); }

    public StringAddr next(int line) { return append('\n', line); }

    public StringAddr tab() { return indent(indent); }

    public StringAddr tab(int tabUnit) { return indent(indent * tabUnit); }

    private StringAddr indent(int indent) { return append(' ', indent); }

    public StringAddr newTab() { return next().tab(); }

    public StringAddr new2Tab() { return newTab(2, 1); }

    public StringAddr newTab2(String script) { return newTab(1, 2).addScript(script); }

    public StringAddr newTab(int line, int tabUnit) { return next(line).tab(tabUnit); }

    public StringAddr newTab(String... scripts) {
        for (String script : scripts) {
            newTab().add(script);
        }
        return this;
    }

    public StringAddr newTab(int line, Iterable<String> scripts) {
        for (String script : scripts) {
            newTab(line, 1).add(script);
        }
        return this;
    }

    public StringAddr newTab(Iterable<String> scripts) {
        for (String script : scripts) {
            newTab().add(script);
        }
        return this;
    }

    /**
     * 标记，可通过{@link Mark#with(StringAddr)}向标记处插入字符串等数据
     *
     * @return 标记
     */
    public Mark mark() { return new Mark(); }

    public final class Mark {

        private final int index;

        private int position = StringAddr.this.builder.length();

        private boolean markReplaced = false;

        private Mark() {
            this.index = StringAddr.this.marks.size();
            StringAddr.this.marks.add(this);
        }

        public void with(StringAddr addr) { with(addr.toString()); }

        public void with(CharSequence script) {
            if (markReplaced) {
                return;
            }
            builder.insert(position, script);
            List<Mark> marks = StringAddr.this.marks;
            int appendedLength = script.length();
            int lastIdx = marks.size() - 1;
            for (int i = lastIdx; i > index; i--) {
                marks.get(i).position += appendedLength;
            }
            this.markReplaced = true;
        }
    }

    @Override
    public String toString() { return builder.toString(); }
}
