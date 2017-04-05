package com.dexcaff.cragmapper.adapters;

import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dexcaff.cragmapper.R;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.03.31
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.adapters
 */

public class NodesAdapter extends DragItemAdapter {
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public NodesAdapter(ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
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
    public void onBindViewHolder(DragItemAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = (String) ((Pair)mItemList.get(position)).second;
        holder.itemView.setTag(mItemList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return (long) ((Pair)mItemList.get(position)).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
