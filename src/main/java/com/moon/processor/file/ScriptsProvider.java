package com.moon.processor.file;

import java.util.List;

/**
 * @author benshaoye
 */
public interface ScriptsProvider {

    /**
     * 返回语句列表
     *
     * @return
     */
    List<String> getScripts();
}
