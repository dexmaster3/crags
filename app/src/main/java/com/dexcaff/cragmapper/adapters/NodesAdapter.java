package com.dexcaff.cragmapper.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dexcaff.cragmapper.R;
import com.dexcaff.cragmapper.models.Node;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.03.31
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.adapters
 */

public class NodesAdapter extends DragItemAdapter<Node, NodesAdapter.ViewHolder> {
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private long mCurrentNodeSelection;
    protected NodesAdapter mAdapter = this;

    public NodesAdapter(ArrayList<Node> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        long cragId = (long) mItemList.get(position).properties.get(Node._ID);
        if (mCurrentNodeSelection == cragId) {
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.black_overlay));
        } else {
            holder.mView.setBackgroundColor(Color.TRANSPARENT);
        }
        String text = Float.toString((float) mItemList.get(position).properties.get(Node.KEY_Y_COORD));
        holder.mText.setText(text);
        holder.itemView.setTag(mItemList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return (long) mItemList.get(position).properties.get(Node._ID);
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;
        View mView;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mView = itemView;
            itemView.setBackgroundColor(Color.TRANSPARENT);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onItemClicked(View view) {
            //ToDo alter underlying data adapter for changes?
            Node node = (Node) view.getTag();
            mCurrentNodeSelection = (long) node.properties.get(Node._ID);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(view.getContext(), "Node " + Long.toString(mCurrentNodeSelection) + " clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
