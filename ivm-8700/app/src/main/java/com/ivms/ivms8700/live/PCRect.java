/** 
 * @Title PCRect.java 
 * @Package com.mcu.iVMS.business.component.play.param.p 
 * @Description  
 * @Copyright Hikvision Digital Technology Co., Ltd. All Right Reserved  
 * @author  
 * @date 2014-10-20 下午8:18:39 
 * @version 1.0
 * @Modification History: Date  Author  Version  Description
 */

package com.ivms.ivms8700.live;

/**
 * @Class PCRect
 * @Description
 * @author
 * @date 2014-10-20 下午8:18:39
 */

public class PCRect
{
    private float mLeft   = 0;
    private float mTop    = 0;
    private float mRight  = 0;
    private float mBottom = 0;
    
    public PCRect(float l, float t, float r, float b)
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
