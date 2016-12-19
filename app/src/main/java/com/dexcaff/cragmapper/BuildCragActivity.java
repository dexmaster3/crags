package com.dexcaff.cragmapper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.db.CragDbHelper;

public class BuildCragActivity extends AppCompatActivity {
    private ImageButton mCragImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_crag);

        //Action bar functionality
        final LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.entity_save_toolbar, null);
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
        mCragImageButton = (ImageButton) findViewById(R.id.crag_add_image);
        mCragImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cragImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cragImageIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cragImageIntent, 1);
                        }
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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mCragImageButton.setImageBitmap(imageBitmap);
        }
    }

    private void saveCragClick() {
        EditText cragTitleView = (EditText) findViewById(R.id.crag_edit_text);
        //ToDo add validation
        CragDbHelper mDbHelper = new CragDbHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CragContract.CragEntry.COLUMN_NAME_TITLE, cragTitleView.getText().toString());
        values.put(CragContract.CragEntry.COLUMN_NAME_IMAGE, "text/image/placeholder.jpg");
        long rowId = db.insert(CragContract.CragEntry.TABLE_NAME, null, values);

        //Return to main activity hitting onCreate()
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        NavUtils.navigateUpTo(this, upIntent);
    }
}
