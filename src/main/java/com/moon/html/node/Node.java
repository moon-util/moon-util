package com.moon.html.node;


import java.io.Serializable;

/**
 * @author moonsky
 */
public abstract class Node extends EventTarget implements Cloneable, Serializable {

    private String nodeName;
    private final int nodeType;
    private String nodeValue;

    public Node(String nodeName, int nodeType) {
        this(nodeName, null, nodeType);
    }

    public Node(String nodeName, String nodeValue, int nodeType) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.nodeValue = nodeValue;
    }

    protected final void setNodeValue(String value) {
        this.nodeValue = value;
    }

    protected final void setNodeName(String name) {
        this.nodeName = name;
    }

    public final String nodeName() { return nodeName; }

    public final int nodeType() { return nodeType; }

    public final String nodeValue() {
        return nodeName;
    }

    public final void nodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }
}
