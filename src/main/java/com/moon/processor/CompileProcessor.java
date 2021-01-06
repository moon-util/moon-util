package com.moon.processor;

import com.google.auto.service.AutoService;
import com.moon.data.jdbc.annotation.Accessor;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processor.manager.CopierManager;
import com.moon.processor.manager.NameManager;
import com.moon.processor.manager.PojoManager;
import com.moon.processor.manager.MapperManager;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.Log2;
import com.moon.processor.utils.Process2;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
@AutoService(Processor.class)
public class CompileProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Environment2.initialize(processingEnv);
        Log2.initialize(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        String mapper = MapperFor.class.getCanonicalName();
        String accessor = Accessor.class.getCanonicalName();
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(accessor);
        supportedTypes.add(mapper);
        return supportedTypes;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        JavaWriter writer = new JavaWriter(Environment2.getFiler());
        NameManager nameManager = new NameManager();
        PojoManager pojoManager = new PojoManager(nameManager);
        CopierManager copierManager = new CopierManager(pojoManager, nameManager);
        MapperManager mapperManager = new MapperManager(copierManager, pojoManager, nameManager);
        processMapperFor(roundEnv, mapperManager);
        pojoManager.writeJavaFile(writer);
        copierManager.writeJavaFile(writer);
        mapperManager.writeJavaFile(writer);
        return true;
    }

    private void processMapperFor(RoundEnvironment roundEnv, MapperManager mapperManager) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MapperFor.class);
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            Process2.getMapperForClasses(typeElement).forEach(that -> {
                mapperManager.with(typeElement, that);
            });
        }
    }
}
