package com.moon.mapping.processing;

import com.google.auto.service.AutoService;
import com.moon.mapping.annotation.MappingFor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.*;

import static com.moon.mapping.processing.ProcessUtils.toPropertiesMap;

/**
 * @author moonsky
 */
@AutoService(Processor.class)
public class MappingProcessor extends AbstractProcessor {

    private final static String SUPPORTED_TYPE = MappingFor.class.getName();

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.RELEASE_8; }

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

    private void doWriteJavaFile(List<JavaFileWritable> models) throws IOException {
        for (JavaFileWritable model : models) {
            model.writeJavaFile(EnvUtils.getEnv().getFiler());
        }
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        List<JavaFileWritable> models = getMappingModels(annotations, roundEnv);
        if (!models.isEmpty()) {
            try {
                doWriteJavaFile(models);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private List<JavaFileWritable> getMappingModels(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        final List<JavaFileWritable> models = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(SUPPORTED_TYPE)) {
                for (Element element :  env.getElementsAnnotatedWith(MappingFor.class)) {
                    if (element instanceof TypeElement) {
                        models.add(onAnnotatedMappingFor((TypeElement) element));
                    }
                }
            }
        }
        return models;
    }

    private JavaFileWritable onAnnotatedMappingFor(final TypeElement thisElement) {
        if (thisElement.getKind().isInterface()) {
            return onAnnotatedInter(thisElement);
        }
        return onAnnotatedClass(thisElement);
    }

    private JavaFileWritable onAnnotatedInter(final TypeElement thisElement) {
        InterDefinition definition = InterfaceUtils.toPropertiesMap(thisElement);
        final Map<String, BasicDefinition> mappingForDetailsMap = new HashMap<>(4);
        return new MappingWriter(definition, mappingForDetailsMap);
    }

    private JavaFileWritable onAnnotatedClass(final TypeElement thisElement) {
        final Elements utils = EnvUtils.getUtils();
        final Map<String, BasicDefinition> mappingForDetailsMap = new HashMap<>(4);
        final Collection<String> classes = ProcessUtils.getMappingForClasses(thisElement);
        BasicDefinition thisDefined = toPropertiesMap(thisElement);
        MappingWriter writable = new MappingWriter(thisDefined, mappingForDetailsMap);
        if (classes.isEmpty()) {
            return writable;
        }
        for (String thatClassname : classes) {
            TypeElement target = utils.getTypeElement(thatClassname);
            if (target == null) {
                continue;
            }
            mappingForDetailsMap.put(thatClassname, toPropertiesMap(target));
        }
        return writable;
    }
}
