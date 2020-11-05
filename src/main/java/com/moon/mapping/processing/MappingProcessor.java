package com.moon.mapping.processing;

import com.google.auto.service.AutoService;
import com.moon.mapping.annotation.MappingFor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import static com.moon.mapping.processing.ProcessingUtil.toPropertiesMap;

/**
 * @author moonsky
 */
@AutoService(Processor.class)
public class MappingProcessor extends AbstractProcessor {

    private final static String SUPPORTED_TYPE = MappingFor.class.getName();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(SUPPORTED_TYPE);
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnv
    ) {
        List<MappingForModel> models = getMappingModels(annotations, roundEnv);
        if (!models.isEmpty()) {
            try {
                doWriteJavaFile(models);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void doWriteJavaFile(List<MappingForModel> models) throws IOException {
        Filer filer = env().getFiler();
        for (MappingForModel model : models) {
            JavaFileObject java = filer.createSourceFile(model.getGeneratedMappingName());
            try (Writer javaWriter = java.openWriter(); PrintWriter writer = new PrintWriter(javaWriter)) {
                writer.println(model.toString());
                writer.flush();
            }
        }
    }

    private List<MappingForModel> getMappingModels(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        List<MappingForModel> models = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(SUPPORTED_TYPE)) {
                Set<? extends Element> set = env.getElementsAnnotatedWith(MappingFor.class);
                for (Element element : set) {
                    if (element instanceof TypeElement) {
                        TypeElement thisTyped = (TypeElement) element;
                        Types types = getTypeUtils();
                        TypeMirror superclass = thisTyped.getSuperclass();
                        TypeElement superTyped = cast(types.asElement(superclass));

                        List<GenericModel> genericModels = GenericUtil.parse(superclass, superTyped);

                        models.add(onAnnotatedMapping(thisTyped));
                    }
                }
            }
        }
        return models;
    }

    private MappingForModel onAnnotatedMapping(TypeElement element) {
        Collection<String> classes = ProcessingUtil.getMappingForClasses(element);
        if (classes.isEmpty()) {
            return null;
        }
        String elementName = element.getQualifiedName().toString();
        Elements utils = env().getElementUtils();
        Map<String, Map<String, PropertyModel>> targetModelsMap = new HashMap<>(4);
        Map<String, PropertyModel> modelMap = toPropertiesMap(utils, element);
        for (String classname : classes) {
            TypeElement annotated = utils.getTypeElement(classname);
            targetModelsMap.put(classname, toPropertiesMap(utils, annotated));
        }
        return new MappingForModel(elementName, modelMap, targetModelsMap);
    }

    private void warn(Object obj) {
        env().getMessager().printMessage(Diagnostic.Kind.WARNING, obj == null ? null : obj.toString());
    }

    private Messager getMessager() { return env().getMessager(); }

    private Types getTypeUtils() { return env().getTypeUtils(); }

    private Elements getElementUtils() { return env().getElementUtils(); }

    private ProcessingEnvironment env() { return processingEnv; }

    private static <T> T cast(Object obj) {
        return (T) obj;
    }
}