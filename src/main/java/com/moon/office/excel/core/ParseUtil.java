package com.moon.office.excel.core;

import com.moon.core.lang.StringUtil;

import java.lang.annotation.Annotation;

import static com.moon.core.enums.ArraysEnum.STRINGS;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class ParseUtil {
    private ParseUtil() {
        noInstanceError();
    }

    final static CenterRenderer parseExcel(TableExcel annotation) {
        return new TrueExcelRenderer(annotation, parseSheets(annotation));
    }

    private final static CenterRenderer[] parseSheets(TableExcel annotation) {
        TableSheet[] sheets = annotation.value();
        final int length = sheets.length;
        CenterRenderer[] children = new CenterRenderer[length];
        for (int i = 0; i < length; i++) {
            children[i] = parseItems(sheets[i], Handlers.SHEET);
        }
        return children;
    }

    private final static CenterRenderer[] parseRows(TableSheet annotation) {
        TableRow[] rows = annotation.value();
        final int length = rows.length;
        CenterRenderer[] children = new CenterRenderer[length];
        for (int i = 0; i < length; i++) {
            children[i] = parseItems(rows[i], Handlers.ROW);
        }
        return children;
    }

    private final static CenterRenderer[] parseCells(TableRow annotation) {
        TableCell[] cells = annotation.value();
        final int length = cells.length;
        CenterRenderer[] children = new CenterRenderer[length];
        for (int i = 0; i < length; i++) {
            children[i] = parseItems(cells[i], Handlers.CELL);
        }
        return children;
    }

    private final static CenterRenderer[] parseValues(TableCell cell) {
        return CenterRenderer.EMPTY;
    }

    private interface AnnotationHandler {
        <T> T to(Annotation annotation);

        String when(Annotation annotation);

        String[] delimiters(Annotation annotation);

        CenterRenderer renderTrue(Annotation annotation, String[] formatted);

        CenterRenderer renderTable(Annotation annotation, String[] formatted);
    }

    private enum Handlers implements AnnotationHandler {
        SHEET {
            @Override
            public TableSheet to(Annotation annotation) {
                return (TableSheet) annotation;
            }

            @Override
            public String[] delimiters(Annotation annotation) {
                return to(annotation).delimiters();
            }

            @Override
            public String when(Annotation annotation) {
                return to(annotation).when();
            }

            @Override
            public CenterRenderer renderTrue(Annotation annotation, String[] formatted) {
                TableSheet current = to(annotation);
                return new TrueSheetRenderer(current, parseRows(current), formatted);
            }

            @Override
            public CenterRenderer renderTable(Annotation annotation, String[] formatted) {
                TableSheet current = to(annotation);
                return new WhenSheetRenderer(current, parseRows(current), formatted);
            }
        },
        ROW {
            @Override
            public TableRow to(Annotation annotation) {
                return (TableRow) annotation;
            }

            @Override
            public String[] delimiters(Annotation annotation) {
                return to(annotation).delimiters();
            }

            @Override
            public String when(Annotation annotation) {
                return to(annotation).when();
            }

            @Override
            public CenterRenderer renderTrue(Annotation annotation, String[] formatted) {
                TableRow current = to(annotation);
                return new TrueRowRenderer(current, parseCells(current), formatted);
            }

            @Override
            public CenterRenderer renderTable(Annotation annotation, String[] formatted) {
                TableRow current = to(annotation);
                return new WhenRowRenderer(current, parseCells(current), formatted);
            }
        },
        CELL {
            @Override
            public TableCell to(Annotation annotation) {
                return (TableCell) annotation;
            }

            @Override
            public String[] delimiters(Annotation annotation) {
                return to(annotation).delimiters();
            }

            @Override
            public String when(Annotation annotation) {
                return to(annotation).when();
            }

            @Override
            public CenterRenderer renderTrue(Annotation annotation, String[] formatted) {
                TableCell current = to(annotation);
                return new TrueCellRenderer(current, CenterRenderer.EMPTY, formatted);
            }

            @Override
            public CenterRenderer renderTable(Annotation annotation, String[] formatted) {
                TableCell current = to(annotation);
                return new WhenCellRenderer(current, CenterRenderer.EMPTY, formatted);
            }
        },
    }

    private final static String TRUE = Boolean.TRUE.toString();
    private final static String FALSE = Boolean.FALSE.toString();
    private final static int TWO = 2;

    private final static <T extends Annotation> CenterRenderer parseItems(T annotation, AnnotationHandler handler) {
        String[] delimiters = handler.delimiters(annotation);
        String when = handler.when(annotation).trim();
        if (delimiters.length < TWO) {
            if (TRUE.equals(when)) {
                return handler.renderTrue(annotation, STRINGS.empty());
            } else if (FALSE.equals(when)) {
                return TrueNull.NULL;
            } else {
                throw new IllegalArgumentException(when);
            }
        } else {
            int index = 0;
            String[] formatted = new String[TWO];
            formatted[index] = StringUtil.requireNotEmpty(delimiters[index++].trim());
            formatted[index] = StringUtil.requireNotEmpty(delimiters[index++].trim());
            String cutted = cutWhenWrapped(when, formatted);
            if (TRUE.equals(cutted)) {
                return handler.renderTrue(annotation, STRINGS.empty());
            } else if (FALSE.equals(cutted)) {
                return TrueNull.NULL;
            } else {
                return handler.renderTable(annotation, formatted);
            }
        }
    }

    private final static String cutWhenWrapped(String when, String[] formattedDelimiters) {
        try {
            return OtherUtil.cutWrapped(when, formattedDelimiters);
        } catch (Exception e) {
            if (TRUE.equals(when) || FALSE.equals(when)) {
                return when;
            }
            throw e;
        }
    }
}
