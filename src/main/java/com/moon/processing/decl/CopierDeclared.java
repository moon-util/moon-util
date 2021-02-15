package com.moon.processing.decl;

/**
 * @author benshaoye
 */
public class CopierDeclared {

    private final RecordDeclared thisDeclared;
    private final RecordDeclared thatDeclared;

    public CopierDeclared(RecordDeclared thisDeclared, RecordDeclared thatDeclared) {
        this.thisDeclared = thisDeclared;
        this.thatDeclared = thatDeclared;
    }
}
