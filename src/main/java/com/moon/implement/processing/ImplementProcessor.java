package com.moon.implement.processing;

import com.google.auto.service.AutoService;
import com.moon.implement.annotation.AutoImplement;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author moonsky
 */
@AutoService(Processor.class)
public class ImplementProcessor extends AbstractProcessor {

    private final static String SUPPORTED_TYPE = AutoImplement.class.getName();

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        EnvUtils.initialize(processingEnv);
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(SUPPORTED_TYPE);
        return types;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        EnvUtils.newSourceFile();
        return false;
    }
}
