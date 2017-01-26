package com.dexcaff.cragmapper.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.dexcaff.cragmapper.MainActivity;
import com.dexcaff.cragmapper.R;

import java.lang.ref.WeakReference;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.23
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.helpers
 */

public class ListImage {
    private Context mContext;
    private Bitmap mPlaceholderBitmap;

    public ListImage(Context context) {
        mContext = context;
        mPlaceholderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_close_black_48dp);
    }

    public void loadBitmap(String imageFile, ImageView imageView) {
        final Bitmap bitmap = ((MainActivity) mContext).getBitmapFromMemCache(imageFile);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(imageFile, imageView)) {
            final MainListWorker task = new MainListWorker(mContext, imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mPlaceholderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(imageFile);
        }
    }

    public static boolean cancelPotentialWork(String data, ImageView ImageView) {
        final MainListWorker task = getMainListWorker(ImageView);

        if (task != null) {
            final String bitmapData = task.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData.equals("") || !bitmapData.equals(data)) {
                // Cancel previous task
                task.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static MainListWorker getMainListWorker(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getMainListWorker();
            }
        }
        return null;
    }

    public class MainListWorker extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> ImageViewReference;
        private Context mContext;
        private String data = "";

        public MainListWorker(Context context, ImageView ImageView) {
            mContext = context;
            ImageViewReference = new WeakReference<>(ImageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            final Bitmap bitmap = Image.getSampledRotatedBitmap(mContext, params[0], 100, 100);
            ((MainActivity) mContext).addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (ImageViewReference != null && bitmap != null) {
                final ImageView ImageView = ImageViewReference.get();
                final MainListWorker mainListWorker = ListImage.getMainListWorker(ImageView);
                if (this == mainListWorker && ImageView != null) {
                    ImageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<MainListWorker> mainListWorkerReference;

        public AsyncDrawable(Resources res, Bitmap bmp, MainListWorker mainListWorker) {
            super(res, bmp);
            mainListWorkerReference = new WeakReference<>(mainListWorker);
        }

        public MainListWorker getMainListWorker() {
            return mainListWorkerReference.get();
        }
    }
}
