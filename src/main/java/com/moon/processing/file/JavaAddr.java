package com.moon.processing.file;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaAddr {

    private final static char NEXT = '\n';
    private final static String NEXT_STR = "\n";
    private final static int TAB_SPACES = 4;
    private final static int MAX_LINE_LENGTH = 120;

    private final static Map<Integer, String> INDENT_MAP = new HashMap<>();

    private final List<Mark> marks = new ArrayList<>();
    private final StringBuilder content = new StringBuilder();
    private int lineNumber = 1;
    private String indentSpaces;
    private int indentUnit;

    public JavaAddr() { this(0); }

    public JavaAddr(int indentUnit) { setIndentUnit(indentUnit); }

    private void setIndentUnit(int indentUnit) {
        this.indentUnit = indentUnit;
        String indentSpaces = INDENT_MAP.get(indentUnit);
        if (indentSpaces == null) {
            char[] chars = new char[indentUnit * TAB_SPACES];
            Arrays.fill(chars, ' ');
            indentSpaces = new String(chars);
            INDENT_MAP.put(indentUnit, indentSpaces);
        }
        this.indentSpaces = indentSpaces;
    }

    public int lineNumber() { return lineNumber; }

    public JavaAddr next() {
        content.append(NEXT);
        lineNumber++;
        return this;
    }

    public JavaAddr next(int line) {
        char[] chars = new char[line];
        Arrays.fill(chars, NEXT);
        content.append(chars);
        lineNumber += line;
        return this;
    }

    public JavaAddr start() { return indentStart(1); }

    public JavaAddr add(Object value) {
        content.append(value);
        return this;
    }

    public JavaAddr padAdd(Object value) {
        content.append(indentSpaces);
        return add(value);
    }

    public JavaAddr addScript(CharSequence script) { return add(onlyColonTail(script.toString())); }

    public JavaAddr newAdd(Object value) { return next().padAdd(value); }

    public JavaAddr blankAdd(Object value) { return next(2).padAdd(value); }

    public JavaAddr padScript(CharSequence script) { return padAdd(onlyColonTail(script.toString())); }

    public JavaAddr newScript(CharSequence script) { return next().padScript(script); }

    public JavaAddr blankScript(CharSequence script) { return next(2).padScript(script); }

    public JavaAddr scriptEnd() {
        final StringBuilder builder = this.content;
        for (int i = builder.length() - 1; i > 0; i--) {
            char ch = builder.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch != ';') {
                builder.append(';');
            }
            break;
        }
        return this;
    }

    public JavaAddr newEnd() { return next().end(); }

    public JavaAddr newEnd(String close) { return next().end().padAdd(close); }

    public JavaAddr end() { return indentStart(-1); }

    public int lineLength() {
        int afterIndex = content.lastIndexOf(NEXT_STR) + 1;
        return afterIndex > 0 ? content.length() - afterIndex : content.length();
    }

    public boolean willOverLength(Object script) {
        String scriptStringify = String.valueOf(script);
        return lineLength() + scriptStringify.length() > MAX_LINE_LENGTH;
    }

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

    /**
     * 确保语句末尾有且只有一个分号
     *
     * @param script
     *
     * @return
     */
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
