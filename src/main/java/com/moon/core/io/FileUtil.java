package com.moon.core.io;

import com.moon.core.enums.Const;
import com.moon.core.lang.LangUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SupportUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.function.ThrowingConsumer;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.moon.core.lang.LangUtil.applyBi;
import static com.moon.core.lang.ThrowUtil.doThrow;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class FileUtil {
    private FileUtil() {
        ThrowUtil.noInstanceError();
    }

    /*
     * -----------------------------------------------------------------------
     * copies
     * -----------------------------------------------------------------------
     */

    /**
     * 将 sourceFilepath 绝对路径所指向的文件或目录复制到 targetDir
     *
     * @param sourceFilepath 源路径
     * @param targetDir      目标目录
     */
    public static void copyToDirectory(String sourceFilepath, String targetDir) {
        copyToDirectory(new File(sourceFilepath), new File(targetDir));
    }

    /**
     * 将 sourceFilepath 绝对路径所指向的文件复制到 targetDir，并将文件命名为 newFileName
     * 新文件名设置只针对复制文件有效，如果是复制文件夹，将使用原文件名
     *
     * @param sourceFilepath 源路径
     * @param targetDir      目标目录
     */
    public static void copyToDirectory(String sourceFilepath, String targetDir, String newFileName) {
        copyToFile(new File(sourceFilepath), new File(targetDir, newFileName));
    }

    public static void copyToDirectory(File sourceFile, final File targetDir) {
        if (sourceFile == null || targetDir == null) {
            return;
        } else if (sourceFile.isFile()) {
            copyToFile(sourceFile, new File(targetDir, sourceFile.getName()));
        } else if (sourceFile.isDirectory()) {
            String root = sourceFile.getParent();
            int len = root.length();
            IteratorUtil.forEach(traverseDirectory(sourceFile), file ->
                copyToFile(file, new File(targetDir, file.getAbsolutePath().substring(len))));
        }
    }

    public static void copyToFile(String sourceFilepath, String targetFilePath) {
        copyToFile(new File(sourceFilepath), new File(targetFilePath));
    }

    public static void copyToFile(File sourceFile, File targetFile) {
        if (exists(sourceFile)) {
            if (sourceFile.isDirectory()) {
                copyToDirectory(sourceFile, targetFile.getParentFile());
            } else if (sourceFile.isFile()) {
                try (FileOutputStream output = getOutputStream(targetFile);
                     FileInputStream input = getInputStream(sourceFile)) {
                    IOUtil.copy(input, output);
                } catch (IOException e) {
                    doThrow(e);
                }
            }
        }
    }

    /*
     * -----------------------------------------------------------------------
     * io
     * -----------------------------------------------------------------------
     */

    /**
     * 获取文件的输出流，如果文件不存在，会创建文件以及目录结构，创建失败返回空
     *
     * @param file
     */
    public static FileOutputStream getOutputStream(File file) {
        return getOutputStream(file, false);
    }

    public static FileOutputStream getOutputStream(String filePath) {
        return getOutputStream(filePath, false);
    }

    public static FileOutputStream getOutputStream(File file, boolean append) {
        return createNewFile(file) ? applyBi(file, append, FileOutputStream::new)
            : doThrow("File not exist: " + file);
    }

    public static FileOutputStream getOutputStream(String filePath, boolean append) {
        return getOutputStream(new File(StringUtil.trimToEmpty(filePath)), append);
    }

    public static FileInputStream getInputStream(String filePath) {
        return getInputStream(new File(StringUtil.trimToEmpty(filePath)));
    }

    /**
     * 从已知文件获取输入流，如不存在返回空
     *
     * @param file
     */
    public static FileInputStream getInputStream(File file) {
        return LangUtil.apply(file, FileInputStream::new);
    }

    /*
     * -----------------------------------------------------------------------
     * creates
     * -----------------------------------------------------------------------
     */

    public static boolean mkdirs(String path) {
        return mkdirs(new File(path));
    }

    public static boolean mkdirs(File dir) {
        if (dir.exists()) {
            if (dir.isFile()) {
                return doThrow("The target exist and is a file: " + dir);
            } else {
                return true;
            }
        } else {
            return dir.mkdirs();
        }
    }

    /**
     * 创建文件以其目录结构，返回创建成功与否的状态
     *
     * @param file
     */
    public static boolean createNewFile(File file) {
        if (!exists(file)) {
            File parent = file.getParentFile();
            if (parent.exists() || parent.mkdirs()) {
                try {
                    return file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean createNewFile(String filepath) {
        return createNewFile(new File(filepath));
    }

    /*
     * -----------------------------------------------------------------------
     * travellers
     * -----------------------------------------------------------------------
     */

    /**
     * 文件列表遍历器
     *
     * @return
     */
    public static FileTraveller traveller() {
        return new FileTraveller();
    }

    /**
     * 遍历指定目录的文件列表，如遇不可访问的安全保护会打印相应错误信息，但不会影响程序执行
     *
     * @param dirPath
     */
    public static List<File> traverseDirectory(String dirPath) {
        return traveller().traverse(dirPath);
    }

    public static List<File> traverseDirectory(File dirPath) {
        return traveller().traverse(dirPath);
    }

    public static List<File> traverseAll(File... dirs) {
        FileTraveller traveller = traveller();
        for (int i = 0; i < dirs.length; i++) {
            traveller.traverse(dirs[i]);
        }
        return traveller;
    }

    public static List<File> traverseAll(List<File> dirs) {
        FileTraveller traveller = traveller();
        for (int i = 0; i < dirs.size(); i++) {
            traveller.traverse(dirs.get(i));
        }
        return traveller;
    }

    /*
     * -----------------------------------------------------------------------
     * write or append lines
     * append：追加字符串行至文件末尾
     * write: 可手动控制是追加还是覆盖
     * -----------------------------------------------------------------------
     */

    private static ThrowingConsumer<Object> newLineWriter(Writer writer) {
        BufferedWriter bw = IOUtil.getBufferedWriter(writer);
        return line -> {
            bw.newLine();
            bw.write(String.valueOf(line));
        };
    }

    public static void writeLinesToWriter(Writer writer, Iterator<? extends Object> lines) {
        ThrowingConsumer<Object> consumer = newLineWriter(writer);
        IteratorUtil.forEach(lines, line -> LangUtil.accept(line, consumer));
    }

    public static void writeLinesToWriter(Writer writer, Collection<? extends Object> lines) {
        ThrowingConsumer<Object> consumer = newLineWriter(writer);
        IteratorUtil.forEach(lines, line -> LangUtil.accept(line, consumer));
    }

    public static void writeLinesToWriter(Writer writer, Object... lines) {
        ThrowingConsumer<Object> consumer = newLineWriter(writer);
        IteratorUtil.forEach(lines, line -> LangUtil.accept(line, consumer));
    }

    public static void writeLinesToOutput(OutputStream os, Iterator<CharSequence> lines) {
        IOUtil.autoCloseAccept(IOUtil.getWriter(os), w -> writeLinesToWriter(w, lines));
    }

    public static void writeLinesToOutput(OutputStream os, Collection<CharSequence> lines) {
        IOUtil.autoCloseAccept(IOUtil.getWriter(os), w -> writeLinesToWriter(w, lines));
    }

    public static void writeLinesToOutput(OutputStream os, CharSequence... lines) {
        IOUtil.autoCloseAccept(IOUtil.getWriter(os), w -> writeLinesToWriter(w, lines));
    }

    public static void appendLinesToFile(File existFile, Iterator<CharSequence> lines) {
        writeLinesToOutput(getOutputStream(existFile, true), lines);
    }

    public static void appendLinesToFile(File existFile, Collection<CharSequence> lines) {
        writeLinesToOutput(getOutputStream(existFile, true), lines);
    }

    public static void appendLinesToFile(File existFile, CharSequence... lines) {
        writeLinesToOutput(getOutputStream(existFile, true), lines);
    }

    public static void appendLinesToFile(String existFilePath, Iterator<CharSequence> lines) {
        appendLinesToFile(new File(existFilePath), lines);
    }

    public static void appendLinesToFile(String existFilePath, Collection<CharSequence> lines) {
        appendLinesToFile(new File(existFilePath), lines);
    }

    public static void appendLinesToFile(String existFilePath, CharSequence... lines) {
        appendLinesToFile(new File(existFilePath), lines);
    }

    /*
     * -----------------------------------------------------------------------
     * length
     * -----------------------------------------------------------------------
     */

    /**
     * 返回文件大小(单位 Bit)
     *
     * @param file
     * @return
     */
    public static long lengthToBit(File file) {
        return lengthToB(file) << 3;
    }

    /**
     * 返回文件大小(单位 B)
     *
     * @param filepath
     * @return
     */
    public static long length(String filepath) {
        return lengthToB(new File(filepath));
    }

    /**
     * 返回文件大小(单位 B)
     *
     * @param file
     * @return
     */
    public static long lengthToB(File file) {
        return file.length();
    }

    /**
     * 返回文件大小(单位 KB)
     *
     * @param file
     * @return
     */
    public static long lengthToKB(File file) {
        return lengthToB(file) >> 10;
    }

    /**
     * 返回文件大小(单位 MB)
     *
     * @param file
     * @return
     */
    public static long lengthToMB(File file) {
        return lengthToKB(file) >> 10;
    }

    /**
     * 返回文件大小(单位 GB)
     *
     * @param file
     * @return
     */
    public static long lengthToGB(File file) {
        return lengthToMB(file) >> 10;
    }

    /**
     * 返回文件大小(单位 TB)
     *
     * @param file
     * @return
     */
    public static long lengthToTB(File file) {
        return lengthToGB(file) >> 10;
    }

    /**
     * 返回文件大小(单位 PB)
     *
     * @param file
     * @return
     */
    public static long lengthToPB(File file) {
        return lengthToTB(file) >> 10;
    }

    /**
     * 返回文件大小(单位 EB)
     *
     * @param file
     * @return
     */
    public static long lengthToEB(File file) {
        return lengthToPB(file) >> 10;
    }

    /**
     * 返回文件大小(单位 ZB)
     *
     * @param file
     * @return
     */
    public static long lengthToZB(File file) {
        return lengthToEB(file) >> 10;
    }

    /*
     * -----------------------------------------------------------------------
     * delete
     * -----------------------------------------------------------------------
     */

    /**
     * 深度删除所有文件
     *
     * @param dir
     * @return
     */
    public static boolean deleteAllFiles(String dir) {
        return deleteAllFiles(new File(dir));
    }

    /**
     * 深度删除所有文件
     *
     * @param dir
     * @return
     */
    public static boolean deleteAllFiles(File dir) {
        if (dir == null) {
            return true;
        } else if (dir.isDirectory()) {
            boolean deleted = true;
            List<File> files = traverseDirectory(dir);
            for (int i = files.size() - 1; i >= 0; i--) {
                deleted = delete(files.get(i));
            }
            return deleted;
        } else if (dir.isFile()) {
            return delete(dir);
        }
        return !dir.exists();
    }

    /**
     * 删除文件，返回 file 所指向的文件是否还存在
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        if (file == null) {
            return true;
        }
        try {
            return file.delete();
        } catch (SecurityException e) {
            return !file.exists();
        }
    }

    /*
     * -----------------------------------------------------------------------
     * others
     * -----------------------------------------------------------------------
     */

    public final static String formatPath(String filePath) {
        if (filePath != null) {
            int index = 0;
            char[] chars = null;
            char WIN = Const.WIN_FileSeparator_Char;
            char DFT = Const.App_FileSeparatorChar;
            for (int i = 0, len = filePath.length(); i < len; i++) {
                char ch = filePath.charAt(i);
                chars = SupportUtil.setChar(chars, index++, ch == WIN ? DFT : ch);
            }
            return chars == null ? null : new String(chars, 0, index);
        }
        return null;
    }

    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    public static boolean exists(String filePath) {
        return filePath != null && new File(filePath).exists();
    }
}
