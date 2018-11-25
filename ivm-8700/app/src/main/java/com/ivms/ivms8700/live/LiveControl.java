package com.ivms.ivms8700.live;

import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.rtsp.RtspClientCallback;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.utils.Utils;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.Player.MPInteger;
import org.MediaPlayer.PlayM4.Player.MPRect;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * control liveing logic Created by hanshuangwu on 2016/2/1.
 */
public class LiveControl implements RtspClientCallback, PlayerCallBack.PlayerDisplayCB {
	
	private static final String TAG = "LiveControl";
	/**
	 * 初始化阶段
	 */
	public static final int LIVE_INIT = 0;
	/**
	 * 取流阶段
	 */
	public static final int LIVE_STREAM = 1;
	/**
	 * 播放阶段
	 */
	public static final int LIVE_PLAY = 2;
	/**
	 * 释放资源阶段
	 */
	public static final int LIVE_RELEASE = 3;
	/**
	 * 预览状态
	 */
	private int mLiveState = LIVE_INIT;
	
    /**
     * 播放地址
     */
    private String liveUrl = null;
    
    private RtspClient mRtspHandler = null;
    private Player mPlayerHandler = null;
    /**
     * 设备账号
     */
    private String username = "";
    /**
     * 账号密码
     */
    private String password = "";

    /**
     * surfaceView on which show videos
     */
    private SurfaceView mSurfaceView = null;
    
    /**
     * create engine id of RTSP
     */
    private int mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
    private LiveCallBack mLiveCallBack = null;
    /**
     * 播放流量
     */
    private long mStreamRate = 0;
    /**
     * 播放库播放端口
     */
    private int mPlayerPort = -1;
    /**
     * 数据流
     */
    private ByteBuffer mStreamHeadDataBuffer;
    /**
     * 是否正在录像
     */
    private boolean mIsRecord = false;
    /**
     * 录像文件
     */
    private File mRecordFile = null;
    /**
     * 文件输出流
     */
    private FileOutputStream mRecordFileOutputStream = null;
    private int connectNum = 0;
    /**
     * 设置SD卡使用限度，当小于256M时，提示SD卡内存不足，根据具体情况可以修改
     */
    private int mSDCardSize = 256 * 1024 * 1024;
    private int pictureSize;
    /**
     * 抓拍图片文件
     */
    private File mPictureFile = null;
	
	/**
	 * 构造函数
	 */
	public LiveControl() {
		mLiveState = LIVE_INIT;
		mPlayerHandler = Player.getInstance();
        mRtspHandler = RtspClient.getInstance();
	}
	
	/**
	 * 设置预览参数
	 *
	 * @param url 播放地址
	 * @param name 登陆设备的用户名
	 * @param password 登陆设备的密码
	 */
	public void setLiveParams(String url, String name, String password) {
		liveUrl = url;
		username = name;
		this.password = password;
	}
	
	/**
	 * 获取当前播放状态
	 * @return LIVE_INIT初始化、LIVE_STREAM取流、LIVE_PLAY播放、LIVE_RELEASE释放资源
	 */
	public int getLiveState() {
		return mLiveState;
	}
	
    /**
     * start live
     * 
     * @param mSurfaceView
     */
    public void startLive(SurfaceView sf) {
        if (null == sf) {
            Log.e(TAG, "startLive():: surfaceView is null");
        }
        mSurfaceView = sf;

        if (LIVE_STREAM == mLiveState) {
            Log.e(TAG, "startLive():: is palying");
        }
        startRtsp();
    }

