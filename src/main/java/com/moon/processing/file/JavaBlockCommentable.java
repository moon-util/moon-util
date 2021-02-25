package com.moon.processing.file;

import com.moon.processing.util.Collect2;
import com.moon.processing.util.String2;

import java.util.ArrayList;
import java.util.List;

/**
 * 可块级文注释和文档注释
 * <p>
 * 可块级注释的有: 字段、方法、构造器、类、接口、枚举、参数
 *
 * @author benshaoye
 */
public abstract class JavaBlockCommentable extends JavaLineCommentable {

    private final List<String> blockComments = new ArrayList<>();
    private final List<String> docComments = new ArrayList<>();

    public JavaBlockCommentable(Importer importer) {
        super(importer);
    }

    private List<String> getBlockComments() { return blockComments; }

    private List<String> getDocComments() { return docComments; }

    @Override
    public JavaLineCommentable lineCommentOf(String lineComment) {
        return blockCommentOf(lineComment);
    }

    public JavaBlockCommentable blockCommentOf(Object... comments) {
        if (comments != null) {
            List<String> list = getBlockComments();
            addCommentsToList(list, comments);
        }
        return this;
    }

    public JavaBlockCommentable docCommentOf(Object... comments) {
        if (comments != null) {
            List<String> list = getDocComments();
            addCommentsToList(list, comments);
        }
        return this;
    }

    public JavaBlockCommentable addBlockCommentOf(Object comment) {
        addCommentToList(getBlockComments(), comment);
        return this;
    }

    public JavaBlockCommentable addDocCommentOf(Object comment) {
        addCommentToList(getDocComments(), comment);
        return this;
    }

    private static void addCommentsToList(List<String> list, Object[] comments) {
        for (Object comment : comments) {
            addCommentToList(list, comment);
        }
    }

    private static void addCommentToList(List<String> list, Object comment) {
        if (comment == null) {
            list.add("");
        } else if (comment instanceof Class<?>) {
            list.add(((Class<?>) comment).getCanonicalName());
        } else {
            list.add(comment.toString());
        }
    }

    /**
     * 这个主要是给方法用的，方法可能会有参数注释
     *
     * @param originComments
     *
     * @return
     */
    protected List<String> usingDocComments(List<String> originComments) {
        return originComments;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        appendBlockComments(addr);
        appendDocComments(addr);
        appendAnnotations(addr);
    }

    private void appendBlockComments(JavaAddr addr) {
        List<String> comments = getBlockComments();
        doAppendCommentsList(addr, comments, false);
    }

    private void appendDocComments(JavaAddr addr) {
        List<String> comments = usingDocComments(getDocComments());
        doAppendCommentsList(addr, comments, true);
    }

    private static void doAppendCommentsList(JavaAddr addr, List<String> comments, boolean doclet) {
        if (Collect2.isNotEmpty(comments)) {
            addr.newAdd(doclet ? "/**" : "/*");
            String eachLeading = doclet ? " *" : "  ";
            for (String blockComment : comments) {
                addr.newAdd(eachLeading);
                if (String2.isNotBlank(blockComment)) {
                    addr.add(" ").add(blockComment);
                }
            }
            addr.newAdd(" */");
        }
    }
}
