package com.moon.area;

import java.util.Date;
import java.util.Objects;

/**
 * @author moonsky
 */
public class CityCodeModel {

    private String code;
    private String name;
    private Date expiredOn;

    public CityCodeModel() { }

    public CityCodeModel(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }

    public CityCodeModel(String code, String name, Date expiredOn) {
        this(code, name);
        this.expiredOn = expiredOn;
    }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Date getExpiredOn() { return expiredOn; }

    public void setExpiredOn(Date expiredOn) { this.expiredOn = expiredOn; }

    public boolean isExpired() { return expiredOn == null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CityCodeModel that = (CityCodeModel) o;
        return Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(expiredOn,
            that.expiredOn);
    }

    @Override
    public int hashCode() { return Objects.hash(code, name, expiredOn); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CityCodeModel{");
        sb.append("code='").append(code).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", expiredOn=").append(expiredOn);
        sb.append('}');
        return sb.toString();
    }
}
