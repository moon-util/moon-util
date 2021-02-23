package com.moon.processing;

import com.google.auto.service.AutoService;
import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.condition.IfMatching;
import com.moon.mapper.annotation.MapperFor;
import com.moon.processing.holder.Holders;
import com.moon.processing.holder.MatchingHolder;
import com.moon.processing.util.Logger2;
import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Element2;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.moon.processing.util.Extract2.getMapperForValues;

/**
 * @author benshaoye
 */
@AutoService(Processor.class)
public class ProcessingProcessor extends AbstractProcessor {

    private final Holders holders;

    public ProcessingProcessor() {
        this.holders = Holders.INSTANCE;
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
        doProcessingMatching(roundEnv);
        doProcessingTableModel(roundEnv);
        doProcessingAccessor(roundEnv);
    }

    private void doProcessingMatching(RoundEnvironment roundEnv) {
        Elements elements = processingEnv.getElementUtils();
        Set<? extends Element> matchingSet = roundEnv.getElementsAnnotatedWith(IfMatching.class);
        MatchingHolder matchingHolder = holders.getMatchingHolder();
        for (Element annotated : matchingSet) {
            TypeElement matchingElem = (TypeElement) annotated;
            Logger2.warn(matchingElem);
            IfMatching matching = annotated.getAnnotation(IfMatching.class);
            String matcherClass = Element2.getClassname(matching, IfMatching::value);
            TypeElement matcherElem = elements.getTypeElement(matcherClass);
            matchingHolder.with(matchingElem, matcherElem, matching);
        }
    }

    private void doProcessingMapper(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(MapperFor.class).forEach(annotated -> {
            TypeElement mapperForAnnotated = (TypeElement) annotated;
            MapperFor mapperFor = annotated.getAnnotation(MapperFor.class);
            Collection<TypeElement> forValues = getMapperForValues(mapperForAnnotated);
            for (TypeElement forElement : forValues) {
                holders.getMapperHolder().with(mapperFor, mapperForAnnotated, forElement);
            }
        });
    }

    private void doProcessingTableModel(RoundEnvironment roundEnv) {
        Set<? extends Element> models = roundEnv.getElementsAnnotatedWith(TableModel.class);
        models.forEach(element -> holders.getTypeHolder().with((TypeElement) element));
    }

    private void doProcessingAccessor(RoundEnvironment roundEnv) {
        Set<? extends Element> accessors = roundEnv.getElementsAnnotatedWith(Accessor.class);
        accessors.forEach(element -> holders.getAccessorHolder().with((TypeElement) element));
    }

    private void doWriteJavaFiles() {
        holders.writeJavaFile(new JavaFiler());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        holders.init(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(Accessor.class.getCanonicalName());
        supportedTypes.add(MapperFor.class.getCanonicalName());
        supportedTypes.add(TableModel.class.getCanonicalName());
        return supportedTypes;
    }
}
