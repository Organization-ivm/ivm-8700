/*
 * @ProjectName: 智能楼宇
 * @Copyright: 2013 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2016-4-18 下午1:57:11
 * @Description: 本内容仅限于杭州海康威视系统技术公有限司内部使用，禁止转发.
 */
package com.ivms.ivms8700.playback;

import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hik.mcrsdk.rtsp.ABS_TIME;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.rtsp.RtspClientCallback;
import com.hikvision.sdk.utils.Utils;
import com.ivms.ivms8700.live.PCRect;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.Player.MPInteger;
import org.MediaPlayer.PlayM4.Player.MPRect;
import org.MediaPlayer.PlayM4.Player.MPSystemTime;
import org.MediaPlayer.PlayM4.PlayerCallBack.PlayerDisplayCB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p></p>
 * @author lvlingdi 2016-4-18 下午1:57:11
 * @version V1.0   
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2016-4-18
 * @modify by reason:{方法名}:{原因}
 */
public class PlayBackControl implements RtspClientCallback, PlayerDisplayCB {
    
    private static final String TAG = PlayBackControl.class.getSimpleName();
    
    
    /**
     * 播放库端口
     */
    private int                 mPlayerPort      = -1;
    /**
     * 初始化阶段
     */
    public final static int            PLAYBACK_INIT    = 0;
    /**
     * 取流阶段
     */
    public final  static int            PLAYBACK_STREAM  = 1;
    /**
     * 播放阶段
     */
    public final static int            PLAYBACK_PLAY    = 2;
    /**
     * 释放资源阶段
     */
    public final static int            PLAYBACK_RELEASE = 3;
    /**
     * 暂停阶段
     */
    public final static int            PLAYBACK_PAUSE   = 4;
    /**
     * 回放状态
     */
    public int                  mPlayBackState   = 0;
    /**
     * 创建播放库句柄
     */
    private Player mPlayerHandler;
    /**
     * 取流库句柄
     */
    private RtspClient mRtspClientHandle;
    private int                 mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
    private int                 mModel           = RtspClient.RTPRTSP_TRANSMODE;
    /**
     * 播放视图的对象
     */
    private SurfaceView         mSurfaceView;
    /**
     * 回放开始时间
     */
    private ABS_TIME mStartTime;
    /**
     * 回放结束时间
     */
    private ABS_TIME mEndTime;
    /**
     * 登录设备的用户名
     */
    private String              mName            = "";
    /**
     * 登录设备密码
     */
    private String              mPassword        = "";
    /**
     * 回放地址
     */
    private String          mUrl = "";
    /**
     * 回调对象
     */
    private PlayBackCallBack    mPlayBackCallBack;
    private File                mPictureFile;
    /**
     * 数据流
     */
    private ByteBuffer          mStreamHeadDataBuffer;
    /**
     * 文件输出流
     */
    private FileOutputStream    mRecordFileOutputStream;
    /**
     * 录像文件
     */
    private File                mRecordFile;
    /**
     * 是否正在录像
     */
    private boolean             mIsRecord        = false;
    /**
     * 设置SD卡使用限度，当小于256M时，提示SD卡内存不足，根据具体情况可以修改
     */
    private int                 mSDCardSize      = 256 * 1024 * 1024;
    /**
     * 播放流量
     */
    private long                mStreamRate;
    

    /**
     * 
     * @author lvlingdi 2016-4-18 下午2:01:48
     */
    public PlayBackControl() {
        init();
    }
    
    /**
     * 初始化
     * @author lvlingdi 2016-4-18 下午2:03:05
     */
    private void init() {
        mPlayerHandler = Player.getInstance();
        mRtspClientHandle = RtspClient.getInstance();
        mPlayBackState = PLAYBACK_INIT;
    }
    
    public void setPlayBackCallBack(PlayBackCallBack callBack) {
        mPlayBackCallBack = callBack;
    }
    
