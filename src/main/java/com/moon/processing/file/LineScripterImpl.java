package com.moon.processing.file;

/**
 * @author benshaoye
 */
public class LineScripterImpl implements LineScripter {

    private final String line;
    private boolean sorted;

    public LineScripterImpl(String line) {
        this.line = line;
        this.sorted = true;
    }

    @Override
    public String getLineScript() { return line; }

    @Override
    public int length() { return line == null ? 0 : line.length(); }

    @Override
    public void withUnsorted() { this.sorted = false; }

    @Override
    public boolean isSorted() { return sorted; }

    @Override
    public void appendTo(JavaAddr addr) { addr.newScript(line); }
}
