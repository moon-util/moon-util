package com.moon.processing.accessor;

import com.moon.processing.decl.AccessorDeclared;
import com.moon.processing.decl.MethodDeclared;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.holder.Holders;

/**
 * @author benshaoye
 */
public class DeclaredSelectImplementor extends DeclaredBaseImplementor {
    protected DeclaredSelectImplementor(
        Holders holders, AccessorDeclared accessorDeclared, FileClassImpl impl
    ) {
        super(holders, accessorDeclared, impl);
    }

    @Override
    public void doImplMethod(MethodDeclared methodDeclared) {

    }
}
