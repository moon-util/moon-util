package com.moon.processor;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.SubQuery;
import com.moon.accessor.annotation.TableEntity;
import com.moon.mapper.annotation.MapperFor;
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
        String query = SubQuery.class.getCanonicalName();
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(accessor);
        supportedTypes.add(mapper);
        supportedTypes.add(query);
        return supportedTypes;
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        NameHolder nameHolder = new NameHolder();
        SessionManager sessionManager = new SessionManager();
        PojoHolder pojoHolder = new PojoHolder(nameHolder, sessionManager);
        PolicyHolder policyHolder = new PolicyHolder();
        AliasesHolder aliasesHolder = new AliasesHolder();
        TablesHolder tablesHolder = new TablesHolder(policyHolder);
        ModelHolder modelHolder = new ModelHolder(pojoHolder, tablesHolder, aliasesHolder, policyHolder);
        CopierHolder copierHolder = new CopierHolder(pojoHolder, nameHolder);
        MapperHolder mapperHolder = new MapperHolder(copierHolder, pojoHolder, nameHolder);

        AccessorHolder accessorHolder = new AccessorHolder(copierHolder, pojoHolder, modelHolder, nameHolder);
        processMapperFor(roundEnv, mapperHolder);
        processTableModel(roundEnv, modelHolder);
        processAccessor(roundEnv, accessorHolder);
        processMapper();
        processSubQuery(roundEnv);
        JavaWriter writer = new JavaWriter(Environment2.getFiler());
        pojoHolder.writeJavaFile(writer);
        copierHolder.writeJavaFile(writer);
        mapperHolder.writeJavaFile(writer);
        modelHolder.writeJavaFile(writer);
        tablesHolder.writeJavaFile(writer);
        aliasesHolder.writeJavaFile(writer);
        sessionManager.writeJavaFile(writer);
        accessorHolder.writeJavaFile(writer);
        return true;
    }

    private void processSubQuery(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SubQuery.class);
        for (Element element : elements) {
            Log2.warn(">> ======== --->>");
            Log2.warn("Elem: {}", element);
            Log2.warn("Type: {}", element.asType());
        }
    }

    private void processMapper() {}

    private void processTableModel(RoundEnvironment roundEnv, ModelHolder modelHolder) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(TableEntity.class);
        for (Element element : elements) {
            modelHolder.with((TypeElement) element);
        }
    }

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
