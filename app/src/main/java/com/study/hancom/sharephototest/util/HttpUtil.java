package com.study.hancom.sharephototest.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HttpUtil {
    public static void addDefaultForm(DataOutputStream os, String fieldName, String value, String boundary) throws IOException {
        os.writeBytes("--" + boundary + "\r\n");
        addDefaultForm(os, fieldName, value);
    }

    public static void addDefaultForm(DataOutputStream os, String fieldName, String value) throws IOException {
        os.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n");
        os.writeBytes(value);
        os.writeBytes("\r\n");
    }

    public static void addFileForm(DataOutputStream os, String fieldName, File file, String boundary) throws IOException {
        os.writeBytes("--" + boundary + "\r\n");
        addFileForm(os, fieldName, file);
    }

    public static void addFileForm(DataOutputStream os, String fieldName, File file) throws IOException {
        String fileName = file.getName();
        os.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"\r\n");
        os.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) > -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.writeBytes("\r\n");
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
