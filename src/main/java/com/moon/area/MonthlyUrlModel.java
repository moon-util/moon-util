package com.moon.area;

import com.moon.core.lang.ObjectUtil;
import com.moon.core.util.ComparatorUtil;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author moonsky
 */
public class MonthlyUrlModel implements Comparable<MonthlyUrlModel>, Serializable {

    private final static long serialVersionUID = 1L;
    /**
     * 具体月份地区代码的url，访问 srcUrl 时会重定向到这里，并且不是 302 自动重定向
     * <p>
     * 而是通过 js location.href 重定向的，只能通过正则表达式提取出来
     */
    private final String redirectUrl;
    /**
     * 具体月份地区代码直接地址，访问这会有一步重定向
     */
    private final String srcUrl;
    /**
     * 具体月份，如：2020年8月xxxx县以上地区代码
     */
    private final String name;

    public MonthlyUrlModel(String srcUrl, String redirectUrl, String name) {
        this.redirectUrl = redirectUrl;
        this.srcUrl = srcUrl;
        this.name = name;
    }

    public String getSrcUrl() { return srcUrl; }

    public String getRedirectUrl() { return redirectUrl; }

    public String getName() { return name; }

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        MonthlyUrlModel that = (MonthlyUrlModel) o;
        return ObjectUtil.equalsProperties(this,
            that,
            MonthlyUrlModel::getRedirectUrl,
            MonthlyUrlModel::getSrcUrl,
            MonthlyUrlModel::getName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(redirectUrl, srcUrl, name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("YearlyModel{");
        sb.append("srcUrl='").append(srcUrl).append('\'');
        sb.append(", redirectUrl='").append(redirectUrl).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }


    @Override
    public int compareTo(MonthlyUrlModel o) {
        return ComparatorUtil.comparing(this,
            o,
            MonthlyUrlModel::getName,
            MonthlyUrlModel::getRedirectUrl,
            MonthlyUrlModel::getSrcUrl);
    }
}
