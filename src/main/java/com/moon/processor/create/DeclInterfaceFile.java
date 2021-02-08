package com.moon.processor.create;

import com.moon.processor.holder.Importer;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class DeclInterfaceFile extends DeclAnnotable {

    private final String packageName;
    private final String simpleName;

    private final Set<String> interfacesSet = new LinkedHashSet<>();
    private final Set<DeclGeneric> generics = new LinkedHashSet<>();

    public DeclInterfaceFile(String packageName, String simpleName) {
        super(new Importer(packageName));
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    public String getClassname() {
        return String.join(".", packageName, simpleName);
    }

    public DeclInterfaceFile implement(Class<?>... interfaces) {
        for (Class<?> aClass : interfaces) {
            this.interfacesSet.add(aClass.getCanonicalName());
        }
        return this;
    }

    public DeclInterfaceFile implement(String... interfaces) {
        for (String aClass : interfaces) {
            this.interfacesSet.add(aClass);
        }
        return this;
    }

    public DeclInterfaceFile addGenericWith(String name) {
        generics.add(new DeclGeneric(getImporter(), name));
        return this;
    }

    public DeclInterfaceFile addGenericWith(String name, Consumer<DeclGeneric> genericUsing) {
        DeclGeneric generic = new DeclGeneric(getImporter(), name);
        genericUsing.accept(generic);
        generics.add(generic);
        return this;
    }

    @Override
    public Set<Modifier> getModifiers() { return Filer2.FOR_INTERFACE; }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Filer2.FOR_INTERFACE; }
}
