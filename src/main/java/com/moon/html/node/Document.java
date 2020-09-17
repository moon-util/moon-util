package com.moon.html.node;

/**
 * @author moonsky
 */
public abstract class Document extends Node {

    private DocumentType doctype;

    public Document() { super("#document", 9); }

    public DocumentType doctype() {
        return doctype;
    }
}
