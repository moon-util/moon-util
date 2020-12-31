package com.moon.processor.model;

import com.moon.processor.manager.ConstManager;

/**
 * @author benshaoye
 */
public class DefMapping {

    private final static String[] EMPTY = {};

    private final ConstManager constManager;
    private final DeclareProperty fromProp;
    private final DeclareProperty toProp;
    private final DeclareMapping mapping;
    private final String fromName;
    private final String toName;

    private DefMapping(
        DeclareProperty fromProp,
        DeclareProperty toProp,
        DeclareMapping mapping,
        String fromName,
        String toName,
        ConstManager constManager
    ) {
        this.constManager = constManager;
        this.fromProp = fromProp;
        this.toProp = toProp;
        this.mapping = mapping;
        this.fromName = fromName;
        this.toName = toName;
    }

    public static DefMapping forward(
        DeclareProperty thisProp,
        DeclareProperty thatProp,
        DeclareMapping mapping,
        ConstManager constManager
    ) {
        return new DefMapping(thisProp, thatProp, mapping, "self", "that",constManager);
    }

    public static DefMapping backward(
        DeclareProperty thisProp,
        DeclareProperty thatProp,
        DeclareMapping mapping,
        ConstManager constManager
    ) {
        return new DefMapping(thatProp, thisProp, mapping, "that", "self",constManager);
    }

    public static DefMapping returning(String script) { return new Returning(script); }

    public DeclareProperty getFromProp() { return fromProp; }

    public DeclareProperty getToProp() { return toProp; }

    public DeclareMapping getMapping() { return mapping; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    public String[] getScripts() {

        return EMPTY;
    }

    private static final class Returning extends DefMapping {

        private final String[] scripts;

        public Returning(String script) {
            super(null, null, null, null, null, null);
            String[] scripts = {"return " + script + ";"};
            this.scripts = scripts;
        }

        @Override
        public String[] getScripts() { return scripts; }
    }
}
