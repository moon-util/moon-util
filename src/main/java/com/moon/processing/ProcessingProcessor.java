package com.moon.processing;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.TableModel;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processing.holder.*;
import com.moon.processing.util.Logger2;
import com.moon.processing.util.Processing2;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.moon.processing.util.Extract2.getMapperForValues;

/**
 * @author benshaoye
 */
@AutoService(Processor.class)
public class ProcessingProcessor extends AbstractProcessor {

    private final TypeHolder typeHolder;
    private final CopierHolder copierHolder;
    private final MapperHolder mapperHolder;
    private final TableHolder tableHolder;
    private final RecordHolder recordHolder;
    private final TablesHolder tablesHolder;
    private final AliasesHolder aliasesHolder;
    private final AccessorHolder accessorHolder;
    private final DslHelper dslHelper;

    public ProcessingProcessor() {
        NameHolder nameHelper = new NameHolder();
        this.dslHelper = new DslHelper();
        this.tablesHolder = new TablesHolder();
        this.aliasesHolder = new AliasesHolder();
        this.typeHolder = new TypeHolder(nameHelper);
        this.recordHolder = new RecordHolder(typeHolder);
        this.tableHolder = new TableHolder(typeHolder, tablesHolder, aliasesHolder);
        this.copierHolder = new CopierHolder(recordHolder);
        this.mapperHolder = new MapperHolder(copierHolder);
        this.accessorHolder = new AccessorHolder(nameHelper, typeHolder, tableHolder);
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
        Logger2.warn("================================>>>>>>>>");
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
        roundEnv.getElementsAnnotatedWith(MapperFor.class).forEach(annotated -> {
            TypeElement mapperForAnnotated = (TypeElement) annotated;
            MapperFor mapperFor = annotated.getAnnotation(MapperFor.class);
            Collection<TypeElement> forValues = getMapperForValues(mapperForAnnotated);
            for (TypeElement forElement : forValues) {
                mapperHolder.with(mapperFor, mapperForAnnotated, forElement);
            }
        });
    }

    private void doProcessingTableModel(RoundEnvironment roundEnv) {
        Set<? extends Element> models = roundEnv.getElementsAnnotatedWith(TableModel.class);
        models.forEach(element -> typeHolder.with((TypeElement) element));
    }

    private void doProcessingAccessor(RoundEnvironment roundEnv) {
        Set<? extends Element> accessors = roundEnv.getElementsAnnotatedWith(Accessor.class);
        accessors.forEach(element -> accessorHolder.with((TypeElement) element));
    }

    private void doWriteJavaFiles() {
        JavaFiler filer = new JavaFiler();
        recordHolder.write(filer);
        copierHolder.write(filer);
        mapperHolder.write(filer);

        tableHolder.write(filer);
        tablesHolder.write(filer);
        aliasesHolder.write(filer);
        accessorHolder.write(filer);

        dslHelper.write(filer);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Processing2.initialize(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
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
