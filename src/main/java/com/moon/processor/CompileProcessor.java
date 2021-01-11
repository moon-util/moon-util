package com.moon.processor;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processor.manager.TablesManager;
import com.moon.processor.manager.*;
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
        NameManager nameManager = new NameManager();
        PojoManager pojoManager = new PojoManager(nameManager);
        PolicyManager policyManager = new PolicyManager();
        TablesManager tablesManager = new TablesManager(policyManager);
        ModelManager modelManager = new ModelManager(pojoManager, tablesManager, policyManager);
        CopierManager copierManager = new CopierManager(pojoManager, nameManager);
        MapperManager mapperManager = new MapperManager(copierManager, pojoManager, nameManager);

        AccessorManager accessorManager = new AccessorManager(copierManager, pojoManager, modelManager, nameManager);
        processMapperFor(roundEnv, mapperManager);
        processAccessor(roundEnv, accessorManager);
        processMapper();
        JavaWriter writer = new JavaWriter(Environment2.getFiler());
        pojoManager.writeJavaFile(writer);
        copierManager.writeJavaFile(writer);
        mapperManager.writeJavaFile(writer);
        modelManager.writeJavaFile(writer);
        tablesManager.writeJavaFile(writer);
        accessorManager.writeJavaFile(writer);
        return true;
    }

    private void processMapper() {}

    private void processAccessor(RoundEnvironment roundEnv, AccessorManager accessorManager) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Accessor.class);
        for (Element element : elements) {
            TypeElement accessor = (TypeElement) element;
            Process2.getAccessorClasses(accessor).forEach(model -> {
                accessorManager.with(accessor, model);
            });
        }
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
