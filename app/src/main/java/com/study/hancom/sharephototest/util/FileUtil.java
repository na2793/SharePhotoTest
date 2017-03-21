package com.study.hancom.sharephototest.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    private static final String TAG = FileUtil.class.getName();
    private static final int BUFFER_SIZE = 1024;

    public File makeDirectory(String dir_path) {
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

    public File createFile(File dir, String file_path) {
        File file = null;
        boolean isSuccess = false;
        if (dir.isDirectory()) {
            file = new File(file_path);
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

    public boolean copyFile(File file, String save_file) {
        boolean result;
        if (file != null && file.exists()) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean deleteFolder(File targetFolder) {
        if (!targetFolder.exists()) return false;

        File[] childFile = targetFolder.listFiles();
        int size = childFile.length;

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (childFile[i].isFile()) {
                    childFile[i].delete();
                } else {
                    deleteFolder(childFile[i]);
                }
            }
        }
        if (targetFolder.delete()) {
            Log.i(TAG, targetFolder.getName() + " 폴더 삭제 성공");
        } else {
            Log.i(TAG, targetFolder.getName() + " 폴더 삭제 실패");
        }
        return (!targetFolder.exists());
    }

    public void writeFile(File file, String content) {
        FileOutputStream fos;
        if (file.exists()) {
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
    }
}
