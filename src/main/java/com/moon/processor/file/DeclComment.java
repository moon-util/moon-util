package com.moon.processor.file;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 目前是给 表字段列举专用，也没考虑其他情况
 *
 * @author benshaoye
 */
public class DeclComment implements ScriptsProvider {

    private final static String PREFIX = "   ";

    private final CommentType type;
    private final String[] comments;

    public DeclComment(CommentType type, String... comments) {
        this.type = type;
        this.comments = comments;
    }


    @Override
    public List<String> getScripts() {
        List<String> comments = Arrays.stream(this.comments).map(it -> PREFIX + it).collect(toList());
        comments.add(0, "    /*");
        comments.add(" */");
        comments.add("");
        return comments;
    }
}
