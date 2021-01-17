package com.moon.processor.holder;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.def.DefBeanCopier;
import com.moon.processor.def.DefBeanMapper;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MapperHolder implements JavaFileWriteable {

    private final NameHolder nameHolder;
    private final PojoHolder pojoHolder;
    private final CopierHolder copierHolder;
    private final Map<String, DefBeanMapper> definedMapperMap = new LinkedHashMap<>();

    public MapperHolder(
        CopierHolder copierHolder, PojoHolder pojoHolder, NameHolder nameHolder
    ) {
        this.copierHolder = copierHolder;
        this.pojoHolder = pojoHolder;
        this.nameHolder = nameHolder;
    }

    public DefBeanMapper with(Class<?> thisClass, Class<?> thatClass) {
        return with(thisClass.getCanonicalName(), thatClass.getCanonicalName());
    }

    public DefBeanMapper with(TypeElement thisElem, TypeElement thatElem) {
        String thisClassname = Element2.getQualifiedName(thisElem);
        String thatClassname = Element2.getQualifiedName(thatElem);
        String mapperKey = String2.keyOf(thisClassname, thatClassname);
        DefBeanMapper mapper = definedMapperMap.get(mapperKey);
        if (mapper == null) {
            DeclaredPojo thisClass = pojoHolder.with(thisElem);
            DeclaredPojo thatClass = pojoHolder.with(thatElem);
            DefBeanCopier forward = copierHolder.with(thisElem, thatElem);
            DefBeanCopier backward = copierHolder.with(thatElem, thisElem);
            mapper = new DefBeanMapper(forward, backward, thisClass, thatClass, nameHolder);
            definedMapperMap.put(mapperKey, mapper);
        }
        return mapper;
    }

    public DefBeanMapper with(String thisClass, String thatClass) {
        Elements elements = Environment2.getUtils();
        return with(elements.getTypeElement(thisClass), elements.getTypeElement(thatClass));
    }

    @Override
    public void writeJavaFile(JavaWriter javaWriter) {
        definedMapperMap.forEach((key, mapper) -> mapper.writeJavaFile(javaWriter));
    }
}
