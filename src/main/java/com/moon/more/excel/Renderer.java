package com.moon.more.excel;

import com.moon.core.util.Table;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

/**
 * @author benshaoye
 */
public interface Renderer<E, C extends Iterator<E>> {

    void render(C data, Table<Integer,Integer,Object> tableFlat, Sheet sheet);
}
