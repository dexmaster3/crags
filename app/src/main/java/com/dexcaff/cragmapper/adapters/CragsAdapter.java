package com.dexcaff.cragmapper.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexcaff.cragmapper.R;
import com.dexcaff.cragmapper.models.Crag;

import java.util.ArrayList;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.adapters
 */

public class CragsAdapter extends ArrayAdapter<Crag> {
    private static final String TAG = "CragAdapter";

    public CragsAdapter(Context context, ArrayList<Crag> crags) {
        super(context, 0, crags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Crag crag = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.crag_list_item, parent, false);
        }
        TextView cragTitle = (TextView) convertView.findViewById(R.id.crag_title);
        ImageView cragImage = (ImageView) convertView.findViewById(R.id.crag_image);

        cragTitle.setText(crag.name);
        cragImage.setImageURI(Uri.parse(crag.imageURI));
        return convertView;
    }
}
