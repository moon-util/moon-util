package com.moon.html.node;

import java.util.List;

/**
 * @author benshaoye
 */
abstract class Example {
    
    
    private abstract static class Element {

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
         * @param outerHTML 包含当前节点在内以及所有子节点的 html
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

    private abstract static class Node {

        /**
         * 在当前节点最后位置追加一个节点
         *
         * @param node 将要被追加的节点
         *
         * @return 被追加的节点
         */
        public abstract Node appendChild(Node node);

        /**
         * 所有子节点
         *
         * @return 所有子节点
         */
        public abstract NodeList childNodes();

        /**
         * 当前节点是否有子节点
         *
         * @return true | false
         */
        public boolean hasChildNodes() {
            NodeList list = childNodes();
            return list != null && !list.isEmpty();
        }

        /**
         * 插入新节点到指定节点前面
         *
         * @param newNode       新节点
         * @param referenceNode 插入到什么位置
         *
         * @return 被插入的节点（新节点）
         */
        public abstract Node insertBefore(Node newNode, Node referenceNode);

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

        /**
         * 返回一个元素内所有子节点及其后代的文本内容。
         *
         * @return 文本内容
         */
        public abstract String textContent();

        /**
         * 设置一个元素内所有子节点及其后代的文本内容。
         *
         * @param content 文本内容
         */
        public abstract void textContent(String content);
    }
}
