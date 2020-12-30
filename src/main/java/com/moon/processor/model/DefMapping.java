package com.moon.processor.model;

/**
 * @author benshaoye
 */
public class DefMapping {

    private final static String[] EMPTY = {};

    private final DeclareProperty fromProp;
    private final DeclareProperty toProp;
    private final DeclareMapping mapping;
    private final String fromName = "self";
    private final String toName = "that";

    private DefMapping(
        DeclareProperty fromProp, DeclareProperty toProp, DeclareMapping mapping, String fromName, String toName
    ) {
        this.fromProp = fromProp;
        this.toProp = toProp;
        this.mapping = mapping;
    }

    public static DefMapping forward(DeclareProperty fromProp, DeclareProperty toProp, DeclareMapping mapping) {
        return new DefMapping(fromProp, toProp, mapping, "self", "that");
    }

    public static DefMapping backward(DeclareProperty fromProp, DeclareProperty toProp, DeclareMapping mapping) {
        return new DefMapping(fromProp, toProp, mapping, "that", "self");
    }

    public static DefMapping returning(String script) {
        return new Returning(script);
    }

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
            super(null, null, null, null, null);
            String[] scripts = {"return " + script + ";"};
            this.scripts = scripts;
        }

        @Override
        public String[] getScripts() { return scripts; }
    }
}
