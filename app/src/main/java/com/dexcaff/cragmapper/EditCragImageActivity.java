package com.dexcaff.cragmapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dexcaff.cragmapper.helpers.ActionBarHelper;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;
import com.dexcaff.cragmapper.views.EditCragImageView;

import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditCragImageActivity extends AppCompatActivity {
    private final static String TAG = "EditCragImageActivity";
    private final static int ICON_SIZE = 96;
    private EditCragImageView mContentView;
    private android.support.v7.app.ActionBar mActionBar;
    private int mActionBarOptions;
    private long mCragId;
    private Crag mCurrentCrag;
    private boolean mActionBarActive = false;

    private boolean mIsEndingAnimator;
    private AnimatorSet mAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCragId = getIntent().getLongExtra(Crag.EXTRA_TAG, -1);
        mCurrentCrag = Crag.getCragById(this, mCragId);
        mActionBar = getSupportActionBar();
        mActionBarOptions = mActionBar.getDisplayOptions();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            String actionBarTitle = getString(R.string.edit_crag_actionbar) + " " + mCurrentCrag.properties.get(Crag.KEY_TITLE);
            mActionBar.setTitle(actionBarTitle);
        }

        mContentView = new EditCragImageView(this, mCurrentCrag);
        setContentView(mContentView);
        checkShowNextStepBar();
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
    public void onBackPressed() {
        if (mActionBarActive) {
            cancelButton();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNodeButton() {
        try {
            Node node = mContentView.getTempNode();
            mContentView.addAfterTempNodeSaved(node.addNode(this));
            mContentView.removeTempNode();
            checkShowNextStepBar();
        } catch (Exception ex) {
            Log.e(TAG, "Save node click failed", ex);
        }
    }

    private void cancelButton() {
        mContentView.removeTempNode();
        checkShowNextStepBar();
    }

    private void nextStepButton() {
        Intent intent = new Intent(this, EditNodeOrderActivity.class);
        intent.putExtra(Crag.EXTRA_TAG, mCragId);
        startActivity(intent);
    }

    private void checkShowNextStepBar() {
        HashMap<String, Node> nodes = Node.getAllNodesByCragId(this, (long)mCurrentCrag.properties.get(Crag._ID));
        if (nodes.size() >= 1) {
            showNextStepActionBar();
        } else {
            hideAddNodeActionBar();
        }
    }

    public void showNextStepActionBar() {
        TextView doneText = ActionBarHelper.setupActionBar(this, mActionBar, getString(R.string.next_save_nodes));
        doneText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextStepButton();
                    }
                });
        mActionBarActive = false;
    }

    public void showAddNodeActionBar() {
        TextView doneText = ActionBarHelper.setupActionBar(this, mActionBar, getString(R.string.save_node));
        doneText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNodeButton();
                    }
                });
        mActionBarActive = true;
    }

    private void hideAddNodeActionBar() {
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setDisplayOptions(mActionBarOptions);
        mActionBarActive = false;
    }
}
