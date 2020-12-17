package com.moon.data.jdbc.processing;

import com.moon.data.jdbc.annotation.Accessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
// @AutoService(Processor.class)
public class AccessorProcessor extends AbstractProcessor {

    private final static String ACCESSOR = Accessor.class.getCanonicalName();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supports = new HashSet<>();
        supports.add(ACCESSOR);
        return supports;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        return false;
    }
}
