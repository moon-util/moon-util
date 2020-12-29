package com.moon.processor;

import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.Log2;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author benshaoye
 */
public class CompileProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Environment2.initialize(processingEnv);
        Log2.initialize(processingEnv);
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        return true;
    }
}
