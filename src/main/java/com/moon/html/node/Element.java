package com.moon.html.node;

import com.moon.core.lang.StringUtil;

import java.util.List;

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
     * innerHtml 表示这个元素的内容文本
     *
     * @return innerHtml
     */
    public abstract String innerHTML();

    /**
     * innerHtml 表示这个元素的内容文本
     *
     * @param innerHTML 内容文本
     *
     * @return this Element
     */
    public abstract Element innerHTML(String innerHTML);

    /**
     * 获取该DOM元素及其后代的HTML文本。
     *
     * @return 获取该DOM元素及其后代的HTML文本。
     */
    public abstract String outerHTML();

    /**
     * 在设置它的时候，会从给定的字符串开始解析，替换自身的内容。
     *
     * @return this Element
     */
    public abstract Element outerHTML(String outerHTML);

    /**
     * 该元素下一个兄弟节点, 如果为 null 表示不存在..
     *
     * @return null | Element
     */
    public abstract Element nextElementSibling();

    /**
     * 该元素上一个兄弟节点, 如果为null表示不存在
     *
     * @return null | Element
     */
    public abstract Element previousElementSibling();

    /**
     * 返回与参数中给定的选择器匹配的当前元素（或当前元素本身）的最近祖先元素。
     *
     * @param selector 选择器
     *
     * @return 符合条件的最近祖先元素。
     */
    public abstract Element closest(String selector);

    /**
     * 返回指定属性的属性值
     *
     * @param attributeName 属性名
     *
     * @return 属性名对应的属性值，当不存在属性值时，返回 null
     */
    public abstract String getAttribute(String attributeName);

    /**
     * 返回所有属性名
     *
     * @return 所有属性名
     */
    public abstract List<String> getAttributeNames();

    /**
     * 返回是否存在指定属性
     *
     * @param attributeName 属性名
     *
     * @return 返回是否存在指定属性
     */
    public abstract boolean hasAttribute(String attributeName);
}
