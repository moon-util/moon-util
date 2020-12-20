package com.moon.data.jdbc.processing;

/**
 * @author benshaoye
 */
public enum Replacer {
    /**
     * 接口名称
     */
    INTERFACE("<interface>"),
    IMPORTS,
    type,
    name,
    field,
    params,
    prefix,
    caption,
    ;

    private final String placeholder;

    Replacer() { this.placeholder = ("{" + name() + "}"); }

    Replacer(String placeholder) { this.placeholder = placeholder; }

    public String replace(String template, Object replacement) {
        String parameter = String.valueOf(replacement);
        return StringUtils.replaceAll(template, placeholder, parameter);
    }

    public static Group of(Replacer... replacers) { return new Group(replacers); }

    static class Group {

        private final Replacer[] replacers;

        Group(Replacer... replacers) {this.replacers = replacers;}

        public String replace(String template, Object... args) {
            String replaced = template;
            for (int i = 0; i < replacers.length; i++) {
                replaced = replacers[i].replace(replaced, args[i]);
            }
            return replaced;
        }
    }
}
