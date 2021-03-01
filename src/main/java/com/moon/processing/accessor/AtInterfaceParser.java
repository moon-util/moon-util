package com.moon.processing.accessor;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.decl.AccessorDeclared;
import com.moon.processing.decl.MethodDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processing.file.BaseImplementation;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public class AtInterfaceParser extends BaseGenerator {

    protected final AccessorDeclared accessorDeclared;
    protected final TypeDeclared accessorTypeDeclared;
    protected final FileClassImpl impl;

    public AtInterfaceParser(FileClassImpl impl, AccessorDeclared declared) {
        super(impl.getImporter());
        this.accessorTypeDeclared = declared.getTypeDeclared();
        this.accessorDeclared = declared;
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
        Holders holders = accessorDeclared.getHolders();
        switch (String2.firstWord(methodDeclared.getMethodName()).toLowerCase()) {
            case "insert":
                return new DeclaredInsertImplementor(holders, accessorDeclared, impl);
            case "modify":
            case "update":
                return new DeclaredUpdateImplementor(holders, accessorDeclared, impl);
            case "remove":
            case "delete":
                return new DeclaredDeleteImplementor(holders, accessorDeclared, impl);
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
