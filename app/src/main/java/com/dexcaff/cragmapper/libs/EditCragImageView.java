package com.dexcaff.cragmapper.libs;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.db.NodeContract;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.13
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.libs
 */

public class EditCragImageView extends View {
    private final static String TAG = "EditCragImageView";
    private Crag mCurrentCrag;
    private Node mTempNode;
    private float mScale, mStartW, mStartH;

    private Rect mContentRect = new Rect();
    private BitmapDrawable mBackground;
    private ShapeDrawable mDrawableOval = new ShapeDrawable(new OvalShape());
    private Paint mNodePaint = new Paint();
    private ShapeDrawable mNodeShape = new ShapeDrawable(new OvalShape());
    private RectF mNodeRectF = new RectF();
    private Rect mNodeRect = new Rect();

    public EditCragImageView(Context context) {
        this(context, new Crag(0, "", "", 0f));
    }

    @TargetApi(16)
    public EditCragImageView(Context context, Crag currentCrag) {
        super(context);
        mCurrentCrag = currentCrag;
        String originalImage = (String) mCurrentCrag.properties.get(CragContract.CragEntry.COLUMN_NAME_IMAGE);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mBackground = new BitmapDrawable(getResources(), originalImage);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBackground.setBounds(mContentRect);
        mBackground.draw(canvas);

        drawNodes(canvas);

        mDrawableOval.setBounds(10, 50, canvas.getWidth() / 4, canvas.getHeight() / 8);
        mDrawableOval.getPaint().setColor(Color.BLUE);
        mDrawableOval.setAlpha(150);
        mDrawableOval.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Rect imageRect = new Rect();
        getScaledImageRect().round(imageRect);
        mContentRect.set(imageRect);
    }

    private RectF getScaledImageRect() {
        Bitmap bmp = mBackground.getBitmap();
        mStartW = getPaddingLeft();
        mStartH = getPaddingTop();
        float bmpH = bmp.getHeight();
        float bmpW = bmp.getWidth();
        float ratio = bmpW / bmpH;
        float viewH = getHeight() - (getPaddingTop() + getPaddingBottom());
        float viewW = getWidth() - (getPaddingLeft() + getPaddingRight());
        float viewR = viewW / viewH;
        if (viewR > ratio) {
            //Viewport is wider -> scale image by max height
            mScale = viewH / bmpH;
            viewW = mScale * bmpW;
            mStartW = (getWidth() - viewW) / 2;
        } else {
            //Viewport is taller
            mScale = viewW / bmpW;
            viewH = mScale * bmpH;
            mStartH = (getHeight() - viewH) / 2;
        }
        return new RectF(mStartW, mStartH, viewW + mStartW, viewH + mStartH);
    }

    private void drawNodes(Canvas canvas) {
        if (mTempNode != null) {
            drawNode(canvas, mTempNode);
        }
        HashMap<String, Node> nodes = (HashMap<String, Node>) mCurrentCrag.properties.get(NodeContract.NodeEntry.TABLE_NAME);
        for (Map.Entry<String, Node> entry : nodes.entrySet()){
            Node node = entry.getValue();
            drawNode(canvas, node);
        }
    }

    private void drawNode(Canvas canvas, Node node) {
        float[] coords = getScaledNodeCoords(node);
        mNodeRectF.set(coords[0] - 20, coords[1] - 20, coords[0] + 20, coords[1] + 20);
        mNodeRectF.round(mNodeRect);
        mNodeShape.setBounds(mNodeRect);
        mNodePaint.setColor(Color.MAGENTA);
        mNodeShape.getPaint().set(mNodePaint);
        mNodeShape.draw(canvas);
    }

    public boolean drawTempNode(MotionEvent event) {
        float xcoord = event.getX();
        float ycoord = event.getY();
        xcoord = (xcoord - mStartW) / mScale;
        ycoord = (ycoord - mStartH) / mScale;
        if (xcoord < 0 || ycoord < 0 || event.getX() > mContentRect.right || event.getY() > mContentRect.bottom) {
            mTempNode = null;
            return false;
        } else {
            mTempNode = new Node(-1, (long) mCurrentCrag.properties.get(CragContract.CragEntry._ID), xcoord, ycoord);
        }
        invalidate();
        return true;
    }

    public void removeTempNode() {
        mTempNode = null;
        invalidate();
    }

    private float[] getScaledNodeCoords(Node node) {
        float[] coords = new float[2];
        coords[0] = (float) node.properties.get(NodeContract.NodeEntry.COLUMN_NAME_X_COORD);
        coords[1] = (float) node.properties.get(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD);
        coords[0] *= mScale;
        coords[1] *= mScale;
        coords[0] += mContentRect.left;
        coords[1] += mContentRect.top;
        return coords;
    }
}
