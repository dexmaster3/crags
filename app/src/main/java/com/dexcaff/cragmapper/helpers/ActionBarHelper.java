package com.dexcaff.cragmapper.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexcaff.cragmapper.R;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.04.18
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.helpers
 */

public class ActionBarHelper {
    private static final int ICON_SIZE = 96;
    private static final String DEFAULT_BUTTON_TEXT = "Save";
    public static TextView setupActionBar(Context context, ActionBar actionBar) {
        return setupActionBar(context, actionBar, DEFAULT_BUTTON_TEXT);
    }

    public static TextView setupActionBar(Context context, ActionBar actionBar, String text) {
        if (actionBar == null) {
            return null;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(R.layout.general_single_button_toolbar);
        View actionBarView = actionBar.getCustomView();
        Drawable doneIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow_forward_white_48dp, null);
        doneIcon.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
        TextView doneText = (TextView) actionBarView.findViewById(R.id.save_button);
        doneText.setCompoundDrawables(null, null, doneIcon, null);
        doneText.setText(text);
        if (text == null) {
            doneText.setVisibility(View.GONE);
        }

        actionBar.setDisplayOptions(
                android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM,
                android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                        | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME
                        | android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(actionBarView,
                new android.support.v7.app.ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);
        return doneText;
    }
}
