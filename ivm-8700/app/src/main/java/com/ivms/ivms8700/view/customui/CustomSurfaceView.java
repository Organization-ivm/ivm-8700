package com.ivms.ivms8700.view.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.hikvision.sdk.net.bean.CustomRect;

/**
 * <p>
 * 视频在此控件中播放
 * </p>
 * @author jiangfei5 2016年4月5日 下午4:05:31
 * @version V1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2016年4月5日
 * @modify by reason:{方法名}:{原因}
 */
public class  CustomSurfaceView extends SurfaceView {

    private static final int INVALID_POINTER = -1;
    private static final float UNIT_SCALE_RATIO = 0.003f;

    private static final int TOUCH_SLOP = 30;
    /**
     * 最大放大倍数
     */
    private static final int MAX_SCALE = 8;
    /**
     * 双击事件间隔
     */
    private static final int DOUBLE_CLICK_TIME = 300;

    private float mLastMotionY = 0;
    private float mLastMotionX = 0;
    private float mRatioX = 1;
    private float mRatioY = 1;

    private float mLastDis = 0;
    private float mLastScale = 1;
    private TouchMode mClickMode = TouchMode.NONE;
    private int mActionPointerId = INVALID_POINTER;
    private final com.hikvision.sdk.net.bean.CustomRect mOriginalRect = new com.hikvision.sdk.net.bean.CustomRect();
    private final com.hikvision.sdk.net.bean.CustomRect mVirtualRect = new com.hikvision.sdk.net.bean.CustomRect();
    private OnZoomListener mZoomListener = null;
    /**
     * 判断是否在等待双击 false - 表示单击，true - 表示双击
     */
    private boolean mWaitDouble = true;
    /**
     * 用于判断click事件
     */
    private float mLastClickX;
    /**
     * 用于判断click事件
     */
    private float mLastClickY;

    /**
     * 单拍手势监听
     */
    public CustomSurfaceView(Context context) {
        super(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /***
     * 设置电子放大监听，设置“null”时表示取消电子放大监听，设置有效的监听时，当前CustomSurfaceview会截获父控件的touch事件。
     * @param listener 监听者
     */
    public void setOnZoomListener(OnZoomListener listener) {
        mZoomListener = listener;

        if (mZoomListener == null) {
            mVirtualRect.setValue(mOriginalRect.getLeft(), mOriginalRect.getTop(), mOriginalRect.getRight(),
                    mOriginalRect.getBottom());

            mLastMotionY = 0;
            mLastMotionX = 0;
            mLastDis = 0;
            mRatioX = 1;
            mRatioY = 1;
            mLastScale = 1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mZoomListener == null) {
            return false;
        }

        if (!click(ev)) {// 点击事件未被“双击事件”消耗
            zoom(ev);
        }

        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mOriginalRect.setValue(l, t, r, b);
        if (changed) {
            mVirtualRect.setValue(l, t, r, b);
        }

    }

    private boolean click(MotionEvent ev) {
        if (ev.getPointerCount() != 1) {
            return false;
        }

        final int action = ev.getAction();
        boolean isEventConsume = false;

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastClickX = ev.getX(0);
                mLastClickY = ev.getY(0);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float delateX = (ev.getX(0) - mLastClickX);
                float delateY = (ev.getY(0) - mLastClickY);
                if (isClick(delateX, delateY)) {
                    if (mWaitDouble) {
                        mWaitDouble = false;
                        postDelayed(new ProcessSingleClick(), DOUBLE_CLICK_TIME);
                    } else {
                        mWaitDouble = true;

                        if (mLastScale == MAX_SCALE) {
                            midPointDoubleClick(ev);
                            scale(1);
                        } else {
                            midPointDoubleClick(ev);
                            scale(MAX_SCALE);
                        }

                        isEventConsume = true;

                    }
                }

                break;
            default:
                break;
        }

        return isEventConsume;

    }

    private void zoom(MotionEvent ev) {
        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mClickMode = TouchMode.ZOOM_DRAG;

                if (ev.getPointerCount() < 1) {
                    return;
                }

                mActionPointerId = ev.getPointerId(0);

                if (mActionPointerId < 0) {
                    return;
                }

                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (TouchMode.ZOOM_DRAG == mClickMode) {
                    final int index = ev.findPointerIndex(mActionPointerId);
                    if (index < 0) {
                        return;
                    }

                    final float x = ev.getX(index);
                    final float y = ev.getY(index);

                    move(mLastMotionX, mLastMotionY, x, y);

                    mLastMotionX = x;
                    mLastMotionY = y;
                } else if (TouchMode.ZOOM_SCALE == mClickMode) {
                    if (ev.getPointerCount() != 2) {
                        return;
                    }

                    float dis = spacing(ev);
                    float scale = mLastScale + (dis - mLastDis) * UNIT_SCALE_RATIO;

                    mLastDis = dis;

                    if (scale < 1) {
                        scale = 1;
                    }

                    if (scale > MAX_SCALE) {
                        scale = MAX_SCALE;
                    }

                    scale(scale);
                    midPoint(ev);

                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mLastDis = spacing(ev);
                mClickMode = TouchMode.ZOOM_SCALE;
                midPoint(ev);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mClickMode = TouchMode.ZOOM_DRAG;
                break;
        }
    }

    private float spacing(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private void scale(float newScale) {
        float w = mOriginalRect.getWidth() * newScale;
        float h = mOriginalRect.getHeight() * newScale;

        float newL = mVirtualRect.getLeft() - mRatioX * (w - mVirtualRect.getWidth());
        float newT = mVirtualRect.getTop() - mRatioY * (h - mVirtualRect.getHeight());
        float newR = newL + w;
        float newB = newT + h;

        mVirtualRect.setValue(newL, newT, newR, newB);

        judge(mOriginalRect, mVirtualRect);

        if (mZoomListener != null) {
            mLastScale = newScale;
            mZoomListener.onZoomChange(mOriginalRect, mVirtualRect);
        }
    }

    private void midPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);

        mRatioX = Math.abs(x / 2 - mVirtualRect.getLeft()) / mVirtualRect.getWidth();
        mRatioY = Math.abs(y / 2 - mVirtualRect.getTop()) / mVirtualRect.getHeight();

    }

