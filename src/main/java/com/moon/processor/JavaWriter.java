package com.moon.processor;

import com.moon.processor.holder.Closer;
import com.moon.processor.model.ThrowingConsumer;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * @author benshaoye
 */
public class JavaWriter {

    private final static byte[] BYTES = {};
    private final Map<String, Object> written = new ConcurrentHashMap<>();
    private final Filer filer;

    public JavaWriter(Filer filer) { this.filer = filer; }

    private Filer getFiler() { return filer; }

    /**
     * 写
     *
     * @param fullClassname com.example.ClassName
     * @param consumer
     */
    public void write(String fullClassname, ThrowingConsumer<Writer> consumer) {
        if (written.containsKey(fullClassname)) {
            return;
        }
        written.put(fullClassname, BYTES);
        try (Writer jw = getFiler().createSourceFile(fullClassname).openWriter();
             PrintWriter writer = new PrintWriter(jw)) {
            consumer.accept(writer);
        } catch (/* IOException */Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeResourceFile(String resourceName, Collection<String> values) {
        String resourceFile = "META-INF/services/" + resourceName;
        Set<String> valuesOfAll = new LinkedHashSet<>();
        try {
            FileObject existed = filer.getResource(CLASS_OUTPUT, "", resourceFile);
            valuesOfAll.addAll(getPresentSessionImpls(existed));
        } catch (IOException ignored) { }

        if (valuesOfAll.containsAll(values)) {
            return;
        }

        valuesOfAll.addAll(values);

        FileObject fileObject;
        try {
            fileObject = filer.createResource(CLASS_OUTPUT, "", resourceFile);
        } catch (/* IOException */Throwable e) {
            throw new IllegalStateException(e);
        }

        try (OutputStream out = fileObject.openOutputStream();
             OutputStreamWriter outWriter = new OutputStreamWriter(out, UTF_8);
             BufferedWriter writer = new BufferedWriter(outWriter)) {
            for (String service : valuesOfAll) {
                writer.write(service);
                writer.newLine();
            }
            writer.flush();
        } catch (/* IOException */Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private static Set<String> getPresentSessionImpls(FileObject fileObject) {
        try {
            return readAll(fileObject, JavaWriter::getPresentSessionImpls);
        } catch (IOException ignored) {
            return Collections.emptySet();
        }
    }

    private static Set<String> getPresentSessionImpls(InputStream input) throws IOException {
        Set<String> sessionImpls = new LinkedHashSet<>();
        try (Closer closer = Closer.of()) {
            InputStreamReader reader = closer.add(new InputStreamReader(input, UTF_8));
            BufferedReader r = closer.add(new BufferedReader(reader));
            String line;
            while ((line = r.readLine()) != null) {
                int hasCommentIdx = line.indexOf('#');
                if (hasCommentIdx >= 0) {
                    line = line.substring(0, hasCommentIdx);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    sessionImpls.add(line);
                }
            }
            return sessionImpls;
        } catch (IOException e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private static <T> T readAll(FileObject existingFile, ThrowingApplier<InputStream, T> inputConsumer)
        throws IOException {
        try (InputStream is = existingFile.openInputStream()) {
            return inputConsumer.apply(is);
        }
    }

    private interface ThrowingApplier<P, R> {

        R apply(P p) throws IOException;
    }
}
