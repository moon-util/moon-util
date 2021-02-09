package com.moon.processing.file;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaAddr {

    private final static Map<Integer, String> INDENT_MAP = new HashMap<>();

    private final List<Mark> marks = new ArrayList<>();
    private final StringBuilder content = new StringBuilder();
    private String indentSpaces;
    private int indentUnit;

    public JavaAddr() { this(0); }

    public JavaAddr(int indentUnit) { setIndentUnit(indentUnit); }

    private void setIndentUnit(int indentUnit) {
        this.indentUnit = indentUnit;
        String indentSpaces = INDENT_MAP.get(indentUnit);
        if (indentSpaces == null) {
            char[] chars = new char[indentUnit * 4];
            Arrays.fill(chars, ' ');
            indentSpaces = new String(chars);
            INDENT_MAP.put(indentUnit, indentSpaces);
        }
        this.indentSpaces = indentSpaces;
    }

    public JavaAddr next() {
        content.append('\n');
        return this;
    }

    public JavaAddr next(int line) {
        char[] chars = new char[line];
        Arrays.fill(chars, '\n');
        content.append(chars);
        return this;
    }

    public JavaAddr start() { return indentStart(1); }

    public JavaAddr add(Object value) {
        content.append(indentSpaces).append(value);
        return this;
    }

    public JavaAddr newAdd(Object value) { return next().add(value); }

    public JavaAddr blankAdd(Object value) { return next(2).add(value); }

    public JavaAddr script(CharSequence script) {
        return add(onlyColonTail(script.toString()));
    }

    public JavaAddr newScript(CharSequence script) {
        return next().script(script);
    }


    public JavaAddr blankScript(CharSequence script) {
        return next(2).script(script);
    }

    public JavaAddr newEnd() { return next().end(); }

    public JavaAddr end() { return indentStart(-1); }

    private JavaAddr indentStart(int count) {
        setIndentUnit(indentUnit + count);
        return this;
    }

    /**
     * 标记，可通过{@link Mark#with(CharSequence)}向标记处插入字符串等数据
     *
     * @return 标记
     */
    public Mark mark() { return new Mark(); }

    @Override
    public String toString() { return content.toString(); }

    public final class Mark {

        private final int index;
        private final int indentUnit;

        private int position = JavaAddr.this.content.length();

        private volatile boolean markReplaced = false;

        private Mark() {
            this.index = JavaAddr.this.marks.size();
            this.indentUnit = JavaAddr.this.indentUnit;
            JavaAddr.this.marks.add(this);
        }

        private boolean wasReplaced() {
            if (markReplaced) {
                return true;
            }
            synchronized (this) {
                if (markReplaced) {
                    return true;
                }
                this.markReplaced = true;
            }
            return false;
        }

        public void withProtected(Consumer<JavaAddr> addrConsumer) {
            if (wasReplaced()) {
                return;
            }
            final JavaAddr addr = new JavaAddr();
            final int unitState = addr.indentUnit;
            addr.setIndentUnit(this.indentUnit);
            addrConsumer.accept(addr);
            doReplace(addr.toString());
            addr.setIndentUnit(unitState);
        }

        public void with(CharSequence script) {
            if (wasReplaced()) {
                return;
            }
            doReplace(script);
        }

        private void doReplace(CharSequence script) {
            content.insert(position, script);
            List<Mark> marks = JavaAddr.this.marks;
            int appendedLength = script.length();
            int lastIdx = marks.size() - 1;
            for (int i = lastIdx; i > index; i--) {
                marks.get(i).position += appendedLength;
            }
        }
    }

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
