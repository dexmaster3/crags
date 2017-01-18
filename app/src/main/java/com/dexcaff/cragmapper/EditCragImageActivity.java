package com.dexcaff.cragmapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.views.EditCragImageView;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditCragImageActivity extends AppCompatActivity {
    private final static String TAG = "EditCragImageActivity";
    private EditCragImageView mContentView;
    private android.support.v7.app.ActionBar mActionBar;
    private int mActionBarOptions;
    private boolean mActionBarActive = false;

    private boolean mIsEndingAnimator;
    private AnimatorSet mAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crag currentCrag = Crag.getCragById(this, getIntent().getLongExtra(Crag.EXTRA_TAG, -1));
        mActionBar = getSupportActionBar();
        mActionBarOptions = mActionBar.getDisplayOptions();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            String actionBarTitle = getString(R.string.title_activity_edit_crag_image) + " " + currentCrag.properties.get(CragContract.CragEntry.COLUMN_NAME_TITLE);
            mActionBar.setTitle(actionBarTitle);
        }

        mContentView = new EditCragImageView(this, currentCrag);
        setContentView(mContentView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsEndingAnimator = false;
        mAnim = new AnimatorSet();

        ObjectAnimator darkerFade = ObjectAnimator.ofInt(mContentView, "nodeAlpha", 30, 255);
        darkerFade.setDuration(500);

        ObjectAnimator lighterFade = ObjectAnimator.ofInt(mContentView, "nodeAlpha", 255, 30);
        lighterFade.setDuration(500);

        mAnim.playSequentially(darkerFade, lighterFade);
        mAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!mIsEndingAnimator) {
                    mAnim.start();
                }
            }
        });
        mAnim.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mActionBarActive) {
                cancelButton();
                return true;
            } else {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCragButton() {
        try {
            Node node = mContentView.getTempNode();
            mContentView.addAfterTempNodeSaved(node.addNode(this));
            mContentView.removeTempNode();
            hideAddCragActionBar();
        } catch (Exception ex) {
            Log.e(TAG, "Save node click failed", ex);
        }
    }

    private void cancelButton() {
        mContentView.removeTempNode();
        hideAddCragActionBar();
    }

    public void showAddCragActionBar() {
        mActionBar.setCustomView(R.layout.entity_save_toolbar);
        View actionBarView = mActionBar.getCustomView();
        actionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelButton();
                    }
                });
        actionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveCragButton();
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
        mActionBarActive = true;
    }

    private void hideAddCragActionBar() {
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setDisplayOptions(mActionBarOptions);
        mActionBarActive = false;
    }
}
