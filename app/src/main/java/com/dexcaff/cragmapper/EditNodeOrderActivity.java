package com.dexcaff.cragmapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dexcaff.cragmapper.adapters.NodesAdapter;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;
import com.dexcaff.cragmapper.views.EditCragImageView;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class EditNodeOrderActivity extends AppCompatActivity {
    private static final String TAG = "EditNodeOrderActivity";
    private Context mContext;
    private Crag mCrag;
    private long mCragId;
    private final static int ICON_SIZE = 96;
    private ArrayList<Node> mNodeArray;
    private DragListView mDragListView;
    private EditCragImageView mCragImageView;
    private ActionBar mActionBar;
    private NodesAdapter mNodeAdapter;

    private boolean mIsEndingAnimator;
    private AnimatorSet mAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mCragId = getIntent().getLongExtra(Crag.EXTRA_TAG, -1);
        mCrag = Crag.getCragById(this, mCragId);
        mNodeArray = Node.getAllNodesListByCragId(this, mCragId, Node.hasNodesWeightedValues(this, mCragId));

        setupActionBar();

        setContentView(R.layout.activity_edit_node_order);
        FrameLayout cragFrame = (FrameLayout) findViewById(R.id.node_order_crag_frame);
        mCragImageView = new EditCragImageView(this, mCrag);
        cragFrame.addView(mCragImageView, 0);

        mDragListView = (DragListView) findViewById(R.id.node_drag_list_view);
        mDragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
                Toast.makeText(mContext, "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    Toast.makeText(mContext, "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }
        });

        mDragListView.setLayoutManager(new LinearLayoutManager(mContext));
        mNodeAdapter = new NodesAdapter(mNodeArray, R.layout.node_list_item, R.id.image, false);
        mNodeAdapter.setCragImageView(mCragImageView);
        mDragListView.setAdapter(mNodeAdapter, false);
        mDragListView.setCanDragHorizontally(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsEndingAnimator = false;
        mAnim = new AnimatorSet();

        ObjectAnimator darkerFade = ObjectAnimator.ofInt(mCragImageView, "nodeAlpha", 30, 255);
        darkerFade.setDuration(500);

        ObjectAnimator lighterFade = ObjectAnimator.ofInt(mCragImageView, "nodeAlpha", 255, 30);
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
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        int actionBarOptions = mActionBar.getDisplayOptions();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            String actionBarTitle = getString(R.string.title_activity_order_nodes);
            mActionBar.setTitle(actionBarTitle);
        }

        mActionBar.setCustomView(R.layout.node_order_save_toolbar);
        View actionBarView = mActionBar.getCustomView();
        Drawable doneIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_forward_white_48dp, null);
        doneIcon.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
        TextView doneText = (TextView) actionBarView.findViewById(R.id.node_order_done_text);
        doneText.setCompoundDrawables(doneIcon, null, null, null);
        doneText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishButton();
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

    private void finishButton() {
        for (Node node : mNodeArray) {
            int position = mNodeAdapter.getPositionForItem(node);
            node.properties.put(Node.KEY_NODE_WEIGHT, position);
            try {
                node.updateNode(this);
            } catch (Exception ex) {
                Log.e(TAG, "Update node failed", ex);
            }
        }
        Intent intent = new Intent(this, CragViewActivity.class);
        intent.putExtra(Crag.TAG, mCragId);
        startActivity(intent);
    }
}
