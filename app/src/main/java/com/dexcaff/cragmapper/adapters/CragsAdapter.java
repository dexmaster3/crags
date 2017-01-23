package com.dexcaff.cragmapper.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dexcaff.cragmapper.BuildCragActivity;
import com.dexcaff.cragmapper.R;
import com.dexcaff.cragmapper.helpers.Image;
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
    private Context mContext;

    public CragsAdapter(Context context, ArrayList<Crag> crags) {
        super(context, R.layout.crag_list_item, crags);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Crag crag = getItem(position);
        final long cragId = (long) crag.properties.get(Crag._ID);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.crag_list_item, parent, false);
        }
        TextView cragTitle = (TextView) convertView.findViewById(R.id.crag_list_title);
        ImageView cragImage = (ImageView) convertView.findViewById(R.id.crag_list_image);
        Button editButton = (Button) convertView.findViewById(R.id.crag_list_edit_button);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.crag_list_rating);

        editButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editCragIntent = new Intent(mContext, BuildCragActivity.class);
                        editCragIntent.putExtra(Crag.EXTRA_TAG, cragId);
                        mContext.startActivity(editCragIntent);
                    }
                }
        );
        ratingBar.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        try {
                            Crag crag = Crag.getCragById(mContext, cragId);
                            if ((float) crag.properties.get(Crag.KEY_RATING) != rating) {
                                crag.properties.put(Crag.KEY_RATING, rating);
                                crag.addCrag(mContext);
                                ratingBar.setRating(rating);
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Rating bar change save failed", ex);
                        }
                    }
                }
        );
        ratingBar.setRating((float) crag.properties.get(Crag.KEY_RATING));
        cragTitle.setText((String) crag.properties.get(Crag.KEY_TITLE));
        cragImage.setImageBitmap(Image.getSampledBitmap((String) crag.properties.get(Crag.KEY_IMAGE), 200, 200));

        return convertView;
    }
}
