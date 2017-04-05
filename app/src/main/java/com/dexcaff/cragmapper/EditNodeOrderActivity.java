package com.dexcaff.cragmapper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.dexcaff.cragmapper.adapters.NodesAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class EditNodeOrderActivity extends AppCompatActivity {
    private Context mContext;
    private ArrayList<Pair<Long, String>> mItemArray;
    private DragListView mDragListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_edit_node_order);
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

        mItemArray = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            mItemArray.add(new Pair<>((long) i, "Item " + i));
        }

        mDragListView.setLayoutManager(new LinearLayoutManager(mContext));
        NodesAdapter listAdapter = new NodesAdapter(mItemArray, R.layout.crag_list_item, R.id.image, false);
        mDragListView.setAdapter(listAdapter, false);
        mDragListView.setCanDragHorizontally(false);
    }

}
