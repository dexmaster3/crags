package com.dexcaff.cragmapper.libs;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dexcaff.cragmapper.db.NodeContract;
import com.dexcaff.cragmapper.models.Node;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.06
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.libs
 */

public class NodeCircle extends View {
    private Paint mPaint;
    private RectF mCircleBounds = new RectF();
    public NodeCircle(Context context) {
        super(context);
    }
    public NodeCircle(Context context, AttributeSet attrs, Node node) {
        super(context, attrs);

        float node_x = (float) node.properties.get(NodeContract.NodeEntry.COLUMN_NAME_X_COORD);
        float node_y = (float) node.properties.get(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD);
        mCircleBounds.set(node_x, node_y, node_x + 10, node_y + 10);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(10);
        int duration = 5000;
        ObjectAnimator anim = ObjectAnimator.ofObject(this, "color", new ArgbEvaluator(), 0xffff0000, 0xff00ff00, 0xff0000ff);
        anim.setDuration(duration).start();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mCircleBounds, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Set dimensions for text, pie chart, etc
        //
        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        float diameter = Math.min(ww, hh);
        mCircleBounds = new RectF(
                0.0f,
                0.0f,
                diameter,
                diameter);
    }
}
