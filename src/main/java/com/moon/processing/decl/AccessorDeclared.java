package com.moon.processing.decl;

import com.moon.processing.JavaDeclarable;

/**
 * 数据库访问对象
 *
 * @author benshaoye
 */
public class AccessorDeclared implements JavaDeclarable {

    /** 数据库访问对象 */
    private final TypeDeclared typeDeclared;
    /** 实体 */
    private final TypeDeclared pojoDeclared;
    /** 数据表 */
    private final TableDeclared tableDeclared;

    public AccessorDeclared(TypeDeclared typeDeclared, TableDeclared tableDeclared, TypeDeclared pojoDeclared) {
        this.tableDeclared = tableDeclared;
        this.typeDeclared = typeDeclared;
        this.pojoDeclared = pojoDeclared;
    }

    @Override
    public String getClassname() {
        return null;
    }
}
