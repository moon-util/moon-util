package com.moon.html.node;

import com.moon.core.lang.StringUtil;

/**
 * @author moonsky
 */
public abstract class Element extends Node {

    private String id;
    private String classname;

    public Element(String nodeName) {
        super(nodeName, 1);
    }

    String getClassname() {
        return StringUtil.emptyIfNull(classname);
    }

    void setClassname(TokenList classList) {
        this.classname = classList.value();
    }

    /*
     * Exposed methods
     */

    public String id() { return this.id; }

    public TokenList classList() {
        return new TokenList(getClassname(), this::setClassname);
    }

    public String className() { return getClassname(); }

    public void className(String className) {
        this.classname = className;
    }

    /**
     * 只读
     *
     * @return
     */
    public String tagName() { return nodeName(); }

    /**
     * 获取该DOM元素及其后代的HTML文本。
     *
     * @return 获取该DOM元素及其后代的HTML文本。
     */
    public String outerHTML() {
        throw new UnsupportedOperationException();
    }

    /**
     * 在设置它的时候，会从给定的字符串开始解析，替换自身的内容。
     *
     * @param outerHTML 包含当前节点在内以及所有子节点的 html
     *
     * @return this Element
     */
    public Element outerHTML(String outerHTML){
        throw new UnsupportedOperationException();
    }
}
