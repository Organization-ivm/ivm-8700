/*
 * @ProjectName: 智能楼宇
 * @Copyright: 2013 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2016-4-18 下午3:58:06
 * @Description: 本内容仅限于杭州海康威视系统技术公有限司内部使用，禁止转发.
 */
package com.ivms.ivms8700.playback;

/**
 * <p></p>
 * @author lvlingdi 2016-4-18 下午3:58:06
 * @version V1.0   
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2016-4-18
 * @modify by reason:{方法名}:{原因}
 */
public class ConstantPlayBack {
    private static final int ERR_BASE = 1000;
    /**
     * 启动取流失败
     * */
    public static final int START_RTSP_FAIL = ERR_BASE;
    /**
     * 启动取流成功
     * */
    public static final int START_RTSP_SUCCESS = ERR_BASE + 1;
    /**
     * 暂停失败
     * */
    public static final int PAUSE_FAIL = ERR_BASE + 2;
    /**
     * 暂停成功
     * */
    public static final int PAUSE_SUCCESS = ERR_BASE + 3;
    /**
     * 恢复播放失败
     * */
    public static final int RESUEM_FAIL = ERR_BASE + 4;
    /**
     * 恢复播放成功
     * */
    public static final int RESUEM_SUCCESS = ERR_BASE + 5;
    /**
     * 启动播放失败
     * */
    public static final int START_OPEN_FAILED = ERR_BASE + 6;
    /**
     * 回放成功
     * */
    public static final int PLAY_DISPLAY_SUCCESS = ERR_BASE + 7;
    /**
     * SD卡不可用
     * */
    public static final int SD_CARD_UN_USEABLE = ERR_BASE + 8;
    /**
     * SD卡空间不足
     * */
    public static final int SD_CARD_SIZE_NOT_ENOUGH = ERR_BASE + 9;
    /**
     * 非播放状态不能抓怕
     */
    public static final int CAPTURE_FAILED_NPLAY_STATE = ERR_BASE + 10;
    /**
     * 非播放状态不能暂停
     */
    public static final int PAUSE_FAIL_NPLAY_STATE = ERR_BASE + 11;
    /**
     * 非暂停状态不需要恢复
     */
    public static final int RESUEM_FAIL_NPAUSE_STATE = ERR_BASE + 12;
    /**
     * 时间条刷新
     */
    public static final int MSG_REMOTELIST_UI_UPDATE = ERR_BASE + 13;
}
