package com.study.hancom.sharephototest.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ImageUtil {

    private static String TAG = ImageUtil.class.getName();

    public static List<String> getMediaImage(Context context) {

        List<String> galleryPictures = new ArrayList<>();

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        final String[] columns = {MediaStore.Images.Media.DATA, //The data stream for the file
                MediaStore.Images.Media._ID, //The unique ID for a row.
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        Log.v(TAG, "imageCursor.getCount() --->" + String.valueOf(imageCursor.getCount()));

        for (int i = 0; i < imageCursor.getCount(); i++) {
            imageCursor.moveToPosition(i);

            //gets the zero-based ColumnIndex for the given column name
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int directoryColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            //get the value of the requested column from ColumnIndex as a String.
            String directoryName = imageCursor.getString(directoryColumnIndex);
            Log.v(TAG, "directoryName --->" + directoryName);
            String fileName = imageCursor.getString(dataColumnIndex);
            Log.d(TAG, "fileName--->" + fileName);

            galleryPictures.add(i, imageCursor.getString(dataColumnIndex));

        }
        imageCursor.close();

        return galleryPictures;
    }

    public static Bitmap getViewScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}