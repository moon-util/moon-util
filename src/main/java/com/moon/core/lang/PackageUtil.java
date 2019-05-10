package com.moon.core.lang;

import static com.moon.core.enums.ArraysEnum.STRINGS;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class PackageUtil {

    private PackageUtil() { noInstanceError(); }

    public static PackageScanner scanner() { return new PackageScanner(); }

    public static String[] scan(String packageName) {
        return PackageScanner.scanOf(packageName).toArray(STRINGS.empty());
    }
}
