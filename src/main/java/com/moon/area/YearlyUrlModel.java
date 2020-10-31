package com.moon.area;

import com.moon.core.lang.ObjectUtil;
import com.moon.core.util.ComparatorUtil;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author moonsky
 */
public class YearlyUrlModel implements Comparable<YearlyUrlModel>, Serializable {

    private final static long serialVersionUID = 1L;

    private final String redirectUrl;
    private final String srcUrl;
    private final String name;

    public YearlyUrlModel(String srcUrl, String redirectUrl, String name) {
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
        YearlyUrlModel that = (YearlyUrlModel) o;
        return ObjectUtil.equalsProperties(this,
            that,
            YearlyUrlModel::getRedirectUrl,
            YearlyUrlModel::getSrcUrl,
            YearlyUrlModel::getName);
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
    public int compareTo(YearlyUrlModel o) {
        return ComparatorUtil.comparing(this,
            o,
            YearlyUrlModel::getName,
            YearlyUrlModel::getRedirectUrl,
            YearlyUrlModel::getSrcUrl);
    }
}
