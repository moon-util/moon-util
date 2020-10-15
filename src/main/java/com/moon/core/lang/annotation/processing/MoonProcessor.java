package com.moon.core.lang.annotation.processing;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author moonsky
 */
public class MoonProcessor extends AbstractProcessor {

    public MoonProcessor() {
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        return false;
    }
}
