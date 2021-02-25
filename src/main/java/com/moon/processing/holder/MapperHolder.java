package com.moon.processing.holder;

import com.moon.mapper.annotation.MapperFor;
import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;
import com.moon.processing.decl.CopierDeclared;
import com.moon.processing.decl.MapperDeclared;
import com.moon.processing.util.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MapperHolder extends BaseHolder implements JavaWritable {

    private final Map<String, MapperDeclared> mapperDeclaredMap = new HashMap<>();

    public MapperHolder(Holders holders) { super(holders); }

    public MapperDeclared with(MapperFor annotated, TypeElement thisElement, TypeElement thatElement) {
        String thisClass = Element2.getQualifiedName(thisElement);
        String thatClass = Element2.getQualifiedName(thatElement);
        String mapperKey = toMapperKey(thisClass, thatClass);
        MapperDeclared mapperDeclared = mapperDeclaredMap.get(mapperKey);
        if (mapperDeclared != null) {
            return mapperDeclared;
        }
        CopierDeclared forward = copierHolder().with(thisElement, thatElement);
        CopierDeclared backward = copierHolder().with(thatElement, thisElement);
        mapperDeclared = new MapperDeclared(annotated, forward, backward);
        mapperDeclaredMap.put(mapperKey, mapperDeclared);
        return mapperDeclared;
    }

    private String toMapperKey(String thisClass, String thatClass) {
        return String.format("%s>%s", thisClass, thatClass);
    }

    @Override
    public void write(JavaFiler writer) {

    }
}
