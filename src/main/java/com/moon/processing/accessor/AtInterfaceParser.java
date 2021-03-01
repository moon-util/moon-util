package com.moon.processing.accessor;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.decl.*;
import com.moon.processing.file.BaseImplementation;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.holder.TableHolder;
import com.moon.processing.holder.TypeHolder;
import com.moon.processing.util.Element2;
import com.moon.processing.util.Processing2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AtInterfaceParser extends BaseGenerator {

    protected final TypeHolder typeHolder;
    protected final TableHolder tableHolder;
    protected final TypeElement accessorElement;
    protected final String accessorClassname;
    protected final AccessorDeclared accessorDeclared;
    protected final TypeDeclared accessorTypeDeclared;
    protected final TableDeclared tableDeclared;
    protected final FileClassImpl impl;

    protected final Map<String, GenericDeclared> thisGenericMap;
    protected final Elements utils;
    protected final Types types;

    public AtInterfaceParser(FileClassImpl impl, AccessorDeclared declared) {
        super(impl.getImporter());
        TypeDeclared accessorTypeDeclared = declared.getTypeDeclared();
        this.thisGenericMap = accessorTypeDeclared.getGenericDeclaredMap();
        this.accessorClassname = declared.getAccessorClassname();
        this.accessorElement = declared.getAccessorElement();
        this.accessorTypeDeclared = accessorTypeDeclared;
        this.tableHolder = declared.tableHolder();
        this.typeHolder = declared.typeHolder();
        this.tableDeclared = declared.getTableDeclared();
        this.accessorDeclared = declared;
        this.utils = Processing2.getUtils();
        this.types = Processing2.getTypes();
        this.impl = impl;
    }

    @Override
    protected final BaseImplementation getImplementation() { return impl; }

    public void doGenerate() {
        List<Runnable> runners = new ArrayList<>();
        accessorTypeDeclared.getAllMethodsDeclared().forEach(declared -> {
            ExecutableElement element = declared.getMethod();
            if (Test2.isAny(element, Modifier.DEFAULT)) {
                return;
            }
            Provided provided = element.getAnnotation(Provided.class);
            if (provided != null) {
                // Provided 有优先权，但要后处理
                runners.add(() -> onProvidedAnnotated(declared, provided));
                return;
            }
            String methodName = Element2.getSimpleName(element);
            if (doParsingOnAnnotated(methodName, element)) {
                // TODO SQL注解
                return;
            }
            // TODO 方法名解析
            getDeclaredImplementor(declared).doImplMethod(declared);
        });
        runners.forEach(Runnable::run);
    }

    private DeclaredImplementor getDeclaredImplementor(MethodDeclared methodDeclared) {
        switch (String2.firstWord(methodDeclared.getMethodName()).toLowerCase()) {
            case "insert":
                return new DeclaredInsertImplementor(accessorDeclared.getHolders(), accessorDeclared, impl);
            case "modify":
            case "update":
                return new DeclaredUpdateImplementor(accessorDeclared.getHolders(), accessorDeclared, impl);
            case "remove":
            case "delete":
                return new DeclaredDeleteImplementor(accessorDeclared.getHolders(), accessorDeclared, impl);
            case "find":
            case "read":
            case "fetch":
            case "select":
            case "search":
                return new DeclaredSelectImplementor(accessorDeclared.getHolders(), accessorDeclared, impl);
            default:
                throw new UnsupportedOperationException(methodDeclared.getMethodName());
        }
    }

    private boolean doParsingOnAnnotated(String methodName, ExecutableElement element) {
        return false;
    }

    @SuppressWarnings("all")
    private DeclaredImplementor doParsingWithDeclared(MethodDeclared methodDeclared) {
        switch (String2.firstWord(methodDeclared.getMethodName()).toLowerCase()) {
            case "insert":
            case "modify":
            case "update":
            case "delete":
            case "remove":
            case "find":
            case "read":
            case "query":
            case "fetch":
            case "select":
            case "search":
                //
            case "get":
            case "save":
            case "count":
            case "exists":
            case "existing":
            default:
                return null;
        }
    }
}
