package com.moon.processing;

import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.TableModel;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processing.holder.*;
import com.moon.processing.util.Processing2;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class ProcessingProcessor extends AbstractProcessor {

    private final NameHolder nameHolder;
    private final TypeHolder typeHolder;
    private final CopierHolder copierHolder;
    private final MapperHolder mapperHolder;
    private final TableHolder tableHolder;
    private final AccessorHolder accessorHolder;

    public ProcessingProcessor() {
        this.nameHolder = new NameHolder();
        this.typeHolder = new TypeHolder(nameHolder);
        this.tableHolder = new TableHolder(typeHolder);
        this.copierHolder = new CopierHolder(typeHolder);
        this.mapperHolder = new MapperHolder(typeHolder);
        this.accessorHolder = new AccessorHolder(typeHolder, tableHolder);
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        // if (roundEnv.processingOver()) {
        //     doWriteJavaFiles();
        // } else {
        //     doProcessing(roundEnv);
        // }
        doProcessing(roundEnv);
        doWriteJavaFiles();
        return true;
    }

    private void doProcessing(RoundEnvironment roundEnv) {
        doProcessingMapper(roundEnv);
        doProcessingTableModel(roundEnv);
        doProcessingAccessor(roundEnv);
    }

    private void doProcessingMapper(RoundEnvironment roundEnv) {

    }

    private void doProcessingTableModel(RoundEnvironment roundEnv) {
        Set<? extends Element> models = roundEnv.getElementsAnnotatedWith(TableModel.class);
        models.forEach(element -> typeHolder.with((TypeElement) element));
    }

    private void doProcessingAccessor(RoundEnvironment roundEnv) {
        Set<? extends Element> accessors = roundEnv.getElementsAnnotatedWith(Accessor.class);
        accessors.forEach(element -> accessorHolder.with((TypeElement) element));
    }

    private void doWriteJavaFiles() {}

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Processing2.initialize(processingEnv);
        // Logger2.initialize(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(Accessor.class.getCanonicalName());
        supportedTypes.add(MapperFor.class.getCanonicalName());
        supportedTypes.add(TableModel.class.getCanonicalName());
        return supportedTypes;
    }
}
