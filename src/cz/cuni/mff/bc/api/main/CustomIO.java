/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Jakub
 */
public class CustomIO {

    public static String getExtension(File file) {
        int dotIndex = file.getName().lastIndexOf(".") + 1;
        return file.getName().substring(dotIndex);
    }

    public static void extractZipFile(File zip, File dest) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(dest, fileName);
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    ze = zis.getNextEntry();
                } catch (IOException e) {
                    throw new IOException("Problem with extracting file " + ze.getName() + " from archive " + zip.getName(), e);
                }
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            throw new IOException("Problem with opening project archive" + zip.getName(), e);
        }
    }

    private static void appendToZip(File file, ZipOutputStream zos) throws IOException {
        String name = file.getName();
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = fis.read(byteBuffer)) != -1) {
                zos.write(byteBuffer, 0, bytesRead);
            }
            zos.flush();
        }
    }

    public static void zipFiles(File zip, File[] files) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
            for (File file : files) {
                appendToZip(file, zos);
            }
        }
    }

    public static File createFolder(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from.toPath(), to.toPath());
    }

    public static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    public static void deleteWithPattern(File dirWhere, final String pattern) {
        File[] files = dirWhere.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.matches(pattern)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (File file : files) {
            file.delete();
        }
    }
}
