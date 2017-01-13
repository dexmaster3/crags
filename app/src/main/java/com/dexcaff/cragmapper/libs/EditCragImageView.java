package com.dexcaff.cragmapper.libs;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.13
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.libs
 */

public class EditCragImageView extends TouchImageView {
    //ToDo try adding cust view nodes, onto the xml layout
    private BitmapDrawable mBackground;

    public EditCragImageView(Context context) {
        super(context);
    }

    @TargetApi(16)
    public EditCragImageView(Context context, String backgroundFile) {
        super(context);
        mBackground = new BitmapDrawable(getResources(), backgroundFile);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(mBackground);
        } else {
            setBackground(mBackground);
        }
    }

    public void addNode(NodeCircle node) {

    }
}
