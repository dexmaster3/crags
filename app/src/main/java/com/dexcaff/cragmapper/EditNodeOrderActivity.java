package com.dexcaff.cragmapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dexcaff.cragmapper.adapters.NodesAdapter;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;
import com.dexcaff.cragmapper.views.EditCragImageView;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class EditNodeOrderActivity extends AppCompatActivity {
    private Context mContext;
    private long mCragId;
    private ArrayList<Node> mNodeArray;
    private DragListView mDragListView;
    private EditCragImageView mCragImageView;

    private boolean mIsEndingAnimator;
    private AnimatorSet mAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mCragId = getIntent().getLongExtra(Crag.EXTRA_TAG, -1);
        Crag crag = Crag.getCragById(this, mCragId);
        mNodeArray = Node.getAllNodesListByCragId(this, mCragId);

        setContentView(R.layout.activity_edit_node_order);
        FrameLayout cragFrame = (FrameLayout) findViewById(R.id.node_order_crag_frame);
        mCragImageView = new EditCragImageView(this, crag);
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
        NodesAdapter listAdapter = new NodesAdapter(mNodeArray, R.layout.node_list_item, R.id.image, false);
        listAdapter.setCragImageView(mCragImageView);
        mDragListView.setAdapter(listAdapter, false);
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
}
