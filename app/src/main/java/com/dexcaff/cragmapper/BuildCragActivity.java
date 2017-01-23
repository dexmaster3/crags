package com.dexcaff.cragmapper;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dexcaff.cragmapper.helpers.Image;
import com.dexcaff.cragmapper.models.Crag;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuildCragActivity extends AppCompatActivity {
    private ImageButton mCragImageButton;
    private String mCurrentPhotoPath;
    private String mTempPhotoPath;
    private Uri mTempPhotoUri;
    private long mCragId;
    private static final int ICON_SIZE = 96;
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
        //Action bar functionality
        final LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.entity_save_toolbar, (ViewGroup) findViewById(R.id.content_main));
        Drawable doneIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_forward_white_48dp, null);
        Drawable closeIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_white_48dp, null);
        doneIcon.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
        closeIcon.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
        TextView doneText = (TextView) customActionBarView.findViewById(R.id.actionbar_done_text);
        TextView closeText = (TextView) customActionBarView.findViewById(R.id.actionbar_cancel_text);
        doneText.setText(R.string.save_crag);
        closeText.setText(R.string.node_cancel);
        doneText.setCompoundDrawables(doneIcon, null, null, null);
        closeText.setCompoundDrawables(closeIcon, null, null, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveCragClick();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(
                android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM,
                android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                        | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME
                        | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new android.support.v7.app.ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                deleteTempImage();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        deleteTempImage();
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            mCragImageButton.setImageBitmap(Image.getSampledRotatedBitmap(this, mTempPhotoPath, 200, 200));
            mCurrentPhotoPath = mTempPhotoPath;
        }
    }

    private void dispatchTakePicture() {
        Intent cragImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cragImageIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Image file creating error", ex);
            }
            if (photoFile != null) {
                mTempPhotoUri = FileProvider.getUriForFile(
                        this,
                        "com.dexcaff.cragmapper.fileprovider",
                        photoFile);
                cragImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTempPhotoUri);
                startActivityForResult(cragImageIntent, CAMERA_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mTempPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveCragClick() {
        EditText cragTitleView = (EditText) findViewById(R.id.crag_edit_title);
        RatingBar cragRatingBar = (RatingBar) findViewById(R.id.crag_edit_rating);

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
        mCragImageButton.setImageBitmap(Image.getSampledRotatedBitmap(this, mCurrentPhotoPath, 300, 300));
        cragTitle.setText((String) crag.properties.get(Crag.KEY_TITLE));
        cragRating.setRating((float) crag.properties.get(Crag.KEY_RATING));
    }

    private boolean deleteTempImage() {
        File file = new File(mCurrentPhotoPath);
        return file.delete();
    }
}