    /**
     * 开启回放
     * @author lvlingdi 2016-4-18 下午2:28:55
     * @param obj
     */
    public void startPlayBack(PlayBackParams obj) {
        boolean ret = checkParams(obj);
        if (!ret) {
            return;
        }
        mSurfaceView = obj.surfaceView;
        mStartTime = obj.startTime;
        mEndTime = obj.endTime;
        mName = obj.name;
        mPassword = obj.passwrod;
        mUrl = obj.url;

        if (PLAYBACK_STREAM == mPlayBackState) {
            return;
        }

        startRtsp();
    }
    
    private boolean checkParams(PlayBackParams obj) {
        if (null == obj) {
            Log.e(TAG, "checkParams():: obj is null");
            return false;
        }
        if (null == obj.name) {
            obj.name = "";
        }
        if (null == obj.passwrod) {
            obj.passwrod = "";
        }
        if (null == obj.surfaceView || null == obj.startTime || null == obj.endTime || null == obj.url
                || obj.url.equals("")) {
            Log.e(TAG, "checkParams():: params error");
            return false;
        }
        return true;
    }
    
    /**
     * 启动取流
     * 
     * @since V1.0
     */
    /**
     * 开启取流
     * @author lvlingdi 2016-4-18 下午2:32:49
     */
    private void startRtsp() {
        if (null == mRtspClientHandle) {
            Log.e(TAG, "startRtsp():: mRtspClientHandle is null");
            return;
        }

        mRtspEngineIndex = mRtspClientHandle.createRtspClientEngine(this, mModel);
        if (RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID == mRtspEngineIndex) {
            Log.e(TAG, "startRtsp():: mRtspEngineIndex error");
            return;
        }
        
        if (null == mName) {
            mName = "";
        }
        if (null == mPassword) {
            mPassword = "";
        }
        
        boolean ret = mRtspClientHandle.playbackByTime(mRtspEngineIndex, mUrl, mName, mPassword, mStartTime, mEndTime);
        if (!ret) {
            int errorCode = mRtspClientHandle.getLastError();
            mRtspClientHandle.releaseRtspClientEngineer(mRtspEngineIndex);
            if (null == mPlayBackCallBack) {
                Log.e(TAG, "startRtsp():: mPlayBackCallBack is null");
                return;
            } else {
                Log.e(TAG, "startRtsp()::playbackByTime() fail, errorCode is R" + errorCode);
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.START_RTSP_FAIL);
                return;
            }
        }

