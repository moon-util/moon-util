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
        EnvironmentUtils.initialize(processingEnv);
        super.init(processingEnv);
    }

    private void doWriteJavaFile(List<MappingForDetail> models) throws IOException {
        for (MappingForDetail model : models) {
            model.writeJavaFile();
        }
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        List<MappingForDetail> models = getMappingModels(annotations, roundEnv);
        if (!models.isEmpty()) {
            try {
                doWriteJavaFile(models);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private List<MappingForDetail> getMappingModels(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        List<MappingForDetail> models = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(SUPPORTED_TYPE)) {
                for (Element element :  env.getElementsAnnotatedWith(MappingFor.class)) {
                    if (element instanceof TypeElement) {
                        models.add(onAnnotatedMapping((TypeElement) element));
                    }
                }
            }
        }
        return models;
    }

    private MappingForDetail onAnnotatedMapping(final TypeElement thisElement) {
        final Elements utils = EnvironmentUtils.getUtils();
        final Map<String, DefinitionDetail> mappingForDetailsMap = new HashMap<>(4);
        final Collection<String> classes = ProcessUtils.getMappingForClasses(thisElement);
        final MappingForDetail mappingResult = new MappingForDetail(thisElement, mappingForDetailsMap);
        mappingResult.putAll(ProcessUtils.toPropertiesMap(thisElement));
        if (classes.isEmpty()) {
            return mappingResult;
        }
        for (String thatClassname : classes) {
            TypeElement target = utils.getTypeElement(thatClassname);
            if (target == null) {
                continue;
            }
            mappingForDetailsMap.put(thatClassname, ProcessUtils.toPropertiesMap(target));
        }
        return mappingResult;
    }
}
