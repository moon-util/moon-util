package com.moon.processing.file;

/**
 * 仅支持单行文本注释
 *
 * @author benshaoye
 */
public abstract class JavaLineCommentable extends BaseAnnotatedElement {

    private String comment;

    public JavaLineCommentable(Importer importer) {
        super(importer);
    }

    protected void appendAnnotations(JavaAddr addr) {
        super.appendTo(addr);
    }

    public JavaLineCommentable lineCommentOf(String lineComment) {
        this.comment = lineComment;
        return this;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        appendAnnotations(addr);
    }
}
