package com.moon.processor.manager;

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
public class MapperManager implements JavaFileWriteable {

    private final NameManager nameManager;
    private final PojoManager pojoManager;
    private final CopierManager copierManager;
    private Map<String, DefBeanMapper> definedMapperMap = new LinkedHashMap<>();

    public MapperManager(
        CopierManager copierManager, PojoManager pojoManager, NameManager nameManager
    ) {
        this.copierManager = copierManager;
        this.pojoManager = pojoManager;
        this.nameManager = nameManager;
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
            DeclaredPojo thisClass = pojoManager.with(thisElem);
            DeclaredPojo thatClass = pojoManager.with(thatElem);
            DefBeanCopier forward = copierManager.with(thisElem, thatElem);
            DefBeanCopier backward = copierManager.with(thatElem, thisElem);
            mapper = new DefBeanMapper(forward, backward, thisClass, thatClass, nameManager);
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
