package com.dexcaff.cragmapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dexcaff.cragmapper.helpers.ActionBarHelper;
import com.dexcaff.cragmapper.helpers.Image;
import com.dexcaff.cragmapper.helpers.Validation;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuildCragActivity extends AppCompatActivity {
    private ImageButton mCragImageButton;
    private String mCurrentPhotoPath;
    private String mExistingPhotoPath;
    private long mCragId;
    private static final int CAMERA_CAPTURE = 1;
    private static final String TAG = "BuildCragActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_crag);

        if (getIntent().getExtras() != null) {
            mCragId = getIntent().getLongExtra(Crag.EXTRA_TAG, -1);
            setCragData();
        } else {
            mCragId = -1;
        }

        TextView doneButton = ActionBarHelper.setupActionBar(this, getSupportActionBar(), getString(R.string.save_crag));
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveCragClick();
                    }
                });

        //Image button
        mCragImageButton = (ImageButton) findViewById(R.id.crag_edit_image);
        mCragImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePicture();
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            mCragImageButton.setImageBitmap(Image.getSampledRotatedBitmap(this, mCurrentPhotoPath, 200, 200));
            Node.deleteAllNodesByCragId(this, mCragId);
        } else if (resultCode == RESULT_CANCELED) {
            mCurrentPhotoPath = mExistingPhotoPath;
        }
    }

    private void dispatchTakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Image file creating error", ex);
            }
            if (photoFile != null) {
                Uri photoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveCragClick() {
        EditText cragTitleView = (EditText) findViewById(R.id.crag_edit_title);
        RatingBar cragRatingBar = (RatingBar) findViewById(R.id.crag_edit_rating);
        if (!Validation.validateItems(new View[] {cragRatingBar, cragTitleView})) {
            return;
        }

        //ToDo add validation
        try {
            Crag crag = new Crag(
                    mCragId,
                    cragTitleView.getText().toString(),
                    mCurrentPhotoPath,
                    cragRatingBar.getRating()
            );
            crag.addCrag(this);

            //Go to picture edit
            Intent intent = new Intent(this, EditCragImageActivity.class);
            //todo Couldn't reduce this into the putExtra() for some reason - could worth looking at some times
            long cragId = (long) crag.properties.get(Crag._ID);
            intent.putExtra(Crag.EXTRA_TAG, cragId);
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Save crag click failed", ex);
        }
    }

    private void setCragData() {
        Crag crag = Crag.getCragById(this, mCragId);
        mCragImageButton = (ImageButton) findViewById(R.id.crag_edit_image);
        TextView cragTitle = (TextView) findViewById(R.id.crag_edit_title);
        RatingBar cragRating = (RatingBar) findViewById(R.id.crag_edit_rating);

        mCurrentPhotoPath = (String) crag.properties.get(Crag.KEY_IMAGE);
        mExistingPhotoPath = mCurrentPhotoPath;
        mCragImageButton.setImageBitmap(Image.getSampledRotatedBitmap(this, mCurrentPhotoPath, 300, 300));
        cragTitle.setText((String) crag.properties.get(Crag.KEY_TITLE));
        cragRating.setRating((float) crag.properties.get(Crag.KEY_RATING));
    }
}
