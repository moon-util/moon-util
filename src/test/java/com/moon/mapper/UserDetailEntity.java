package com.moon.mapper;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author moonsky
 */
public class UserDetailEntity extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment env
    ) {
        // processingEnv.
        return false;
    }

    public static class UserDetailEntityGetter {

    }
}
