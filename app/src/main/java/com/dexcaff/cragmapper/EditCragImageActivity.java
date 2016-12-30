package com.dexcaff.cragmapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.libs.TouchImageView;
import com.dexcaff.cragmapper.models.Crag;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditCragImageActivity extends AppCompatActivity {
    private TouchImageView mImageView;
    private Uri mOriginalImage;
    private android.support.v7.app.ActionBar mActionBar;
    private int mActionBarOptions;
    private boolean mTouchActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo image resizing etc..
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        mActionBarOptions = mActionBar.getDisplayOptions();
        Crag crag = Crag.getCragById(getBaseContext(), getIntent().getLongExtra(Crag.EXTRA_TAG, -1));

        setContentView(R.layout.activity_edit_crag_image);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mOriginalImage = Uri.parse((String) crag.properties.get(CragContract.CragEntry.COLUMN_NAME_IMAGE));
        mImageView = (TouchImageView) findViewById(R.id.crag_edit_image_view);
        mImageView.setImageURI(mOriginalImage);
        mImageView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return addCragTouch(event);
                    }
                }
        );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addCragTouch(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (!toggleTouchActive()) {
                showAddCragActionBar();

                float[] touchCoords = getViewCoords(event, mImageView);
                Bitmap cragImage = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                Bitmap cragNode = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_cancel);
                Bitmap resultBitmap = Bitmap.createBitmap(cragImage.getWidth(),cragImage.getHeight(), cragImage.getConfig());
                Canvas canvas = new Canvas(resultBitmap);
                canvas.drawBitmap(cragImage, new Matrix(), null);
                canvas.drawBitmap(cragNode, touchCoords[0] - (cragNode.getWidth() / 2), touchCoords[1] - (cragNode.getHeight() / 2), new Paint());

                mImageView.setImageBitmap(resultBitmap);
            } else {
                hideAddCragActionBar();
                mImageView.setImageURI(mOriginalImage);
            }
        }
        return false;
   }

    private boolean toggleTouchActive() {
        mTouchActive = !mTouchActive;
        return !mTouchActive;
    }

    private void hideCragTouch() {

    }

    private void saveCragTouch() {

    }

    private void showAddCragActionBar() {
        mActionBar.setCustomView(R.layout.entity_save_toolbar);
        View actionBarView = mActionBar.getCustomView();
        actionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        actionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveCragTouch();
                    }
                });

        mActionBar.setDisplayOptions(
                android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM,
                android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                        | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME
                        | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setCustomView(actionBarView,
                new android.support.v7.app.ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void hideAddCragActionBar() {
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setDisplayOptions(mActionBarOptions);
    }

    private float[] getViewCoords(MotionEvent event, TouchImageView imageView) {
        Matrix invertMatrix = new Matrix();
        imageView.getImageMatrix().invert(invertMatrix);
        float[] touchCoords = new float[] {event.getX(), event.getY()};
        invertMatrix.mapPoints(touchCoords);
        //Todo density could be an issue but this doesn't work
        float density = getResources().getDisplayMetrics().density;
        touchCoords[0] *= density;
        touchCoords[1] *= density;
        return touchCoords;
    }
}
