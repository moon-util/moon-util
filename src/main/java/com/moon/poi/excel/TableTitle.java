package com.moon.poi.excel;

/**
 * @author moonsky
 */
public class TableTitle {

    private final String title;
    private final int height;
    private final String classname;

    private TableTitle(String title, int height, String classname) {
        this.title = title;
        this.height = height;
        this.classname = classname;
    }

    public static TableTitle of(String title) {
        return of(title, -1);
    }

    public static TableTitle of(String title, int height) {
        return of(title, height, null);
    }

    public static TableTitle of(String title, String classname) {
        return of(title, -1, classname);
    }

    public static TableTitle of(String title, int height, String classname) {
        return new TableTitle(title, height, classname);
    }

    public String getTitle() {
        return title;
    }

    public int getHeight() {
        return height;
    }

    public String getClassname() {
        return classname;
    }
}
