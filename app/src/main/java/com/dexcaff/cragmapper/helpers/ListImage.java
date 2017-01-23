package com.dexcaff.cragmapper.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

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

    public void loadBitmap(String imageFile, ImageView ImageView) {
        if (cancelPotentialWork(imageFile, ImageView)) {
            final MainListWorker task = new MainListWorker(mContext, ImageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mPlaceholderBitmap, task);
            ImageView.setImageDrawable(asyncDrawable);
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

    private static MainListWorker getMainListWorker(ImageView ImageView) {
        if (ImageView != null) {
            final Drawable drawable = ImageView.getDrawable();
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
            data = params[0];
            return Image.getSampledRotatedBitmap(mContext, data, 100, 100);
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
