package com.moon.processing.decl;

/**
 * 数据库访问对象
 *
 * @author benshaoye
 */
public class DataAccessorDeclared {

    /** 数据库访问对象 */
    private final TypeDeclared typeDeclared;
    /** 实体 */
    private final TypeDeclared pojoDeclared;
    /** 数据表 */
    private final TableDeclared tableDeclared;

    public DataAccessorDeclared(TypeDeclared typeDeclared, TableDeclared tableDeclared, TypeDeclared pojoDeclared) {
        this.tableDeclared = tableDeclared;
        this.typeDeclared = typeDeclared;
        this.pojoDeclared = pojoDeclared;
    }
}
