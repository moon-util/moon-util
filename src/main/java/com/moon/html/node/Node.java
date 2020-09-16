package com.moon.html.node;


import com.moon.core.lang.IntUtil;

import java.io.Serializable;

/**
 * @author moonsky
 */
public abstract class Node extends EventTarget implements Cloneable, Serializable {

    private final String nodeName;
    private final int nodeType;
    private String nodeValue;

    public Node(String nodeName, int nodeType) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
    }

    protected void setNodeValue(String value) {
        this.nodeValue = value;
    }

    public String nodeName() { return nodeName; }

    public int nodeType() { return nodeType; }

    public String nodeValue() {
        return nodeName;
    }

    public int getNodeValueAsInt() {
        return nodeValue == null ? 0 : IntUtil.toIntValue(nodeValue);
    }

    /**
     * 所有子节点
     *
     * @return 所有子节点
     */
    public abstract NodeList childNodes();

    /**
     * 第一个子节点
     *
     * @return 第一个子节点
     */
    public abstract Node firstChild();

    /**
     * 最后一个子节点
     *
     * @return 最后一个子节点
     */
    public abstract Node lastChild();

    /**
     * 当前所属文档
     *
     * @return 当前所属文档，如果不存在返回 null
     */
    public abstract Document ownerDocument();

    /**
     * 返回父节点
     *
     * @return 返回父节点
     */
    public abstract Node parentNode();

    /**
     * 返回父元素
     *
     * @return 返回父元素，如果没有父元素或父元素不是一个元素节点返回 null
     */
    public abstract Element parentElement();
}
