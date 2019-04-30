package com.moon.core.lang;

import com.moon.core.util.FilterUtil;
import com.moon.core.util.IteratorUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

/**
 * 包扫描器
 *
 * @author benshaoye
 */
public class PackageScanner extends HashSet<String> {

    private final static String DOT_CLASS = ".class";

    PackageScanner() {
    }

    public PackageScanner scan(String packageName) {
        this.addAll(scanOf(packageName));
        return this;
    }

    static List<String> scanOf(String packageName) {
        final String currentName = packageName.replaceAll("\\.", "/");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> urls = cl.getResources(currentName);
            ArrayList<String> result = new ArrayList<>();
            IteratorUtil.forEach(urls, url -> result.addAll(scanFromUrl(url, currentName)));
            return result;
        } catch (IOException e) {
            return ThrowUtil.doThrow(e);
        }
    }

    private static List<String> scanFromUrl(URL currUrl, String packageName) {
        URL tempUrl;
        try {
            tempUrl = new URL(URLDecoder.decode(currUrl.toString(), "UTF-8"));
        } catch (Exception e) {
            tempUrl = ThrowUtil.doThrow(e);
        }
        final URL url = tempUrl;
        final String jar = "jar", file = "file", protocol = url.getProtocol();
        FileSystemProvider provider;
        if (protocol.equals(jar)) {
            provider = getZipFSProvider();
            if (provider != null) {
                String target = url.getPath().replaceFirst("file:/", "").replaceFirst("!.*", "");
                try (FileSystem fs = provider.newFileSystem(Paths.get(target), new HashMap<>())) {
                    return walkFileTree(fs.getPath(packageName), null);
                } catch (Exception e) {
                    ThrowUtil.doThrow(e);
                }
            }
        } else if (protocol.equals(file)) {
            int end = url.getPath().lastIndexOf(packageName);
            String basePath = url.getPath().substring(1, end);
            try {
                return walkFileTree(Paths.get(url.getPath().replaceFirst("/", "")), Paths.get(basePath));
            } catch (Exception e) {
                ThrowUtil.doThrow(e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    private static List<String> walkFileTree(Path path, Path basePath) throws Exception {
        final List<String> result = new ArrayList<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            private String packageName = StringUtil.stringifyOrEmpty(basePath);

            @Override
            public FileVisitResult visitFile(Path arg, BasicFileAttributes attrs) {
                if (arg.toString().endsWith(DOT_CLASS)) {
                    String path = arg.toString();
                    path = path.replace(packageName, "");
                    path = path.substring(1);
                    path = path.replace('\\', '/');
                    path = path.replace(DOT_CLASS, "");
                    path = path.replace('/', '.');
                    result.add(path);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) {
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

    private static FileSystemProvider getZipFSProvider() {
        return FilterUtil.nullableFirst(FileSystemProvider.installedProviders(),
            provider -> "jar".equals(provider.getScheme()));
    }
}
