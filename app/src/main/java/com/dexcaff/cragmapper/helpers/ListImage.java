package com.dexcaff.cragmapper.helpers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.animation.Animation;
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
        mPlaceholderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.spinner_48_inner_holo);
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
        private final WeakReference<ImageView> mImageViewReference;
        private Context mContext;
        private ObjectAnimator mAnim;
        private String data = "";

        public MainListWorker(Context context, ImageView imageView) {
            mContext = context;
            mImageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected void onPreExecute() {
            if (mImageViewReference != null) {
                final ImageView imageView = mImageViewReference.get();
                final MainListWorker mainListWorker = ListImage.getMainListWorker(imageView);
                if (this == mainListWorker && imageView != null) {
                    mAnim = ObjectAnimator.ofFloat(imageView, "rotation", 1f, 360f);
                    mAnim.setDuration(1000);
                    mAnim.setRepeatCount(Animation.INFINITE);
                    mAnim.start();
                }
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            final Bitmap bitmap = Image.getSampledRotatedBitmap(mContext, params[0], 100, 100);
            ((MainActivity) mContext).addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mAnim.end();
            if (isCancelled()) {
                bitmap = null;
            }
            if (mImageViewReference != null && bitmap != null) {
                final ImageView imageView = mImageViewReference.get();
                final MainListWorker mainListWorker = ListImage.getMainListWorker(imageView);
                if (this == mainListWorker && imageView != null) {
                    imageView.clearAnimation();
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAnim.end();
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
