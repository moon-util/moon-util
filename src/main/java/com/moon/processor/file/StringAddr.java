package com.moon.processor.file;

import com.moon.processor.utils.Const2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author benshaoye
 */
public class StringAddr {

    private final List<Mark> marks = new ArrayList<>();
    private final StringBuilder builder;

    public StringAddr() { this.builder = new StringBuilder(); }

    public static StringAddr of() { return new StringAddr(); }

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

    public StringAddr addPackage(String packageName) { return addScript("package " + packageName); }

    public StringAddr addScript(CharSequence script) { return add(onlyColonTail(script.toString().trim())); }

    public StringAddr addBlockComment(int indent, String comment, String... comments) {
        next().indent(indent).add("/*");
        next().indent(indent).add(" * ").add(comment);
        String[] strings = comments == null ? Const2.EMPTY : comments;
        for (String string : strings) {
            next().indent(indent).add(" * ").add(string);
        }
        return next().indent(indent).add(" */").next();
    }

    public StringAddr addDocComment(int indent, String comment, String... comments) {
        next().indent(indent).add("/** ");
        if (comments == null || comments.length == 0) {
            add(comment);
        } else {
            for (String string : comments) {
                next().indent(indent).add(" * ").add(string);
            }
        }
        return next().indent(indent).add(" */").next();
    }

    public StringAddr append(Object sequence) { return add(sequence); }

    private StringAddr append(char ch, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, ch);
        return add(chars);
    }

    public StringAddr next() { return add('\n'); }

    public StringAddr next(int line) { return append('\n', line); }

    public StringAddr indent(int count) { return append(' ', count); }

    public StringAddr indent(int count, int multi) { return append(' ', count * multi); }

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

    private static String onlyColonTail(String script) {
        final int initLastIndex = script.length() - 1;
        int lastIndex = initLastIndex;
        boolean present = false;
        for (int i = initLastIndex; i >= 0; i--) {
            char ch = script.charAt(i);
            if (ch == ';') {
                if (present) {
                    lastIndex--;
                }
                present = true;
            } else if (Character.isWhitespace(ch)) {
                if (present) {
                    break;
                }
            } else {
                break;
            }
        }
        if (present) {
            return script.substring(0, lastIndex + 1);
        } else {
            return script + ';';
        }
    }
}
