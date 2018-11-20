package com.ivms.ivms8700.view.customui;

/**
 * @Title CustomRect.java
 * @Package com.mcu.iVMS.ui.component.zoom
 * @Description 电子放大矩形信息，存储窗口坐标和尺寸
 * @Copyright Hikvision Digital Technology Co., Ltd. All Right Reserved
 * @author wuchunyuan
 * @date 2014-9-11 下午12:43:01
 * @version 1.0
 * @Modification History: Date  Author  Version  Description
 */


/**
 * @Class CustomRect
 * @Description 电子放大矩形信息，存储窗口坐标和尺寸
 * @author wuchunyuan
 * @date 2014-9-11 下午12:43:01
 */

public class CustomRect
{
    private float mLeft   = 0;
    private float mTop    = 0;
    private float mRight  = 0;
    private float mBottom = 0;

    public void setValue(float l, float t, float r, float b)
    {
        mLeft = l;
        mTop = t;
        mRight = r;
        mBottom = b;
    }

    /**
     * @function getLeft
     * @Description 该值小于或等于0
     * @author wuchunyuan
     * @date 2014-9-11 下午8:24:39
     * @return x坐标
     */
    public float getLeft()
    {
        return mLeft;
    }

    /**
     * @function getTop
     * @Description 该值小于或等于0
     * @author wuchunyuan
     * @date 2014-9-11 下午8:27:01
     * @return y坐标
     */
    public float getTop()
    {
        return mTop;
    }

    public float getRight()
    {
        return mRight;
    }

    public float getBottom()
    {
        return mBottom;
    }

    public float getWidth()
    {
        return (mRight - mLeft);
    }

    public float getHeight()
    {
        return (mBottom - mTop);
    }
}
