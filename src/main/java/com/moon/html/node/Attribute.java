package com.moon.html.node;

import java.util.Map;

/**
 * @author moonsky
 */
public class Attribute extends Node implements Map.Entry<String, String> {

    public Attribute(String nodeName) {
        super(nodeName, 2);
    }

    @Override
    public NodeList childNodes() {
        return NodeList.EMPTY;
    }

    @Override
    public Node firstChild() {
        return null;
    }

    @Override
    public Node lastChild() {
        return null;
    }

    @Override
    public Document ownerDocument() {
        return null;
    }

    @Override
    public Node parentNode() {
        return null;
    }

    @Override
    public Element parentElement() {
        return null;
    }

    @Override
    public String getKey() {
        return nodeName();
    }

    @Override
    public String getValue() {
        return nodeValue();
    }

    @Override
    public String setValue(String value) {
        String oldValue = nodeValue();
        setNodeValue(value);
        return oldValue;
    }
}
