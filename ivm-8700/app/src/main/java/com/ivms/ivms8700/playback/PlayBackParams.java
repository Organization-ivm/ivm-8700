/*
 * @ProjectName: 智能楼宇
 * @Copyright: 2013 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2016-4-18 下午2:03:44
 * @Description: 本内容仅限于杭州海康威视系统技术公有限司内部使用，禁止转发.
 */
package com.ivms.ivms8700.playback;

import android.view.SurfaceView;

import com.hik.mcrsdk.rtsp.ABS_TIME;

/**
 * <p>回放时传递的参数类</p>
 * @author lvlingdi 2016-4-18 下午2:03:44
 * @version V1.0   
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2016-4-18
 * @modify by reason:{方法名}:{原因}
 */
public class PlayBackParams {
    /**
     * 播放控件
     * */
    public SurfaceView surfaceView;
    /**
     * 回放地址
     * */
    public String url;
    /**
     * 登录设备的用户名
     * */
    public String name;
    /**
     * 登录设备的密码
     * */
    public String passwrod;
    /**
     * 回放开始时间
     * */
    public ABS_TIME startTime;
    /**
     * 回放结束时间
     * */
    public ABS_TIME endTime;

}