    private void startRtsp() {
        if (null == mRtspHandler) {
            Log.e(TAG, "startRtsp():: mRtspHandler is null");
            return;
        }
        mRtspEngineIndex = mRtspHandler.createRtspClientEngine(this, RtspClient.RTPRTSP_TRANSMODE);
        if (mRtspEngineIndex < 0) {
            Log.e(TAG, "startRtsp():: errorCode is R:" + mRtspHandler.getLastError());
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLiveSDK.RTSP_FAIL);
            }
            return;
        }

        boolean ret = mRtspHandler.startRtspProc(mRtspEngineIndex, liveUrl, username, password);
        if (!ret) {
            Log.e(TAG, "startRtsp():: errorCode is R" + mRtspHandler.getLastError());
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLiveSDK.RTSP_FAIL);
            }
            return;
        }
        mLiveState = LIVE_STREAM;
        if (null != mLiveCallBack) {
            mLiveCallBack.onMessageCallback(ConstantLiveSDK.RTSP_SUCCESS);
        }
    }

	/**
	 * set callback at play control level
	 *
	 * @param liveCallBack
	 */
	public void setLiveCallBack(LiveCallBack liveCallBack) {
		this.mLiveCallBack = liveCallBack;
	}
	
	/**
	 * @param handle 引擎ID
	 * @param dataType 数据类型，决定data的数据类型，包括DATATYPE_HEADER和DATATYPE_STREAM两种类型
	 * @param data 回调数据，分为：header数据和stream数据，由datatype作区分，header用于初始化播放库
	 * @param length data的数据长度
	 * @param timeStamp 时间戳（保留）
	 * @param packetNo rtp包号（保留）
	 * @param useId 用户数据，默认就是引擎ID，与handle相同
	 */
	@Override
	public void onDataCallBack(int handle, int dataType, byte[] data, int length, int timeStamp, int packetNo, int useId) {
		if (mStreamRate + length >= Long.MAX_VALUE) {
			mStreamRate = 0;
		}
		mStreamRate += length;
		
		switch (dataType) {
			case RtspClient.DATATYPE_HEADER:
				boolean ret = processStreamHeader(data, length);
				if (!ret) {
					if (null != mLiveCallBack) {
						mLiveCallBack.onMessageCallback(ConstantLiveSDK.START_OPEN_FAILED);
						return;
					} else {
						Log.e(TAG, "onDataCallBack():: liveCallBack is null");
					}
				} else {
					Log.e(TAG, "MediaPlayer Header success!");
				}
				break;
			default:
				processStreamData(data, length);
				break;
		}
		processRecordData(dataType, data, length);
		
	}
	
	/**
	 * 录像数据处理
	 *
	 * @param dataType 数据流
	 * @param dataBuffer 数据缓存
	 * @param dataLength 数据长度
	 */
	private void processRecordData(int dataType, byte[] dataBuffer, int dataLength) {
		if (null == dataBuffer || dataLength == 0) {
			return;
		}
		
		if (RtspClient.DATATYPE_HEADER == dataType) {
			mStreamHeadDataBuffer = ByteBuffer.allocate(dataLength);
			for (int i = 0; i < dataLength; i++) {
				mStreamHeadDataBuffer.put(dataBuffer[i]);
			}
		} else
			if (RtspClient.DATATYPE_STREAM == dataType) {
				if (mIsRecord) {
					writeStreamData(dataBuffer, dataLength);
				}
			}
		dataBuffer = null;
	}
	
	/**
	 * 录像数据写到文件
	 * @param recordData 录像数据
	 * @param dataLength 录像数据长度
	 */
	private boolean writeStreamData(byte[] recordData, int dataLength) {
		if (null == recordData || dataLength <= 0) {
			return false;
		}
		
		if (null == mRecordFile) {
			return false;
		}
		
		try {
			if (null == mRecordFileOutputStream) {
				mRecordFileOutputStream = new FileOutputStream(mRecordFile);
			}
			mRecordFileOutputStream.write(recordData, 0, dataLength);
			Log.i(TAG, "writeStreamData ():: success");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 向播放库塞数据
	 *
	 * @param data
	 * @param length
	 */
	private void processStreamData(byte[] data, int length) {
		if (null == data || 0 == length) {
			Log.e(TAG, "processStreamData():: Stream data is null or length is zero");
			return;
		}
		if (null != mPlayerHandler) {
			boolean ret = mPlayerHandler.inputData(mPlayerPort, data, length);
			if (!ret) {
				SystemClock.sleep(10);
			}
		}
	}
	
	/**
	 * 处理数据流头
	 *
	 * @param data
	 * @param length
	 * @return
	 */
	private boolean processStreamHeader(byte[] data, int length) {
		if (-1 != mPlayerPort) {
			closePlayer();
		}
		
		boolean ret = startPlayer(data, length);
		return ret;
	}
	
	/**
	 * 开启播放库
	 *
	 * @param data
	 * @param length
	 * @return
	 */
	private boolean startPlayer(byte[] data, int length) {
		if (null == data || 0 == length) {
			Log.e(TAG, "startPlayer(): Stream data error data is null or len is 0");
			return false;
		}
		
		if (null == mPlayerHandler) {
			Log.e(TAG, "startPlayer(): mPlayerHandler is null");
			return false;
		}
		
		mPlayerPort = mPlayerHandler.getPort();
		if (-1 == mPlayerPort) {
			Log.e(TAG, "startPlayer(): mPlayerPort is -1");
			return false;
		}
		
		boolean ret = mPlayerHandler.setStreamOpenMode(mPlayerPort, Player.STREAM_REALTIME);
		if (!ret) {
			int tempErrorCode = mPlayerHandler.getLastError(mPlayerPort);
			mPlayerHandler.freePort(mPlayerPort);
			mPlayerPort = -1;
			Log.e(TAG, "startPlayer():: Player setStreamOpenMode failed! errorCode is P" + tempErrorCode);
			return ret;
		}
		
		ret = mPlayerHandler.openStream(mPlayerPort, data, length, 2 * 1024 * 1024);
		if (!ret) {
			Log.e(TAG, "startPlayer():: mPlayerHandle.openStream failed!" + "Port: " + mPlayerPort + "ErrorCode is P "
			        + mPlayerHandler.getLastError(mPlayerPort));
			return false;
		}
		
		ret = mPlayerHandler.setDisplayCB(mPlayerPort, this);
		if (!ret) {
			Log.e(TAG,
			        "startPlayer():: mPlayerHandle.setDisplayCB() failed errorCode is P"
			                + mPlayerHandler.getLastError(mPlayerPort));
			return false;
		}
		
		if (null == mSurfaceView) {
			Log.e(TAG, "startPlayer():: surfaceView is null");
			return false;
		}
		
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		if (null == surfaceHolder) {
			Log.e(TAG, "startPlayer():: mPlayer mainSurface is null !");
			return false;
		}
		
		ret = mPlayerHandler.play(mPlayerPort, surfaceHolder);
		if (!ret) {
			Log.e(TAG, "startPlayer():: mPlayerHnadle.paly failed!" + "Port: " + mPlayerPort + "PlayView Surface: "
			        + surfaceHolder + "errorCode is P" + mPlayerHandler.getLastError(mPlayerPort));
			return false;
		}
		
		return true;
	}
	
	/**
	 * 关闭播放库
	 */
	private void closePlayer() {
		if (null != mPlayerHandler && -1 != mPlayerPort) {
			boolean ret = mPlayerHandler.stop(mPlayerPort);
			if (!ret) {
				Log.e(TAG, "closePlayer(): Player stop  failed!  errorCode is P" + mPlayerHandler.getLastError(mPlayerPort));
			}
			
			ret = mPlayerHandler.closeStream(mPlayerPort);
			if (!ret) {
				Log.e(TAG, "closePlayer(): Player closeStream failed!");
			}
			
			ret = mPlayerHandler.freePort(mPlayerPort);
            if (!ret) {
                Log.e(TAG, "closePlayer(): Player freePort  failed!");
            }
            
			mPlayerPort = -1;
		}
	}
	
	/**
	 *
	 * @param handle 引擎id
	 * @param opt 回调消息，包括：RTSPCLIENT_MSG_PLAYBACK_FINISH,RTSPCLIENT_MSG_BUFFER_OVERFLOW ,RTSPCLIENT_MSG_CONNECTION_EXCEPTION 三种
	 * @param param1 保留参数
	 * @param param2 保留参数
	 * @param useId 用户数据，默认就是引擎id,与handle相同
	 */
	@Override
	public void onMessageCallBack(int handle, int opt, int param1, int param2, int useId) {
		if (opt == RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION) {
			stop();
			Log.e(TAG, "onMessageCallBack():: rtsp connection exception");
			if (connectNum > 3) {
				Log.e(TAG, "onMessageCallBack():: rtsp connection more than three times");
				connectNum = 0;
			} else {
				startLive(mSurfaceView);
				connectNum++;
			}
		}
	}
	
	/**
	 * 停止预览
	 */
	public void stop() {
		if (LIVE_INIT == mLiveState) {
			return;
		}
		
		if (mIsRecord) {
			stopRecord();
			mIsRecord = false;
		}
		
		stopRtsp();
		closePlayer();
		if (null != mLiveCallBack) {
			mLiveCallBack.onMessageCallback(ConstantLiveSDK.STOP_SUCCESS);
		}
		
		mLiveState = LIVE_INIT;
	}
	
	/**
	 * 停止RTSP
	 */
	private void stopRtsp() {
		if (null != mRtspHandler) {
			if (RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID != mRtspEngineIndex) {
				mRtspHandler.stopRtspProc(mRtspEngineIndex);
				mRtspHandler.releaseRtspClientEngineer(mRtspEngineIndex);
				mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
			}
		}
	}
	
	@Override
	public void onDisplay(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, int i6) {
		if (LIVE_PLAY != mLiveState) {
			mLiveState = LIVE_PLAY;
			if (null != mLiveCallBack) {
				mLiveCallBack.onMessageCallback(ConstantLiveSDK.PLAY_DISPLAY_SUCCESS);
			} else {
				Log.e(TAG, "onDisplay():: liveCallBack is null");
			}
		}
	}
	
	/**
	 * 抓拍
	 *
	 * @param filePath 存放文件路径
	 * @param picName 抓拍时文件的名称
	 * @return true-抓拍成功，false-抓拍失败
	 * @since V1.0
	 */
	public boolean capture(String filePath, String picName) {
		if (!Utils.isSDCardUsable()) {
			if (null != mLiveCallBack) {
				mLiveCallBack.onMessageCallback(ConstantLiveSDK.SD_CARD_UN_USEABLE);
			}
			return false;
		}
		
		if (Utils.getSDCardRemainSize() <= mSDCardSize) {
			if (null != mLiveCallBack) {
				mLiveCallBack.onMessageCallback(ConstantLiveSDK.SD_CARD_SIZE_NOT_ENOUGH);
			}
			return false;
		}
		
		if (LIVE_PLAY != mLiveState) {
			mLiveCallBack.onMessageCallback(ConstantLiveSDK.CAPTURE_FAILED_NPLAY_STATE);
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
		pictureBuffer = null;
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mPictureFile = null;
		}
	}
	
	/**
	 * 抓拍图片写到SDCard
	 *
	 * @param picData 图片数据
	 * @param length 图片数据长度
	 * @since V1.0
	 */
	private boolean writePictureToFile(byte[] picData, int length) {
		if (null == picData || length <= 0 || picData.length > length) {
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
			String fileStr=dirPath + File.separator + fileName;
			mPictureFile = new File(fileStr);
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
				tempFile.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
			tempFile = null;
			return "";
		}
		return tempFile.getAbsolutePath();
	}
	
	private byte[] getPictureOnJPEG() {
		if (null == mPlayerHandler) {
			Log.e(TAG, "getPictureOnJPEG():: mPlayerHnadler is null");
			return null;
		}
		
		if (-1 == mPlayerPort) {
			Log.e(TAG, "getPictureOnJPEG():: mPlayerPort is Unavailable");
			return null;
		}
		
		int picSize = getPictureSize();
		if (picSize <= 0) {
			return null;
		}
		
		byte[] pictureBuffer = null;
		try {
			pictureBuffer = new byte[picSize];
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			pictureBuffer = null;
			return null;
		}
		
		MPInteger jpgSize = new MPInteger();
		
		boolean ret = mPlayerHandler.getJPEG(mPlayerPort, pictureBuffer, picSize, jpgSize);
		if (!ret) {
			Log.e(TAG, "getPictureOnJPEG():: mPlayerHandler.getJPEG() return false");
			return null;
		}
		
		int jpegSize = jpgSize.value;
		if (jpegSize <= 0) {
			pictureBuffer = null;
			return null;
		}
		
		ByteBuffer jpgBuffer = ByteBuffer.wrap(pictureBuffer, 0, jpegSize);
		if (null == jpgBuffer) {
			pictureBuffer = null;
			return null;
		}
		
		return jpgBuffer.array();
	}
	
	public int getPictureSize() {
		MPInteger width = new MPInteger();
		MPInteger height = new MPInteger();
		boolean ret = mPlayerHandler.getPictureSize(mPlayerPort, width, height);
		if (!ret) {
			Log.e(TAG,
			        "getPictureSize():: mPlayerHandler.getPictureSize() return false，errorCode is P"
			                + mPlayerHandler.getLastError(mPlayerPort));
			return 0;
		}
		int pictureSize = width.value * height.value * 3;
		return pictureSize;
	}
	
	/**
	 * 开启音频
	 * @author lvlingdi 2016-4-26 下午3:28:38
	 * @return
	 */
    public boolean startAudio() {
        if (LIVE_PLAY != mLiveState) {
            Log.e(TAG, "非播放状态不能开启音频");
            return false;
        }

        if (null == mPlayerHandler) {
            return false;
        }

        boolean ret = mPlayerHandler.playSound(mPlayerPort);
        return ret;
    }
    
    /**
     * 关闭音频
     * @author lvlingdi 2016-4-26 下午3:28:29
     * @return
     */
    public boolean stopAudio() {
        if (LIVE_PLAY != mLiveState) {
            Log.e(TAG, "非播放状态不能关闭音频");
            return false;
        }

        if (null == mPlayerHandler) {
            return false;
        }

        boolean ret = mPlayerHandler.stopSound();
        return ret;
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
     * 启动录像方法
     * 
     * @param filePath 录像文件路径
     * @param fileName 录像文件名称
     * @param isRpmPackage 是否启用转封装
     * @return true-启动录像成功，false-启动录像失败
     * @since V1.0
     */
    public boolean startRecord(String filePath, String fileName) {
		System.out.println("filePath=-="+filePath+"=-=fileName=-="+fileName);
        if (!Utils.isSDCardUsable()) {
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.SD_CARD_UN_USEABLE);
            }
            return false;
        }

        if (Utils.getSDCardRemainSize() <= mSDCardSize) {
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.SD_CARD_SIZE_NOT_ENOUGH);
            }
            return false;
        }

        if (LIVE_PLAY != mLiveState) {
            Log.e(TAG, "非播放状态不能录像");
            return false;
        }

        boolean ret = createRecordFile(filePath, fileName);
        if (!ret) {
            Log.e(TAG, "createRecordFile() fail 创建录像文件失败");
            return false;
        }

        ret = writeStreamHead(mRecordFile);
        if (!ret) {
            Log.e(TAG, "writeStreamHead() 写文件失败");
            removeRecordFile();
            return false;
        }

        mIsRecord = true;
        Log.e(TAG, "启动录像成功");
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
			System.out.println("文件创建失败=-=");
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordFile = null;
        }
    }
    
	public interface LiveCallBack {
		
		/**
		 * message callback of play engine
		 *
		 * @param message
		 */
		void onMessageCallback(int message);
	}
	
	/**
	 * 电子放大
	 * @author lvlingdi 2016-5-6 上午10:46:06
	 * @param enable 
	 * @param original
	 * @param current
	 * @return
	 */
    public boolean setDisplayRegion(boolean enable, PCRect original, PCRect current) {
        if ((-1 == mPlayerPort) || LIVE_PLAY != mLiveState) {
            
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
