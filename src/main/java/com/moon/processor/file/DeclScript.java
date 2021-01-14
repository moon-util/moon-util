package com.moon.processor.file;

import java.util.Collections;
import java.util.List;

/**
 * @author benshaoye
 */
public class DeclScript implements ScriptsProvider {

    public static final DeclScript EMPTY = new None();

    public DeclScript() {
    }

    @Override
    public List<String> getScripts() {
        return Collections.emptyList();
    }


    private final static class None extends DeclScript {

        @Override
        public List<String> getScripts() { return Collections.emptyList(); }
    }
}
