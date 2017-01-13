package com.dexcaff.cragmapper;

import android.content.Context;
import android.view.SurfaceView;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.13
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper
 */

public class EditCragImageSurface extends SurfaceView implements Runnable {
    private Thread mThread = null;
    private boolean mCanDraw = false;

    public EditCragImageSurface(Context context) {
        super(context);
    }

    @Override
    public void run() {
        while (mCanDraw) {

        }
    }

    public void pause() {
        mCanDraw = false;

        while (true) {
            try {
                mThread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mThread = null;
    }

    public void resume() {
        mCanDraw = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
