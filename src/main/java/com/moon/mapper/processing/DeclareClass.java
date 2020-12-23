package com.moon.mapper.processing;

import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;

/**
 * POJO 定义
 *
 * 描述只有 field、setter、getter、hashCode、toString、equals 这些方法的类
 * 如：Entity、DTO、DO、BO、VO
 *
 * key: property name
 * value: property declaration (field + getter + setter)
 *
 * @author benshaoye
 */
public class DeclareClass extends LinkedHashMap<String, DeclareProperty> {

    /**
     * 声明类
     */
    private TypeElement declareElement;
    /**
     * 实现类
     */
    private String implClassname;

    public DeclareClass() {}
}
