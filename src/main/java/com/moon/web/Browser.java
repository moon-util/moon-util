package com.moon.web;

import java.util.function.Predicate;

/**
 * @author moonsky
 */
class Browser {

    public Browser() {
    }

    /**
     * 是否是 chrome 浏览器
     *
     * @return true: 是 chrome 浏览器
     */
    public boolean isChrome() {
        throw new UnsupportedOperationException();
    }

    /**
     * 是否是 Firefox 浏览器
     *
     * @return true: 是 Firefox 浏览器
     */
    public boolean isFirefox() {
        throw new UnsupportedOperationException();
    }

    /**
     * 是否是 Opera 浏览器
     *
     * @return true: 是 Opera 浏览器
     */
    public boolean isOpera() {
        throw new UnsupportedOperationException();
    }

    /**
     * 是否是 IE 浏览器
     *
     * @return true: 是 IE 浏览器
     */
    public boolean isIE() {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义检查方式
     *
     * @param tester 断言函数
     *
     * @return 返回符合断言函数的结果
     */
    public boolean isMatchedBy(Predicate<String> tester) {
        throw new UnsupportedOperationException();
    }

    /**
     * 返回远程客户端浏览器主版本号
     *
     * @return 如远程浏览器是 chrome 83.12.23；这里返回主版本号 83
     */
    public int getMajorVersion() {
        throw new UnsupportedOperationException();
    }
}