        mPlayBackState = PLAYBACK_STREAM;
        if (null == mPlayBackCallBack) {
            Log.e(TAG, "startRtsp():: mPlayBackCallBack is null");
        } else {
            mPlayBackCallBack.onMessageCallback(ConstantPlayBack.START_RTSP_SUCCESS);
        }
    }
    
    /**
     * 暂停方法
     * @author lvlingdi 2016-4-25 下午4:58:16
     */
    public void pausePlayBack() {
        if (PLAYBACK_PLAY != mPlayBackState) {
            if (mPlayBackCallBack != null) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.PAUSE_FAIL_NPLAY_STATE);
            }
            return;
        }

        if (null == mPlayerHandler) {
            return;
        }

        boolean ret = mPlayerHandler.pause(mPlayerPort, 1);
        if (!ret) {
            int errorCode = mPlayerHandler.getLastError(mPlayerPort);
            Log.e(TAG, "pausePlayBack():: pause() fail, errorCode is P" + errorCode);
            if (null == mPlayBackCallBack) {
                Log.e(TAG, "pausePlayBack():: mPlayBackCallBack is null");
                return;
            } else {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.PAUSE_FAIL);
                return;
            }
        }

        ret = mRtspClientHandle.pause(mRtspEngineIndex);
        if (!ret) {
            int errorCode = mRtspClientHandle.getLastError();
            mRtspClientHandle.releaseRtspClientEngineer(mRtspEngineIndex);
            Log.e(TAG, "pausePlayBack():: pause() fail, errorCode is R" + errorCode);
            if (null == mPlayBackCallBack) {
                Log.e(TAG, "pausePlayBack():: mPlayBackCallBack is null");
                return;
            } else {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.PAUSE_FAIL);
                return;
            }
        }

        mPlayBackState = PLAYBACK_PAUSE;
        if (null == mPlayBackCallBack) {
            Log.e(TAG, "pausePlayBack():: mPlayBackCallBack is null");
        } else {
            mPlayBackCallBack.onMessageCallback(ConstantPlayBack.PAUSE_SUCCESS);
        }
    }
    
    /**
     * 恢复播放
     * @author lvlingdi 2016-4-25 下午4:58:30
     */
    public void resumePlayBack() {
        if (PLAYBACK_PAUSE != mPlayBackState) {
            if (mPlayBackCallBack != null) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.RESUEM_FAIL_NPAUSE_STATE);
            }
            return;
        }

        boolean ret = mRtspClientHandle.resume(mRtspEngineIndex);
        if (!ret) {
            int errorCode = mRtspClientHandle.getLastError();
            mRtspClientHandle.releaseRtspClientEngineer(mRtspEngineIndex);
            Log.e(TAG, "resumePlayBack():: pause() fail, errorCode is R" + errorCode);
            if (null == mPlayBackCallBack) {
                Log.e(TAG, "resumePlayBack():: mPlayBackCallBack is null");
                return;
            } else {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.RESUEM_FAIL);
                return;
            }
        }

        ret = mPlayerHandler.pause(mPlayerPort, 0);
        if (!ret) {
            int errorCode = mPlayerHandler.getLastError(mPlayerPort);
            Log.e(TAG, "pausePlayBack():: pause() fail, errorCode is P" + errorCode);
            if (null == mPlayBackCallBack) {
                Log.e(TAG, "pausePlayBack():: mPlayBackCallBack is null");
            } else {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.PAUSE_FAIL);
                return;
            }
        }
        mPlayBackState = PLAYBACK_PLAY;
        if (null == mPlayBackCallBack) {
            Log.e(TAG, "resumePlayBack():: mPlayBackCallBack is null");
        } else {
            mPlayBackCallBack.onMessageCallback(ConstantPlayBack.RESUEM_SUCCESS);
        }
    }
    
    /**
     * 停止回放 void
     * @author lvlingdi 2016-4-18 下午4:51:38
     */
    public void stopPlayBack() {

        if (mIsRecord) {
            stopRecord();
        }

        if (null != mRtspClientHandle) {
            if (RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID != mRtspEngineIndex) {
                mRtspClientHandle.stopRtspProc(mRtspEngineIndex);
                mRtspClientHandle.releaseRtspClientEngineer(mRtspEngineIndex);
                mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
            }
        }

        closePlayer();
        mPlayBackState = PLAYBACK_RELEASE;
    }
    
    
    /**
     * 关闭播放库 void
     * @author lvlingdi 2016-4-18 下午4:53:37
     */
    private void closePlayer() {
        if (null != mPlayerHandler) {
            if (-1 != mPlayerPort) {
                boolean ret = mPlayerHandler.stop(mPlayerPort);
                if (!ret) {
                    Log.e(
                            TAG,
                            "closePlayer(): Player stop  failed!  errorCode is P"
                                    + mPlayerHandler.getLastError(mPlayerPort));
                }

                ret = mPlayerHandler.closeStream(mPlayerPort);
                if (!ret) {
                    Log.e(TAG, "closePlayer(): Player closeStream  failed!");
                }

                ret = mPlayerHandler.freePort(mPlayerPort);
                if (!ret) {
                    Log.e(TAG, "closePlayer(): Player freePort  failed!");
                }
                mPlayerPort = -1;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.MediaPlayer.PlayM4.PlayerCallBack.PlayerDisplayCB#onDisplay(int, byte[], int, int, int, int, int, int)
     */
    @Override
    public void onDisplay(int arg0, byte[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
        if (PLAYBACK_PLAY != mPlayBackState) {
            mPlayBackState = PLAYBACK_PLAY;
        }

        if (null != mPlayBackCallBack) {
            mPlayBackCallBack.onMessageCallback(ConstantPlayBack.PLAY_DISPLAY_SUCCESS);
        } else {
            Log.e(TAG, "onDisplay():: mPlayBackCallBack is null");
        }
    }

    /* (non-Javadoc)
     * @see com.hik.mcrsdk.rtsp.RtspClientCallback#onDataCallBack(int, int, byte[], int, int, int, int)
     */
    @Override
    public void onDataCallBack(int handle, int dataType, byte[] data, int length, int timeStamp, int packetNo, int useId) {
        mStreamRate += length;
        switch (dataType) {
            case RtspClient.DATATYPE_HEADER:
                boolean ret = processStreamHeader(data, length);
                if (!ret) {
                    Log.e(TAG, "MediaPlayer Header fail! such as:");
                    Log.e(TAG, "MediaPlayer Header length:" + length);
                    if (null != mPlayBackCallBack) {
                        mPlayBackCallBack.onMessageCallback(ConstantPlayBack.START_OPEN_FAILED);
                        return;
                    } else {
                        Log.e(TAG, "onDataCallBack():: mPlayBackCallBack is null");
                    }
                } else {
                    Log.e(TAG, "Player Header success!");
                }
            break;

            default:
                processStreamData(data, length);
            break;
        }

//        processRecordData(dataType, data, length);
    }

    /**
     * 处理数据流头
     * 
     * @param data
     * @param length
     * @return boolean
     * @since V1.0
     */
    private boolean processStreamHeader(byte[] data, int length) {
        if (-1 != mPlayerPort) {
            closePlayer();
        }
        return startPlayer(data, length);
    }

    /**
     * 处理数据，并向播放库塞数据
     * 
     * @param data
     * @param length void
     * @since V1.0
     */
    private void processStreamData(byte[] data, int length) {
        if (null == data || 0 == length) {
            Log.e(TAG, "processStreamData() Stream data error");
            return;
        }
        if (null != mPlayerHandler) {
            boolean ret = mPlayerHandler.inputData(mPlayerPort, data, length);
            if (!ret) {
                SystemClock.sleep(10);
            }
        }
    }
    /*
     * handle - - 引擎id opt - -回调消息，包括：RTSPCLIENT_MSG_PLAYBACK_FINISH,RTSPCLIENT_MSG_BUFFER_OVERFLOW
     * ,RTSPCLIENT_MSG_CONNECTION_EXCEPTION 三种 param1 - - 保留参数 param2 - - 保留参数 useId - - 用户数据，默认就是引擎id与handle相同
     */
    @Override
    public void onMessageCallBack(int handle, int opt, int param1, int param2, int useId) {
        if (opt == RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION) {
            Log.e(TAG, "onMessageCallBack():: RTSPCLIENT_MSG_CONNECTION_EXCEPTION");
            stopPlayBack();
            mPlayBackCallBack.onMessageCallback(RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION);
        }
    }
   
    /**
     * 启动播放库
     * 
     * @param data
     * @param len
     * @return boolean
     * @since V1.0
     */
    private boolean startPlayer(byte[] data, int len) {
        if (null == data || 0 == len) {
            Log.e(TAG, "startPlayer() fail data is null or len <= 0");
            return false;
        }

        if (null == mPlayerHandler) {
            Log.e(TAG, "startPlayer() fail ,mPlayerHandler is null");
            return false;
        }

        mPlayerPort = mPlayerHandler.getPort();
        if (-1 == mPlayerPort) {
            Log.e(TAG, "startPlayer() fail ,mPlayerPort is -1");
            return false;
        }

        boolean ret = mPlayerHandler.setStreamOpenMode(mPlayerPort, Player.STREAM_REALTIME);
        if (!ret) {
            int tempErrorCode = mPlayerHandler.getLastError(mPlayerPort);
            Log.e(TAG, "startPlayer():: mPlayerHandler.setStreamOpenMode() failed, errorCode is P"
                    + tempErrorCode);
            mPlayerHandler.freePort(mPlayerPort);
            mPlayerPort = -1;
            return false;
        }

        ret = mPlayerHandler.openStream(mPlayerPort, data, len, 2 * 1024 * 1024);
        if (!ret) {
            Log.e(TAG, "startPlayer() fail ,openStream fail");
            return false;
        }

        ret = mPlayerHandler.setDisplayCB(mPlayerPort, this);
        if (!ret) {
            Log.e(TAG, "startPlayer() fail ,setDisplayCB fail");
            return false;
        }

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (null == surfaceHolder) {
            Log.e(TAG, "startPlayer() fail ,surfaceHolder is null ");
            return false;
        }

        ret = mPlayerHandler.play(mPlayerPort, surfaceHolder);
        if (!ret) {
            Log.e(TAG, "startPlayer() fail ,play fail ");
            return false;
        }
        return true;
    }
    
    
    /**
     * 抓拍 void
     * 
     * @param filePath 存放文件路径
     * @param picName 抓拍时文件的名称
     * @return true-抓拍成功，false-抓拍失败
     * @since V1.0
     */
    public boolean capture(String filePath, String picName) {
        if (!Utils.isSDCardUsable()) {
            if (null != mPlayBackCallBack) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.SD_CARD_UN_USEABLE);
            }
            Log.e(TAG, "capture()：：SDCard 不能用");
            return false;
        }

        if (Utils.getSDCardRemainSize() <= mSDCardSize) {
            if (null != mPlayBackCallBack) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.SD_CARD_SIZE_NOT_ENOUGH);
            }
            return false;
        }

        if (PLAYBACK_PLAY != mPlayBackState) {
            if (null != mPlayBackCallBack) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.CAPTURE_FAILED_NPLAY_STATE);
            }
            return false;
        }

        byte[] pictureBuffer = getPictureOnJPEG();
        if (null == pictureBuffer || pictureBuffer.length == 0) {
            Log.e(TAG, "capture():: pictureBuffer is null or length 0");
            return false;
        }

        boolean ret = createPictureFile(filePath, picName);
        if (!ret) {
            pictureBuffer = null;
            Log.e(TAG, "capture():: createPictureFile() return false");
            return false;
        }

        ret = writePictureToFile(pictureBuffer, pictureBuffer.length);
        if (!ret) {
            pictureBuffer = null;
            removePictureFile();
            Log.e(TAG, "capture():: writePictureToFile() return false");
            return false;
        }
        return true;
    }

    /**
     * 获取JPEG图片数据
     * 
     * @return JPEG图片的数据.
     * @since V1.0
     */
    private byte[] getPictureOnJPEG() {
        if (null == mPlayerHandler) {
            Log.e(TAG, "getPictureOnJPEG():: mPlayerHandler is null");
            return new byte[0];
        }

        if (-1 == mPlayerPort) {
            Log.e(TAG, "getPictureOnJPEG():: mPlayerPort is Unavailable");
            return new byte[0];
        }

        int picSize = getPictureSize();
        if (picSize <= 0) {
            return new byte[0];
        }

        byte[] pictureBuffer = null;
        try {
            pictureBuffer = new byte[picSize];
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            pictureBuffer = null;
            return new byte[0];
        }

        MPInteger jpgSize = new MPInteger();

        boolean ret = mPlayerHandler.getJPEG(mPlayerPort, pictureBuffer, picSize, jpgSize);
        if (!ret) {
            Log.e(TAG, "getPictureOnJPEG():: mPlayerHandler.getJPEG() return false，errorCode is P"
                    + mPlayerHandler.getLastError(mPlayerPort));
            return new byte[0];
        }

        int jpegSize = jpgSize.value;
        if (jpegSize <= 0) {
            pictureBuffer = null;
            return new byte[0];
        }

        ByteBuffer jpgBuffer = ByteBuffer.wrap(pictureBuffer, 0, jpegSize);
        if (null == jpgBuffer) {
            pictureBuffer = null;
            return new byte[0];
        }

        return jpgBuffer.array();
    }

    /**
     * 获取JPEG图片大小
     * 
     * @return JPEG图片的大小.
     * @throws PlayerException
     * @throws MediaPlayerException MediaPlayer 异常
     * @since V1.0
     */
    private int getPictureSize() {
        MPInteger width = new MPInteger();
        MPInteger height = new MPInteger();
        boolean ret = mPlayerHandler.getPictureSize(mPlayerPort, width, height);
        if (!ret) {
            Log.e(TAG, "getPictureSize():: mPlayerHandler.getPictureSize() return false, errorCode is P"
                    + mPlayerHandler.getLastError(mPlayerPort));
            return 0;
        }
        int pictureSize = width.value * height.value * 3;
        return pictureSize;
    }

    /**
     * 创建图片文件
     * 
     * @param path 图片路径
     * @param fileName 图片名字
     * @return true - 图片创建成功 or false - 图片创建失败
     * @since V1.0
     */
    private boolean createPictureFile(String path, String fileName) {
        if (null == path || null == fileName || path.equals("") || fileName.equals("")) {
            return false;
        }

        String dirPath = createFileDir(path);
        if (null == dirPath || dirPath.equals("")) {
            return false;
        }

        try {
            mPictureFile = new File(dirPath + File.separator + fileName);
            if ((null != mPictureFile) && (!mPictureFile.exists())) {
                mPictureFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mPictureFile = null;
            return false;
        }
        return true;
    }

    /**
     * 抓拍图片写到SDCard
     * 
     * @param picData 图片数据
     * @param length 图片数据长度
     * @since V1.0
     */
    private boolean writePictureToFile(byte[] picData, int length) {
        if (null == picData || length <= 0) {
            return false;
        }

        if (null == mPictureFile) {
            return false;
        }

        FileOutputStream fOut = null;
        try {
            if (!mPictureFile.exists()) {
                mPictureFile.createNewFile();
            }
            fOut = new FileOutputStream(mPictureFile);
            fOut.write(picData, 0, length);
            fOut.flush();
            fOut.close();
            fOut = null;
        } catch (Exception e) {
            e.printStackTrace();
            fOut = null;
            mPictureFile.delete();
            mPictureFile = null;
            return false;
        }
        return true;
    }

    /**
     * 删除图片文件
     * 
     * @since V1.0
     */
    private void removePictureFile() {
        try {
            if (null == mPictureFile) {
                return;
            }
            mPictureFile.delete();
            mPictureFile = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹
     * 
     * @param path 文件路径
     * @return 文件夹路径
     * @since V1.0
     */
    private String createFileDir(String path) {
        if (null == path || path.equals("")) {
            return "";
        }
        File tempFile = null;
        try {
            tempFile = new File(path);
            if ((null != tempFile) && (!tempFile.exists())) {
                tempFile.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return tempFile.getAbsolutePath();
    }

    /**
     * 启动录像方法
     * 
     * @param filePath 录像文件路径
     * @param fileName 录像文件名称
     * @param isRpmPackage 录像时是否启用转封装
     * @return true-启动录像成功，false-启动录像失败
     * @since V1.0
     */
    public boolean startRecord(String filePath, String fileName) {

        if (!Utils.isSDCardUsable()) {
            if (null != mPlayBackCallBack) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.SD_CARD_UN_USEABLE);
            }
            return false;
        }

        if (Utils.getSDCardRemainSize() <= mSDCardSize) {
            if (null != mPlayBackCallBack) {
                mPlayBackCallBack.onMessageCallback(ConstantPlayBack.SD_CARD_SIZE_NOT_ENOUGH);
            }
            return false;
        }

        if (PLAYBACK_PLAY != mPlayBackState) {
            Log.e(TAG, "非播放状态不能录像");
            return false;
        }

        boolean ret = createRecordFile(filePath, fileName);
        if (!ret) {
            Log.e(TAG, "createRecordFile() fail ");
            return false;
        }

        ret = writeStreamHead(mRecordFile);
        if (!ret) {
            Log.e(TAG, "writeStreamHead() fail");
            removeRecordFile();
            return false;
        }

        mIsRecord = true;
        return true;
    }

    
    /**
     * 创建录像文件
     * 
     * @param path 文件路径
     * @param fileName 文件名
     * @return true - 创建成功 or false - 创建失败
     * @since V1.0
     */
    private boolean createRecordFile(String path, String fileName) {
        if (null == path || path.equals("") || null == fileName || fileName.equals("")) {
            return false;
        }

        try {
            mRecordFile = new File(path + File.separator + fileName);
            if ((null != mRecordFile) && (!mRecordFile.exists())) {
                mRecordFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();

            mRecordFile = null;
            return false;
        }

        return true;
    }

    /**
     * 写流头文件
     * 
     * @param file 写入的文件
     * @return true - 写入头文件成功. false - 写入头文件失败.
     * @since V1.0
     */
    private boolean writeStreamHead(File file) {
        if (null == file || null == mStreamHeadDataBuffer) {
            return false;
        }

        byte[] tempByte = mStreamHeadDataBuffer.array();
        if (null == tempByte) {
            mStreamHeadDataBuffer = null;
            return false;
        }

        try {
            if (null == mRecordFileOutputStream) {
                mRecordFileOutputStream = new FileOutputStream(file);
            }
            mRecordFileOutputStream.write(tempByte, 0, tempByte.length);
        } catch (Exception e) {
            e.printStackTrace();
            mRecordFileOutputStream = null;
            mStreamHeadDataBuffer = null;
            return false;
        }

        return true;
    }

    /**
     * 删除录像文件
     * 
     * @since V1.0
     */
    private void removeRecordFile() {
        try {
            if (null == mRecordFile) {
                return;
            }
            mRecordFile.delete();
            mRecordFile = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录像 void
     * 
     * @since V1.0
     */
    public void stopRecord() {
        if (!mIsRecord) {
            return;
        }

        mIsRecord = false;

        stopWriteStreamData();
    }

    /**
     * 停止写入数据流
     * 
     * @since V1.0
     */
    private void stopWriteStreamData() {
        if (null == mRecordFileOutputStream) {
            return;
        }

        try {
            mRecordFileOutputStream.flush();
            mRecordFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mRecordFileOutputStream = null;
            mRecordFile = null;
        }
    }
    
    /**
     * 开启音频
     * @author lvlingdi 2016-4-26 下午3:21:24
     * @return
     */
    public boolean startAudio() {
        if (PLAYBACK_PLAY != mPlayBackState) {
            Log.e(TAG, "非播放状态不能开启音频");
            return false;
        }

        if (null == mPlayerHandler) {
            Log.e(TAG, "startAudio()：：mPlayerHandler is null ");
            return false;
        }

        return mPlayerHandler.playSound(mPlayerPort);
    }
    
    /**
     * 关闭音频
     * @author lvlingdi 2016-4-26 下午3:21:32
     * @return
     */
    public boolean stopAudio() {
        if (PLAYBACK_PLAY != mPlayBackState) {
            Log.e(TAG, "非播放状态不能关闭音频");
            return false;
        }

        if (null == mPlayerHandler) {
            Log.e(TAG, "startAudio()：：mPlayerHandler is null ");
            return false;
        }
        return mPlayerHandler.stopSound();
    }
    
    /**
     * 获取player
     * @author lvlingdi 2016-4-27 下午3:51:38
     * @return
     */
    public Player getPlayer() {
        return mPlayerHandler;
    }
    
    /**
     * 获取播放状态
     * @author lvlingdi 2016-4-27 下午3:52:28
     * @return
     */
    public int getPlayBackState() {
        return mPlayBackState;
    }
    
    private MPSystemTime mPlayBackSystemTime = new MPSystemTime();
    private Calendar mOSDTime = new GregorianCalendar();
    
    public long getOSDTime() {
        if ((-1 == mPlayerPort) || ((PLAYBACK_PLAY != mPlayBackState) && (PLAYBACK_PAUSE != mPlayBackState))) {

            return -1;
        }

        mPlayBackSystemTime.year = 0;
        mPlayBackSystemTime.month = 0;
        mPlayBackSystemTime.day = 0;
        mPlayBackSystemTime.hour = 0;
        mPlayBackSystemTime.min = 0;
        mPlayBackSystemTime.sec = 0;

        if (!Player.getInstance().getSystemTime(mPlayerPort, mPlayBackSystemTime)) {
            
            return -1;
        }

        int year = mPlayBackSystemTime.year;
        int month = mPlayBackSystemTime.month;
        int day = mPlayBackSystemTime.day;
        int hour = mPlayBackSystemTime.hour;
        int minute = mPlayBackSystemTime.min;
        int second = mPlayBackSystemTime.sec;

        if (year == 0) {
            return -1;
        }

        mOSDTime.set(year, month - 1, day, hour, minute, second);

        return mOSDTime.getTimeInMillis();
    }
    
    /**
     * 电子放大
     * @author lvlingdi 2016-5-6 上午10:46:44
     * @param enable
     * @param original
     * @param current
     * @return
     */
    public boolean setDisplayRegion(boolean enable, PCRect original, PCRect current) {
        if ((-1 == mPlayerPort) || PLAYBACK_PLAY != mPlayBackState) {
            
            return false;
        }
        if (null == mSurfaceView) {
            return false;
        }
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (!enable) {
            return Player.getInstance().setDisplayRegion(mPlayerPort, 0, null, surfaceHolder, 1);
        }

        if (original == null || current == null) {
            return false;
        }

        MPInteger mMPw = new MPInteger();
        MPInteger mMPh = new MPInteger();

        if (!Player.getInstance().getPictureSize(mPlayerPort, mMPw, mMPh)) {
            return false;
        }

        float ratio = (float) (original.getWidth() * 1.0 / current.getWidth());

        float w = ratio * mMPw.value;
        float h = ratio * mMPh.value;

        float left = (float) (mMPw.value * Math.abs(current.getLeft() - original.getLeft()) * 1.0 / current.getWidth());
        float top = (float) (mMPh.value * Math.abs(current.getTop() - original.getTop()) * 1.0 / current.getHeight());
        float right = left + w;
        float bottom = top + h;

        MPRect oRect = new MPRect();
        oRect.left = 0;
        oRect.top = 0;
        oRect.right = mMPw.value;
        oRect.bottom = mMPh.value;

        MPRect cRect = new MPRect();
        cRect.left = (int) left;
        cRect.top = (int) top;
        cRect.right = (int) right;
        cRect.bottom = (int) bottom;

        judgeRect(oRect, cRect);

        if (!Player.getInstance().setDisplayRegion(mPlayerPort, 0, cRect, surfaceHolder, 1)) {
            return false;
        } else {
            return true;
        }
    }
    
    private void judgeRect(MPRect orgRect, MPRect curRect) {
        int oldW = orgRect.right - orgRect.left;
        int oldH = orgRect.bottom - orgRect.top;
        int newW = curRect.right - curRect.left;
        int newH = curRect.bottom - curRect.top;

        if (newW > oldW || newH > oldH) {
            curRect.left = orgRect.left;
            curRect.right = orgRect.right;
            curRect.top = orgRect.top;
            curRect.bottom = orgRect.bottom;
            return;
        }

        if (curRect.left < orgRect.left) {
            curRect.left = orgRect.left;
        }
        curRect.right = curRect.left + newW;

        if (curRect.top < orgRect.top) {
            curRect.top = orgRect.top;
        }
        curRect.bottom = curRect.top + newH;

        if (curRect.right > orgRect.right) {
            curRect.right = orgRect.right;
            curRect.left = curRect.right - newW;
        }

        if (curRect.bottom > orgRect.bottom) {
            curRect.bottom = orgRect.bottom;
            curRect.left = curRect.bottom - newH;
        }
    }
}
