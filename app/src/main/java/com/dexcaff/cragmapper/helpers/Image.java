package com.dexcaff.cragmapper.helpers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.dexcaff.cragmapper.R;

import java.io.IOException;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.21
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.helpers
 */

public class Image {
    private static final String TAG = "dexcaff.helpers.Image";

    public static Bitmap getSampledRotatedBitmap(Context context, String filename, int reqWidth, int reqHeight) {
        Matrix mtx = Image.getPhotoRotateMatrix(context, filename);
        Bitmap tempPhoto = Image.getSampledBitmap(filename, reqWidth, reqHeight);
        if (tempPhoto == null) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_broken_image_black_48dp);
        }
        tempPhoto = Bitmap.createBitmap(tempPhoto, 0, 0, tempPhoto.getWidth(), tempPhoto.getHeight(), mtx, true);
        return tempPhoto;
    }

    public static Bitmap getSampledBitmap(String filename, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static Matrix getPhotoRotateMatrix(Context context, String photoPath) {
        int orientation = getPhotoRotateDegree(context, photoPath);
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return matrix;
    }

    private static int getPhotoRotateDegree(Context context, String photoPath) {
        Uri imageUri = Uri.parse(photoPath);
        String[] orientationColumn = {MediaStore.Images.Media.DATA};
        Cursor cur = context.getContentResolver().query(imageUri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            int colIdx = cur.getColumnIndex(orientationColumn[0]);
            if (colIdx > -1)
                orientation = cur.getInt(colIdx);
            cur.close();
        }
        if (orientation == -1) {
            orientation = getExifOrientation(imageUri.toString());
        }
        return orientation;
    }

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static float[] getOriginalImageSize(Context context, String filename) {
        int rotateDegree = getPhotoRotateDegree(context, filename);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        if (rotateDegree == 0 || rotateDegree == 180) {
            return new float[]{options.outWidth, options.outHeight};
        }
        return new float[]{options.outHeight, options.outWidth};
    }

    private static int getExifOrientation(String filepath) {
        int rotationAngle = 0;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            String orientationStr = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientationStr != null ? Integer.parseInt(orientationStr) : ExifInterface.ORIENTATION_NORMAL;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        } catch (IOException ex) {
            Log.e(TAG, "ExifInterface failture", ex);
        }
        return rotationAngle;
    }
}
