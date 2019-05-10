package com.moon.office.excel.core;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.annotation.AnnotationUtil;
import com.moon.core.lang.reflect.MethodUtil;
import com.moon.office.excel.ExcelUtil;
import com.moon.office.excel.Renderer;
import com.moon.core.util.CollectUtil;
import com.moon.core.util.Console;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public class RendererUtil {
    protected RendererUtil() {
        noInstanceError();
    }

    private final static String NAME = ExcelUtil.class.getName();
    private final static String NAME0 = RendererUtil.class.getName();

    private final static Map<TableExcel, CenterRenderer> CACHE = new HashMap<>();

    private final static TableExcel getAnnotation() {
        int foundCount = 0;
        String TARGET_NAME = NAME, className, methodName, foundName = null;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 2, len = elements.length; i < len; i++) {
            StackTraceElement element = elements[i];
            className = element.getClassName();
            if (foundCount > 0) {
                if (foundCount == 1) {
                    foundName = className;
                }
                if (TARGET_NAME.equals(className)) {
                    foundCount++;
                    continue;
                }
                methodName = element.getMethodName();
                if (methodName.matches("lambda\\$.*\\$\\d+")) {
                    foundCount++;
                    continue;
                }
                List<Method> methods = MethodUtil.getAllMethods(ClassUtil.forName(className), methodName);
                if (CollectUtil.isNotEmpty(methods)) {
                    Method method = methods.get(0);
                    TableExcel excel = AnnotationUtil.get(method, TableExcel.class);
                    if (excel == null) {
                        throw new NotExistTableExcelException(foundName + '.' + methodName);
                    }
                    return excel;
                }
                foundCount++;
            } else if (TARGET_NAME.equals(className)) {
                foundCount++;
            }
        }
        throw new NotExistTableExcelException(foundName);
    }

    private final static TableExcel getInstanceAnnotation() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement elem = elements[i];
            String className = elem.getClassName();
            if (!NAME.equals(className) && !NAME0.equals(className)) {
                String methodName = elem.getMethodName();
                switch (methodName) {
                    case "<init>":
                        break;
                    case "<clinit>":
                        break;
                    default:
                        break;
                }
                Console.out.println(className + '.' + methodName);
                break;
            }
        }

        throw new NotExistTableExcelException("can not found Annotation of: " + NAME);
    }

    final static CenterRenderer getOrParse(TableExcel excel) {
        CenterRenderer renderer = CACHE.get(excel);
        if (renderer == null) {
            renderer = ParseUtil.parseExcel(excel);
            synchronized (CACHE) {
                if (CACHE.get(excel) == null) {
                    CACHE.put(excel, renderer);
                }
            }
        }
        return renderer;
    }

    final static Renderer getInstance() {
        return new GenericRenderer(getInstanceAnnotation());
    }

    protected final static Workbook parseAndRenderTo(Workbook workbook, Object... data) {
        TableExcel excel = getAnnotation();
        CenterRenderer renderer = getOrParse(excel);
        WorkCenterMap centerMap = new WorkCenterMap(
            workbook == null ? excel.type().get() : workbook, data);
        return renderer.render(centerMap).get();
    }
}
