package com.moon.mapping.processing;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author benshaoye
 */
abstract class AbstractMapModel {

    private final Implementation implementation;
    private final ProcessingEnvironment env;
    private final String thisClassname;
    private final Map<String, PropertyModel> propertiesModelMap;
    private final Map<String, MappedPropsMap> targetModelsMap;

    protected AbstractMapModel(
        Implementation implementation,
        ProcessingEnvironment env,
        String thisClassname,
        Map<String, PropertyModel> propertiesModelMap,
        Map<String, MappedPropsMap> targetModelsMap
    ) {
        this.implementation = implementation;
        this.env = env;
        this.thisClassname = thisClassname;
        this.propertiesModelMap = propertiesModelMap;
        this.targetModelsMap = targetModelsMap;
    }

    public final ProcessingEnvironment getEnv() { return env; }

    public final Implementation getImpl() { return implementation; }

    public final String getThisClassname() { return thisClassname; }

    public final Map<String, PropertyModel> getPropertiesModelMap() {
        return propertiesModelMap == null ? Collections.emptyMap() : propertiesModelMap;
    }

    public final Map<String, MappedPropsMap> getTargetModelsMap() {
        return targetModelsMap == null ? Collections.emptyMap() : targetModelsMap;
    }

    /**
     * 创建 java 类
     *
     * @param filer
     */
    protected abstract void writeJavaFile(Filer filer) throws IOException;
}
