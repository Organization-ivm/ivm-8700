/*
 * @ProjectName: 智能楼宇
 * @Copyright: 2013 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2016-4-18 下午2:00:23
 * @Description: 本内容仅限于杭州海康威视系统技术公有限司内部使用，禁止转发.
 */
package com.ivms.ivms8700.playback;

/**
 * <p>回放回调接口</p>
 * @author lvlingdi 2016-4-18 下午2:00:23
 * @version V1.0   
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2016-4-18
 * @modify by reason:{方法名}:{原因}
 */
public interface PlayBackCallBack {

    /**
     * 播放引擎消息回调接口
     * @author lvlingdi 2016-4-18 下午2:00:45
     * @param message 消息
     */
    public void onMessageCallback(int message);
}
