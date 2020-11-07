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

import static com.moon.mapping.processing.ProcessingUtil.toPropertiesModelMap;

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
        Logger.initialize(processingEnv);
        super.init(processingEnv);
    }

    private void doWriteJavaFile(List<FromCToCModel> models) throws IOException {
        Filer filer = env().getFiler();
        for (FromCToCModel model : models) {
            model.writeJavaFile(filer);
        }
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        List<FromCToCModel> models = getMappingModels(annotations, roundEnv);
        if (!models.isEmpty()) {
            try {
                doWriteJavaFile(models);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private List<FromCToCModel> getMappingModels(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        List<FromCToCModel> models = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(SUPPORTED_TYPE)) {
                Set<? extends Element> set = env.getElementsAnnotatedWith(MappingFor.class);
                for (Element element : set) {
                    if (element instanceof TypeElement) {
                        models.add(onAnnotatedMapping((TypeElement) element));
                    }
                }
            }
        }
        return models;
    }

    @SuppressWarnings("all")
    private FromCToCModel onAnnotatedMapping(final TypeElement thisElement) {
        final String thisClassname = thisElement.getQualifiedName().toString();
        final ProcessingEnvironment env = env();
        final Elements utils = env.getElementUtils();
        Map<String, MappedPropsMap> targetsMap = new HashMap<>(4);
        MappedPropsMap thisModel = toPropertiesModelMap(env(), thisElement);
        Collection<String> classes = ProcessingUtil.getMappingForClasses(thisElement);
        FromCToCModel model = new FromCToCModel(env(), thisClassname, thisModel, targetsMap);
        if (classes.isEmpty()) {
            return model;
        }
        for (String thatClassname : classes) {
            TypeElement target = utils.getTypeElement(thatClassname);
            if (target == null) {
                continue;
            }
            targetsMap.put(thatClassname, toPropertiesModelMap(env(), target));
        }
        return model;
    }

    private ProcessingEnvironment env() { return processingEnv; }
}
