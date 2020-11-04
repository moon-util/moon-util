package com.moon.mapping.processing;

import com.google.auto.service.AutoService;
import com.moon.mapping.annotation.MappingFor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
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
                warn(annotation.getQualifiedName() + "2" + set.size());
                for (Element element : set) {
                    if (element instanceof TypeElement) {
                        TypeElement typed = (TypeElement) element;
                        models.add(onAnnotatedMapping(typed));
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
        Elements elements = env().getElementUtils();
        Map<String, Map<String, PropertyModel>> targetModelsMap = new HashMap<>(4);
        Map<String, PropertyModel> modelMap = toPropertiesMap(elements, element);
        for (String classname : classes) {
            TypeElement annotated = elements.getTypeElement(classname);
            targetModelsMap.put(classname, toPropertiesMap(elements, annotated));
        }
        return new MappingForModel(elementName, modelMap, targetModelsMap);
    }

    private void warn(Object obj) {
        env().getMessager().printMessage(Diagnostic.Kind.WARNING, obj == null ? null : obj.toString());
    }


    private ProcessingEnvironment env() { return processingEnv; }
}
