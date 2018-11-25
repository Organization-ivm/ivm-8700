package com.ivms.ivms8700.live;

/**
 * Created by hanshuangwu on 2016/2/1.
 */
public class ConstantLive {
	
	private static final int BASE = 1000;
	/**
	 * 主码流名称
	 */
	public static final String MAIN_STREAM_NAME = "MAIN";
	/**
	 * 子码流名称
	 */
	public static final String SUB_STREAM_NAME = "SUB";
	/**
	 * 主码流标签(高清)
	 */
	public static final int MAIN_HING_STREAM = 1;
	/**
	 * 主码流标签(流畅)
	 */
	public static final int MAIN_STANDARD_STREAM = 2;
	/**
	 * 子码流标签(标清)
	 * */
	public static final int SUB_STREAM = 3;
	
	/**
	 * RTSP链接失败
	 * */
	public static final int RTSP_FAIL = BASE + 4;
	/**
	 * 取流成功
	 */
	public static final int RTSP_SUCCESS = BASE + 5;
	/**
	 * 播放成功
	 * */
	public static final int PLAY_DISPLAY_SUCCESS = BASE + 6;
	public static final int START_OPEN_FAILED = BASE + 7;
	public static final int STOP_SUCCESS = BASE + 8;
	/**
	 * SD卡不可用
	 * */
	public static final int SD_CARD_UN_USEABLE = BASE + 9;
	/**
	 * SD卡空间不足
	 * */
	public static final int SD_CARD_SIZE_NOT_ENOUGH = BASE + 10;
	/**
	 * 非播放状态不能抓拍
	 */
	public static final int CAPTURE_FAILED_NPLAY_STATE = BASE + 11;
}
