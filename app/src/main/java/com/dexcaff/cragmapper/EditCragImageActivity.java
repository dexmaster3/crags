package com.dexcaff.cragmapper;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.libs.EditCragImageView;
import com.dexcaff.cragmapper.libs.NodeCircle;
import com.dexcaff.cragmapper.libs.TouchImageView;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditCragImageActivity extends AppCompatActivity {
    private final static String TAG = "EditCragImageActivity";
    private Crag mCurrentCrag;
    private TouchImageView mImageView;
    private Uri mOriginalImage;
    private android.support.v7.app.ActionBar mActionBar;
    private int mActionBarOptions;
    private boolean mTouchActive;
    private float[] mCurrentTouchCoords;
    private FrameLayout mFramelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo image resizing etc..
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        mActionBarOptions = mActionBar.getDisplayOptions();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCurrentCrag = Crag.getCragById(getBaseContext(), getIntent().getLongExtra(Crag.EXTRA_TAG, -1));
        mOriginalImage = Uri.parse((String) mCurrentCrag.properties.get(CragContract.CragEntry.COLUMN_NAME_IMAGE));
        View contentView = new EditCragImageView(this, mOriginalImage.toString());
        setContentView(R.layout.activity_edit_crag_image);

        mFramelay = (FrameLayout) findViewById(R.id.edit_img_frame);
        getPopulatedCragBitmap();

        mImageView = (TouchImageView) findViewById(R.id.crag_edit_image_view);
        mImageView.setImageDrawable(getPopulatedCragBitmap());
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
                getViewCoords(event, mImageView);
//                Bitmap cragImage = getPopulatedCragBitmap();
//                Bitmap cragNode = BitmapFactory.decodeResource(getResources(), R.drawable.node_circle);
//                Bitmap resultBitmap = Bitmap.createBitmap(cragImage.getWidth(),cragImage.getHeight(), cragImage.getConfig());
//                Canvas canvas = new Canvas(resultBitmap);
//                canvas.drawBitmap(cragImage, new Matrix(), null);
//                NodeCircle testr = new NodeCircle(getApplicationContext(), null);
//                testr.draw(canvas);
//                canvas.drawBitmap(cragNode, mCurrentTouchCoords[0] - (cragNode.getWidth() / 2), mCurrentTouchCoords[1] - (cragNode.getHeight() / 2), new Paint());

//                mImageView.setImageBitmap(resultBitmap);
            } else {
                mImageView.setImageDrawable(getPopulatedCragBitmap());
            }
        }
        return false;
   }

    private void saveCragTouch() {
        try {
            long cragid = (long) mCurrentCrag.properties.get(CragContract.CragEntry._ID);
            Node node = new Node(-1, cragid, mCurrentTouchCoords[0], mCurrentTouchCoords[1]);
            node.addNode(getBaseContext());
            toggleTouchActive();
            mImageView.setImageDrawable(getPopulatedCragBitmap());
        } catch (Exception ex) {
            Log.d(TAG, "Save node click failed", ex);
        }
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

    private BitmapDrawable getPopulatedCragBitmap() {
        long cragId = (long) mCurrentCrag.properties.get(CragContract.CragEntry._ID);
        HashMap<String, Node> nodes = Node.getAllNodesByCragId(this, cragId);
        try {
            EditCragImageView cragImageView = new EditCragImageView(this, mOriginalImage.toString());
            for (Map.Entry<String, Node> entry : nodes.entrySet()){
                Node node = entry.getValue();
                NodeCircle ragy = new NodeCircle(this, null, node);

                mFramelay.addView(ragy);
//                ragy.draw(canvas);
                break;
            }
            return new BitmapDrawable(getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444));
        } catch (Exception ex) {
            Log.d(TAG, "Canvas population error", ex);
            return new BitmapDrawable(getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444));
        }
    }

    private void getViewCoords(MotionEvent event, TouchImageView imageView) {
        Matrix invertMatrix = new Matrix();
        imageView.getImageMatrix().invert(invertMatrix);
        mCurrentTouchCoords = new float[] {event.getX(), event.getY()};
        invertMatrix.mapPoints(mCurrentTouchCoords);
        //This should fix density issues
        float scaleX = imageView.getScaleX();
        float scaleY = imageView.getScaleY();
        mCurrentTouchCoords[0] *= scaleX;
        mCurrentTouchCoords[1] *= scaleY;

    }

    private boolean toggleTouchActive() {
        if (mTouchActive) {
            hideAddCragActionBar();
        } else {
            showAddCragActionBar();
        }
        mTouchActive = !mTouchActive;
        return !mTouchActive;
    }
}
