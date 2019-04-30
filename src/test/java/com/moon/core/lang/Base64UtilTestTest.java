package com.moon.core.lang;

import com.moon.core.util.IteratorUtil;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author benshaoye
 */
class Base64UtilTestTest {

    private static final char[] toBase64 = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    /**
     * It's the lookup table for "URL and Filename safe Base64" as specified
     * in Table 2 of the RFC 4648, with the '+' and '/' changed to '-' and
     * '_'. This table is used when BASE64_URL is specified.
     */
    private static final char[] toBase64URL = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };

    @Test
    void testToString() {
        String str = "发票开具baiwang.invoice.open\n" +
            "发票查询baiwang.invoice.query\n" +
            "发票作废baiwang.invoice.invalid\n" +
            "发票打印baiwang.invoice.print\n" +
            "版式文件生成baiwang.formatfile.build\n" +
            "版式文件查询baiwang.formatfile.query\n" +
            "发票组合查询baiwang.invoice.queryCombination\n" +
            "空白发票查询baiwang.invoice.EMPTY.query\n" +
            "机动车发票开具baiwang.invoice.vehicle.open\n" +
            "机动车发票查询baiwang.invoice.vehicle.query\n" +
            "发票全状态查询baiwang.invoice.status\n" +
            "监控信息查询baiwang.tax.monitor.query\n" +
            "领用存信息查询baiwang.invoice.purchase.query\n" +
            "税控设备联通性监测baiwang.tax.device.connected\n" +
            "(预制)发票上传baiwang.invoice.upload\n" +
            "流水单导入baiwang.invoice.flowSingleImport\n" +
            "流水单状态查询baiwang.invoice.FlowSingleQuery\n" +
            "流水单删除baiwang.invoice.flowSingleDelete\n" +
            "预制(待开)发票作废baiwang.invoice.wait.invalid\n" +
            "智能赋码baiwang.bizinfo.search\n" +
            "云抬头获取baiwang.bizinfo.companySearch\n" +
            "商品编码同步baiwamg.invoice.code.queryGoods\n" +
            "自定义商品添加baiwang.common.goods.code.add\n" +
            "自定义商品查询baiwang.common.goods.code.query\n" +
            "自定义商品编码维护baiwang.common.goods.code.update\n" +
            "自定义商品删除baiwang.common.goods.code.delete\n" +
            "开票终端添加baiwang.tax.terminal.add\n" +
            "开票终端查询baiwang.user.terminal.invoice.query\n" +
            "开票终端维护baiwang.tax.terminal.update\n" +
            "开票终端删除baiwang.user.terminal.delete\n" +
            "取数baiwang.inputv2.sync\n" +
            "取数任务反馈baiwang.inputv2.syncResult\n" +
            "初始化数据baiwang.inputv2.init\n" +
            "发票查验baiwang.inputv2.collect\n" +
            "合规查验baiwang.inputv2.compliancecollect\n" +
            "OCR识别五要素baiwang.other.ocr.read\n" +
            "OCR识别全票面baiwang.other.ocr.readall\n" +
            "认证抵扣申请baiwang.inputv2.deductible\n" +
            "认证结果反馈baiwang.inputv2.result\n" +
            "获取税期信息baiwang.inputv2.getTaxperiod\n" +
            "发票报账记账状态同步baiwang.inputv2.accountingStatus\n" +
            "发票信息查询baiwang.inputv2.findInvoice\n" +
            "违法企业查询baiwang.inputv2.taxofficelist\n" +
            "企业注册baiwang.user.tenant.addTenant\n" +
            "注册（单机盘版）baiwang.user.tenant.register\n" +
            "企业变更管理baiwang.admin.update.applyFor\n" +
            "企业信息审核查询baiwang.common.company.verify.query\n" +
            "纳税人基础信息查询baiwang.admin.getTaxNoInfo\n" +
            "用户注册baiwang.user.tenant.addUser\n" +
            "用户更新baiwang.user.tenant.updateUser\n" +
            "创建机构baiwang.user.tenant.addOrganization\n" +
            "机构查询baiwang.user.tenant.queryOrganization\n" +
            "机构信息（网点）更新baiwang.user.tenant.updateZzCode";

        final Pattern pattern = Pattern.compile("([^\\w\\.]+)");
        IteratorUtil.forEach(str.split("\\n"), item -> {
            Matcher matcher = pattern.matcher(item);
            matcher.find();
            final int start = matcher.start(), end = matcher.end();
            String desc = item.substring(start, end);
            String name = item.substring(end);
        });
    }
}