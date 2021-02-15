package com.moon.processor;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.TableModel;
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

import static javax.lang.model.SourceVersion.latestSupported;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
@AutoService(Processor.class)
public class CompileProcessor extends AbstractProcessor {

    private final NameHolder nameHolder;
    private final SessionManager sessionManager;
    private final PojoHolder pojoHolder;
    private final PolicyHolder policyHolder;
    private final AccessorHolder accessorHolder;
    private final AliasesHolder aliasesHolder;
    private final TablesHolder tablesHolder;
    private final ModelHolder modelHolder;
    private final CopierHolder copierHolder;
    private final MapperHolder mapperHolder;

    public CompileProcessor() {
        nameHolder = new NameHolder();
        sessionManager = new SessionManager();
        pojoHolder = new PojoHolder(nameHolder, sessionManager);
        policyHolder = new PolicyHolder();
        aliasesHolder = new AliasesHolder();
        tablesHolder = new TablesHolder(policyHolder);
        modelHolder = new ModelHolder(pojoHolder, tablesHolder, aliasesHolder, policyHolder);
        copierHolder = new CopierHolder(pojoHolder, nameHolder);
        mapperHolder = new MapperHolder(copierHolder, pojoHolder, nameHolder);
        accessorHolder = new AccessorHolder(copierHolder, pojoHolder, modelHolder, nameHolder);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Environment2.initialize(processingEnv);
        Log2.initialize(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() { return latestSupported(); }

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
        // if (roundEnv.processingOver()) {
        //     doWriteJavaFile();
        // } else {
        //     doProcess(roundEnv);
        // }
        doProcess(roundEnv);
        doWriteJavaFile();
        return true;
    }

    private void doProcess(RoundEnvironment roundEnv) {
        processMapperFor(roundEnv, mapperHolder);
        processTableModel(roundEnv, modelHolder);
        processAccessor(roundEnv, accessorHolder);
        processMapper();
        processSubQuery(roundEnv);
    }

    private void doWriteJavaFile() {
        JavaWriter writer = new JavaWriter(Environment2.getFiler());
        pojoHolder.writeJavaFile(writer);
        copierHolder.writeJavaFile(writer);
        mapperHolder.writeJavaFile(writer);
        modelHolder.writeJavaFile(writer);
        tablesHolder.writeJavaFile(writer);
        aliasesHolder.writeJavaFile(writer);
        sessionManager.writeJavaFile(writer);
        accessorHolder.writeJavaFile(writer);
    }

    private void processSubQuery(RoundEnvironment roundEnv) {
    }

    private void processMapper() {}

    private void processTableModel(RoundEnvironment roundEnv, ModelHolder modelHolder) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(TableModel.class);
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
