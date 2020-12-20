package com.moon.mapper.processing;

import com.google.auto.service.AutoService;
import com.moon.mapper.annotation.MapperFor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.*;

import static com.moon.mapper.processing.ProcessUtils.toPropertiesMap;

/**
 * @author moonsky
 */
@AutoService(Processor.class)
public class MappingProcessor extends AbstractProcessor {

    private final static String SUPPORTED_TYPE = MapperFor.class.getName();

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(SUPPORTED_TYPE);
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        EnvUtils.initialize(processingEnv);
        super.init(processingEnv);
    }

    private void doWriteJavaFile(List<MappingWriter> models) throws IOException {
        Filer filer = EnvUtils.getEnv().getFiler();
        for (MappingWriter model : models) {
            model.writeJavaFile(filer);
        }
        MappingWriter.forAutoConfig(filer, models);
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        List<MappingWriter> models = getMappingModels(annotations, roundEnv);
        if (!models.isEmpty()) {
            try {
                doWriteJavaFile(models);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private List<MappingWriter> getMappingModels(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        final List<MappingWriter> models = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(SUPPORTED_TYPE)) {
                for (Element element : env.getElementsAnnotatedWith(MapperFor.class)) {
                    if (DetectUtils.isMappableElement(element)) {
                        models.add(onAnnotatedClass((TypeElement) element));
                    }
                }
            }
        }
        return models;
    }

    private MappingWriter onAnnotatedClass(final TypeElement thisElement) {
        final Elements utils = EnvUtils.getUtils();
        final Map<String, BasicDefinition> mappingForDetailsMap = new HashMap<>(4);
        final boolean converter = thisElement.getAnnotation(MapperFor.class).converter();
        final Collection<String> classes = ProcessUtils.getMappingForClasses(thisElement);
        BasicDefinition thisDefined = toPropertiesMap(thisElement, converter);
        MappingWriter writable = new MappingWriter(thisDefined, mappingForDetailsMap);
        if (classes.isEmpty()) {
            return writable;
        }
        for (String thatClassname : classes) {
            TypeElement target = utils.getTypeElement(thatClassname);
            if (!DetectUtils.isMappableElement(target)) {
                continue;
            }
            mappingForDetailsMap.put(thatClassname, toPropertiesMap(target, converter));
        }
        return writable;
    }
}
