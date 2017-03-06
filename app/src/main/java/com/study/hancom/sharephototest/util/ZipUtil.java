package com.study.hancom.sharephototest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    private static final int COMPRESSION_LEVEL = 4;
    private static final int BUFFER_SIZE = 1024 * 2;

    public static void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zipOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destZipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipOutputStream.setLevel(COMPRESSION_LEVEL);

            addFolderToZip("", srcFolder, zipOutputStream);

        } finally {
            if (zipOutputStream != null) {
                zipOutputStream.flush();
                zipOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    private static void addFileToZip(String path, String srcFile, ZipOutputStream zipOutputStream) throws Exception {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zipOutputStream);
        } else {
            byte[] buf = new byte[BUFFER_SIZE];
            FileInputStream in = new FileInputStream(srcFile);
            zipOutputStream.putNextEntry(new ZipEntry(path.replace("EpubMaker/", "") + "/" + folder.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, len);
            }
        }
    }

    private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zipOutputStream) throws Exception {
        File folder = new File(srcFolder);
        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zipOutputStream);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zipOutputStream);
            }
        }
    }
}