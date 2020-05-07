package com.moon.more.excel;

/**
 * @author benshaoye
 */
public class TemplateRegion {

    private final int leftRow;
    private final int leftCol;

    private final int rightRow;
    private final int rightCol;

    public TemplateRegion(int leftRow, int leftCol, int rightRow, int rightCol) {
        this.leftRow = leftRow;
        this.leftCol = leftCol;
        this.rightRow = rightRow;
        this.rightCol = rightCol;
    }

    public static TemplateRegion of(int leftRow, int leftCol, int rightRow, int rightCol) {
        return new TemplateRegion(leftRow, leftCol, rightRow, rightCol);
    }

    public int getLeftRow() { return leftRow; }

    public int getLeftCol() { return leftCol; }

    public int getRightRow() { return rightRow; }

    public int getRightCol() { return rightCol; }
}
