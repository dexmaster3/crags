package com.dexcaff.cragmapper.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dexcaff.cragmapper.EditCragImageActivity;
import com.dexcaff.cragmapper.R;
import com.dexcaff.cragmapper.helpers.Image;
import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.13
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.views
 */

public class EditCragImageView extends View {
    private final static String TAG = "EditCragImageView";
    private Crag mCurrentCrag;
    private Node mTempNode;
    private Context mContext;
    private float mScale, mOrigScale, mStartW, mStartH;

    private GestureDetectorCompat mGestureDetector;
    private float mTempX, mTempY;
    private float[] mOriginalSize;

    private Rect mContentRect = new Rect();
    private BitmapDrawable mBackground;
    private Drawable mNodeDrawable;
    private RectF mNodeRectF = new RectF();
    private Rect mNodeRect = new Rect();
    private int mNodeAlpha = 0;

    public EditCragImageView(Context context) {
        this(context, new Crag(0, "", "", 0f));
    }

    @TargetApi(16)
    public EditCragImageView(Context context, Crag currentCrag) {
        super(context);
        mContext = context;
        mCurrentCrag = currentCrag;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        String originalImage = (String) mCurrentCrag.properties.get(Crag.KEY_IMAGE);
        mOriginalSize = Image.getOriginalImageSize(context, originalImage);
        Point size = Image.getScreenSize(context);
        Bitmap sampledBitmap = Image.getSampledRotatedBitmap(mContext, originalImage, size.x/2, size.y/2);
        mBackground = new BitmapDrawable(getResources(), sampledBitmap);
        mNodeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.nodeoval, null);
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBackground.setBounds(mContentRect);
        mBackground.draw(canvas);

        drawNodes(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mGestureDetector.onTouchEvent(event);
        return retVal || super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Rect imageRect = new Rect();
        getScaledImageRect().round(imageRect);
        mContentRect.set(imageRect);
    }

    public void setNodeAlpha(int value) {
        mNodeAlpha = value;
        invalidate();
    }

    public void addAfterTempNodeSaved(Node node) {
        HashMap<String, Node> nodes = (HashMap<String, Node>) mCurrentCrag.properties.get(Node.TABLE_NAME);
        nodes.put(Long.toString((long)node.properties.get(Node._ID)), node);
        mCurrentCrag.properties.put(Node.TABLE_NAME, nodes);
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
            mOrigScale = mOriginalSize[1] / viewH;
            mScale = viewH / bmpH;
            viewW = mScale * bmpW;
            mStartW = (getWidth() - viewW) / 2;
        } else {
            //Viewport is taller
            mOrigScale = mOriginalSize[0] / viewW;
            mScale = viewW / bmpW;
            viewH = mScale * bmpH;
            mStartH = (getHeight() - viewH) / 2;
        }
        return new RectF(mStartW, mStartH, viewW + mStartW, viewH + mStartH);
    }

    private void drawNodes(Canvas canvas) {
        if (mTempNode != null) {
            drawNode(canvas, mTempNode, true);
        }
        HashMap<String, Node> nodes = (HashMap<String, Node>) mCurrentCrag.properties.get(Node.TABLE_NAME);
        for (Map.Entry<String, Node> entry : nodes.entrySet()){
            Node node = entry.getValue();
            drawNode(canvas, node, false);
        }
    }

    private void drawNode(Canvas canvas, Node node, boolean tempNode) {
        float[] coords = getScaledNodeCoords(node);
        mNodeRectF.set(coords[0] - 20, coords[1] - 20, coords[0] + 20, coords[1] + 20);
        mNodeRectF.round(mNodeRect);
        mNodeDrawable.setBounds(mNodeRect);
        mNodeDrawable.setAlpha(255);
        if (tempNode) {
            mNodeDrawable.setAlpha(mNodeAlpha);
        }
        mNodeDrawable.draw(canvas);
    }

    private boolean drawTempNode(MotionEvent event) {
        mTempX = event.getX();
        mTempY = event.getY();
        float xcoord = (mTempX - mStartW) * mOrigScale;
        float ycoord = (mTempY - mStartH) * mOrigScale;
        if (xcoord < 0 || ycoord < 0 || event.getX() > mContentRect.right || event.getY() > mContentRect.bottom) {
            return false;
        }
        mTempNode = new Node(-1, (long) mCurrentCrag.properties.get(Crag._ID), xcoord, ycoord);
        invalidate();
        return true;
    }

    private boolean drawTempNode(float distanceX, float distanceY) {
        mTempX -= distanceX;
        mTempY -= distanceY;
        float xcoord = (mTempX - mStartW) * mOrigScale;
        float ycoord = (mTempY - mStartH) * mOrigScale;
        if (xcoord < 0 || ycoord < 0 || mTempX > mContentRect.right || mTempY > mContentRect.bottom) {
            return false;
        }
        mTempNode = new Node(-1, (long) mCurrentCrag.properties.get(Crag._ID), xcoord, ycoord);
        invalidate();
        return true;
    }

    public Node getTempNode() throws Exception {
        if (mTempNode == null) {
            throw new Exception("mTempNode is somehow not set on Save click");
        }
        return mTempNode;
    }

    public void removeTempNode() {
        mTempNode = null;
        invalidate();
    }

    private float[] getScaledNodeCoords(Node node) {
        float[] coords = new float[2];
        coords[0] = (float) node.properties.get(Node.KEY_X_COORD);
        coords[1] = (float) node.properties.get(Node.KEY_Y_COORD);
        coords[0] /= mOrigScale;
        coords[1] /= mOrigScale;
        coords[0] += mContentRect.left;
        coords[1] += mContentRect.top;
        return coords;
    }

    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            drawTempNode(distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (drawTempNode(e)) {
                ((EditCragImageActivity) mContext).showAddNodeActionBar();
            }
            return true;
        }
    };
}
