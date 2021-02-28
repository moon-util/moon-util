package com.moon.processing.accessor;

import com.moon.processing.decl.AccessorDeclared;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.holder.Holders;

/**
 * @author benshaoye
 */
public abstract class DeclaredBaseImplementor extends BaseImplementor implements DeclaredImplementor {

    protected DeclaredBaseImplementor(
        Holders holders, AccessorDeclared accessorDeclared, FileClassImpl impl
    ) {
        super(holders, accessorDeclared, impl);
    }
}
