package com.study.hancom.sharephototest.util;

import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private static final String TAG = FileUtil.class.getName();
    private static final int BUFFER_SIZE = 1024 * 2;

    private FileUtil() {}

    public static File createDirectory(String dir_path) {
        File dir = new File(dir_path);
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdirs();
            if (isDirectoryCreated) {
                Log.i(TAG, " 파일 생성 성공");
            } else {
                Log.i(TAG, " 파일 생성 실패");
            }
        } else {
            Log.i(TAG, " 기존 파일 존재");
        }
        return dir;
    }

    public static File createFile(File dir, String filePath) {
        File file = null;
        boolean isSuccess = false;
        if (dir.isDirectory()) {
            file = new File(filePath);
            if (!file.exists()) {
                Log.i(TAG, " 파일 없음");
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, " 파일생성 여부 = " + isSuccess);
                }
            } else {
                Log.i(TAG, " 파일 존재");
            }
        }
        return file;
    }

    public static boolean copyFile(File file, String save_file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream newFos = new FileOutputStream(save_file);
            int readCount;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readCount = fis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                newFos.write(buffer, 0, readCount);
            }
            newFos.close();
            fis.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFolder(File targetFolder) {
        if (!targetFolder.exists()) return false;

        File[] childFile = targetFolder.listFiles();
        int size = childFile.length;

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (childFile[i].isFile()) {
                    if(childFile[i].delete()) {
                        Log.i(TAG, childFile[i].getName() + " 파일 삭제 성공");
                    }
                } else {
                    deleteFolder(childFile[i]);
                }
            }
        }
        if(targetFolder.delete()){
            Log.i(TAG, targetFolder.getName() + " 폴더 삭제 성공");
        }else{
            Log.i(TAG, targetFolder.getName() + " 폴더 삭제 실패");
        }
        return (!targetFolder.exists());
    }

    public static void writeFile(File file, String content) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            try {
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String fileToString(InputStream file) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int readCount;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readCount = file.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, readCount);
            }
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    public static boolean zipFolder(String srcFolder, String destZipFile) {
        try {
            ZipFile zipFile = new ZipFile(destZipFile);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFolder(srcFolder + "OEBPS", parameters);
            zipFile.addFolder(srcFolder + "META-INF", parameters);
            zipFile.addFile(new File(srcFolder + "mimetype"), parameters);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return false;
    }
}
