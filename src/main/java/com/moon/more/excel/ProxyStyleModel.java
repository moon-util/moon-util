package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * 样式代理模型
 * @author benshaoye
 */
class ProxyStyleModel extends ProxyModel<Workbook, CellStyleProxy, Object, ProxyStyleBuilder, ProxyStyleSetter> {

    public ProxyStyleModel() {}
}
