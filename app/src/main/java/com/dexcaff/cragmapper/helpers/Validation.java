package com.dexcaff.cragmapper.helpers;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dexcaff.cragmapper.models.Crag;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.25
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.helpers
 */

public class Validation {
    private Context mContext;

    public Validation(Context context) {
        mContext = context;
    }

    private static ObjectAnimator getBackgroundAnimSet(View view, String setAttribute) {
        int colorStart = view.getSolidColor();
        int colorEnd = 0x55ff8585;
        ObjectAnimator redFade = ObjectAnimator.ofInt(view, setAttribute, colorStart, colorEnd);
        redFade.setDuration(300);
        redFade.setEvaluator(new ArgbEvaluator());
        redFade.setRepeatCount(5);
        redFade.setRepeatMode(ObjectAnimator.REVERSE);
        return redFade;
    }

    public static boolean validateItems(View[] views) {
        for (View view : views) {
            if (view instanceof TextView) {
                if (((TextView) view).getText().toString().isEmpty()) {
                    return false;
                }
                getBackgroundAnimSet(view, "backgroundColor").start();
            }
        }
        return true;
    }

    public static boolean isCragValid(Crag crag) {
        String cragImage = (String) crag.properties.get(Crag.KEY_IMAGE);
        String cragTitle = (String) crag.properties.get(Crag.KEY_TITLE);

        if (cragTitle == null || cragTitle.isEmpty()) {
            return false;
        }
        if (cragImage == null || cragImage.isEmpty()) {
            return false;
        }
        return true;
    }
}
