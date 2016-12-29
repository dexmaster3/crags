package com.dexcaff.cragmapper;

import android.app.Dialog;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.libs.TouchImageView;
import com.dexcaff.cragmapper.models.Crag;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditCragImageActivity extends AppCompatActivity {
    private TouchImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo image resizing etc..
        super.onCreate(savedInstanceState);
        Crag crag = Crag.getCragById(getBaseContext(), getIntent().getLongExtra(Crag.EXTRA_TAG, -1));

        setContentView(R.layout.activity_edit_crag_image);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mImageView = (TouchImageView) findViewById(R.id.crag_edit_image_view);
        mImageView.setImageURI(Uri.parse((String) crag.properties.get(CragContract.CragEntry.COLUMN_NAME_IMAGE)));
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
            int x = (int) event.getX();
            int y = (int) event.getY();
            //todo should all contexts be replaced with class this?
            Dialog dialog = new Dialog(EditCragImageActivity.this);
            dialog.setContentView(R.layout.edit_crag_image_popup);
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.CENTER;
            wmlp.x = x;
            wmlp.y = y;
            Button btnSave = (Button) dialog.findViewById(R.id.crag_edit_image_point_save);
            dialog.show();

            Bitmap cragImage = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
            Bitmap cragNode = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_cancel);
            Bitmap resultBitmap = Bitmap.createBitmap(cragImage.getWidth(),cragImage.getHeight(), cragImage.getConfig());
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(cragImage, new Matrix(), null);
            canvas.drawBitmap(cragNode, (cragImage.getWidth() - cragNode.getWidth()) / 2, (cragImage.getHeight() - cragNode.getHeight()) / 2, new Paint());

            mImageView.setImageBitmap(resultBitmap);
        }
        return false;
    }
}
