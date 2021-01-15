package com.moon.processor;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processor.holder.TablesHolder;
import com.moon.processor.holder.*;
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
        NameHolder nameHolder = new NameHolder();
        PojoHolder pojoHolder = new PojoHolder(nameHolder);
        PolicyHolder policyHolder = new PolicyHolder();
        TablesHolder tablesHolder = new TablesHolder(policyHolder);
        ModelHolder modelHolder = new ModelHolder(pojoHolder, tablesHolder, policyHolder);
        CopierHolder copierHolder = new CopierHolder(pojoHolder, nameHolder);
        MapperHolder mapperHolder = new MapperHolder(copierHolder, pojoHolder, nameHolder);

        AccessorHolder accessorHolder = new AccessorHolder(copierHolder, pojoHolder, modelHolder, nameHolder);
        processMapperFor(roundEnv, mapperHolder);
        processAccessor(roundEnv, accessorHolder);
        processMapper();
        JavaWriter writer = new JavaWriter(Environment2.getFiler());
        pojoHolder.writeJavaFile(writer);
        copierHolder.writeJavaFile(writer);
        mapperHolder.writeJavaFile(writer);
        modelHolder.writeJavaFile(writer);
        tablesHolder.writeJavaFile(writer);
        accessorHolder.writeJavaFile(writer);
        return true;
    }

    private void processMapper() {}

    private void processAccessor(RoundEnvironment roundEnv, AccessorHolder accessorHolder) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Accessor.class);
        for (Element element : elements) {
            TypeElement accessor = (TypeElement) element;
            Process2.getAccessorClasses(accessor).forEach(model -> {
                accessorHolder.with(accessor, model);
            });
        }
    }

    private void processMapperFor(RoundEnvironment roundEnv, MapperHolder mapperHolder) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MapperFor.class);
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            Process2.getMapperForClasses(typeElement).forEach(that -> {
                mapperHolder.with(typeElement, that);
            });
        }
    }
}
