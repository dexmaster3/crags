package com.dexcaff.cragmapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dexcaff.cragmapper.adapters.NodesAdapter;
import com.dexcaff.cragmapper.helpers.ActionBarHelper;
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

        TextView doneButton = ActionBarHelper.setupActionBar(this, getSupportActionBar(), getString(R.string.finish_crag));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishButton();
            }
        });

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
    protected void onPause() {
        super.onPause();
        mAnim.cancel();
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