    private void midPointDoubleClick(MotionEvent event) {
        float x = event.getX(0);
        float y = event.getY(0);

        mRatioX = Math.abs(x - mVirtualRect.getLeft()) / mVirtualRect.getWidth();
        mRatioY = Math.abs(y - mVirtualRect.getTop()) / mVirtualRect.getHeight();
    }

    private void move(float lastX, float lastY, float curX, float curY) {

        final float deltaX = curX - lastX;
        final float deltaY = curY - lastY;

        float left = mVirtualRect.getLeft();
        float top = mVirtualRect.getTop();
        float right = mVirtualRect.getRight();
        float bottom = mVirtualRect.getBottom();

        float newL = left + deltaX;
        float newT = top + deltaY;
        float newR = right + deltaX;
        float newB = bottom + deltaY;

        mVirtualRect.setValue(newL, newT, newR, newB);

        judge(mOriginalRect, mVirtualRect);

        if (mZoomListener != null) {
            mZoomListener.onZoomChange(mOriginalRect, mVirtualRect);
        }

    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);

        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
        mLastMotionX = ev.getX(newPointerIndex);
        mLastMotionY = ev.getY(newPointerIndex);
        if (pointerId == mActionPointerId) {
            mActionPointerId = ev.getPointerId(newPointerIndex);

        }

    }

    private void judge(com.hikvision.sdk.net.bean.CustomRect oRect, com.hikvision.sdk.net.bean.CustomRect curRect) {

        float oL = oRect.getLeft();
        float oT = oRect.getTop();
        float oR = oRect.getRight();
        float oB = oRect.getBottom();

        float newL = curRect.getLeft();
        float newT = curRect.getTop();
        float newR;
        float newB;

        float newW = curRect.getWidth();
        float newH = curRect.getHeight();

        if (newL > oL) {
            newL = oL;
        }
        newR = newL + newW;

        if (newT > oT) {
            newT = oT;
        }
        newB = newT + newH;

        if (newR < oR) {
            newR = oR;
            newL = oR - newW;
        }

        if (newB < oB) {
            newB = oB;
            newT = oB - newH;
        }
        curRect.setValue(newL, newT, newR, newB);
    }

    public interface OnZoomListener {
        void onZoomChange(com.hikvision.sdk.net.bean.CustomRect oRect, CustomRect curRect);
    }

    private enum TouchMode {
        NONE, ZOOM_DRAG, ZOOM_SCALE
    }

    private class ProcessSingleClick implements Runnable {

        public void run() {
            if (!mWaitDouble) {
                mWaitDouble = true;

                // 单击

            }
        }
    }

    /***
     * 判断是否是点击事件
     * @param deltaX 点击位置X坐标
     * @param deltaY 点击位置Y坐标
     * @return 是否为点击
     */
    private boolean isClick(float deltaX, float deltaY) {
        return !((Math.abs(deltaX) > TOUCH_SLOP) || (Math.abs(deltaY) > TOUCH_SLOP));
    }

}
