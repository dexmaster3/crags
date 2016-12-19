package com.dexcaff.cragmapper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.dexcaff.cragmapper.adapters.CragsAdapter;
import com.dexcaff.cragmapper.db.CragDbHelper;
import com.dexcaff.cragmapper.models.Crag;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CragsAdapter mAdapter;
    private ListView mCragListView;
    private CragDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCragBuilderActivity();
            }
        });

        generateCragsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new route")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                Log.d(TAG, "Task to add: " + task);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void generateCragsList()
    {
        ArrayList<Crag> cragList = Crag.getAllCrags(getBaseContext());
        //Apply database info to views
        mCragListView = (ListView) findViewById(R.id.crags_list);
        if (mAdapter == null) {
            mAdapter = new CragsAdapter(this, cragList);
            mCragListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(cragList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void openCragBuilderActivity()
    {
        //Todo add edit crag builder
        Intent intent = new Intent(this, BuildCragActivity.class);
        startActivity(intent);
    }
}
