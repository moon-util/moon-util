package com.moon.html.node;

import com.moon.core.lang.StringUtil;

/**
 * @author moonsky
 */
public class TextNode extends Node {

    private Element parent;

    public TextNode(String textContent) {
        super("#text", 2);
        this.setNodeValue(StringUtil.trimToEmpty(textContent));
    }

    public int length() { return nodeValue().length(); }

    @Override
    public NodeList childNodes() { return NodeList.EMPTY; }

    @Override
    public Node firstChild() { return null; }

    @Override
    public Node lastChild() { return null; }

    @Override
    public Document ownerDocument() {
        return parent == null ? null : parent.ownerDocument();
    }

    @Override
    public Node parentNode() {
        return parent;
    }

    @Override
    public Element parentElement() {
        return parent;
    }
}
