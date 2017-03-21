package com.study.hancom.sharephototest.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;

public class ZipUtil {

    public static void zipFolder(String srcFolder, String destZipFile) throws Exception {

        ZipFile zipFile = new ZipFile(destZipFile);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipFile.addFolder(srcFolder + "OEBPS", parameters);
        zipFile.addFolder(srcFolder + "META-INF", parameters);
        zipFile.addFile(new File(srcFolder + "mimetype"), parameters);

    }
}