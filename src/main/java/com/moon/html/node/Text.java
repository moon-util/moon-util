package com.moon.html.node;

import com.moon.core.lang.StringUtil;

/**
 * @author moonsky
 */
public final class Text extends Node {

    private Element parent;

    public Text(String textContent) {
        super("#text", 2);
        this.setNodeValue(StringUtil.trimToEmpty(textContent));
    }

    public int length() { return nodeValue().length(); }
}
