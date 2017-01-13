package com.dexcaff.cragmapper.libs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
    private int mColor = Color.MAGENTA;
    private int mAlpha;
    private RectF mCircleBoundsF = new RectF();
    private Rect mCircleBounds = new Rect();

    public NodeCircle(Context context) {
        super(context);
    }

    public NodeCircle(Context context, AttributeSet attrs, Node node) {
        super(context, attrs);

        float node_x = (float) node.properties.get(NodeContract.NodeEntry.COLUMN_NAME_X_COORD);
        float node_y = (float) node.properties.get(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD);
        mCircleBoundsF.set(node_x, node_y, node_x + 90, node_y + 90);
        mCircleBoundsF.round(mCircleBounds);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(mColor);
    }

    public void setColor(int color) {
        Paint lilpaint = new Paint();
        lilpaint.setColor(color);
//        mDrawableOval.getPaint().set(lilpaint);
        invalidate();
    }

    public void setAlpha(int alpha) {
        mAlpha = alpha;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ShapeDrawable mDrawableOval = new ShapeDrawable(new OvalShape());
        mDrawableOval.setBounds(mCircleBounds);
        mDrawableOval.getPaint().set(mPaint);
        mDrawableOval.setAlpha(150);
        mDrawableOval.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Size + padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        float diameter = Math.min(ww, hh);
        mCircleBoundsF = new RectF(
                0.0f,
                0.0f,
                diameter,
                diameter);
    }
}
