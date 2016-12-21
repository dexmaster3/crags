package com.dexcaff.cragmapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.models.Crag;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuildCragActivity extends AppCompatActivity {
    private ImageButton mCragImageButton;
    private String mCurrentPhotoPath;
    private int mCragId;
    private static final String TAG = "BuildCragActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_crag);

        if (getIntent().getBundleExtra("crag") != null) {
            mCragId = setCragData(getIntent().getBundleExtra("crag"));
        }
        //Action bar functionality
        final LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.entity_save_toolbar, (ViewGroup) findViewById(R.id.content_main));
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveCragClick();
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
        mCragImageButton = (ImageButton) findViewById(R.id.crag_add_image);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            mCragImageButton.setImageURI(Uri.fromFile(file));
        }
    }

    private void dispatchTakePicture() {
        Intent cragImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cragImageIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "Image file creating error", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.dexcaff.cragmapper.fileprovider",
                        photoFile);
                cragImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cragImageIntent, 1);
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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveCragClick() {
        EditText cragTitleView = (EditText) findViewById(R.id.crag_edit_text);

        //ToDo add validation
        try {
            if (mCragId > 0) {
                Crag.updateCrag(getBaseContext(),
                        new Crag(
                                cragTitleView.getText().toString(),
                                mCurrentPhotoPath
                        ),
                        mCragId
                );
            } else {
                Crag.addCrag(getBaseContext(),
                        new Crag(
                                cragTitleView.getText().toString(),
                                mCurrentPhotoPath
                        )
                );
            }
        } catch (Exception ex) {
            Log.d(TAG, "Save crag click failed", ex);
        }

        //Return to main activity hitting onCreate()
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        NavUtils.navigateUpTo(this, upIntent);
    }

    private int setCragData(Bundle crag) {
        mCragImageButton = (ImageButton) findViewById(R.id.crag_add_image);
        mCragImageButton.setImageURI(Uri.parse(crag.getString(CragContract.CragEntry.COLUMN_NAME_IMAGE)));
        TextView cragTitle = (TextView) findViewById(R.id.crag_edit_text);
        cragTitle.setText(crag.getString(CragContract.CragEntry.COLUMN_NAME_TITLE));

        return crag.getInt(CragContract.CragEntry._ID);
    }
}
