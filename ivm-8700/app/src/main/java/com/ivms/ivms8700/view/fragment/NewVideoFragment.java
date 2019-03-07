package com.ivms.ivms8700.view.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.CNetSDKLog;
import com.hikvision.sdk.utils.FileUtils;
import com.hikvision.sdk.utils.SDKUtil;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.MenuTree;
import com.ivms.ivms8700.bean.VideoEntity;

import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.control.TempDatas;
import com.ivms.ivms8700.presenter.LoginPresenter;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.view.AddCamerActivity;
import com.ivms.ivms8700.view.adapter.AdapterVideoRecyView;
import com.ivms.ivms8700.view.customui.CustomSurfaceView;
import com.ivms.ivms8700.view.iview.ILoginView;
import com.kongqw.rockerlibrary.view.RockerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;


public class NewVideoFragment extends Fragment implements View.OnClickListener, ILoginView,SurfaceHolder.Callback {
    private final String TAG = "Alan";
    private View view;
    private LinearLayout live_lay;
    private LinearLayout huifang_lay;
    private View live_view;
    private View huifang_view;
    private int palyType = 1;//1代表预览，2代表远程回放
    private LinearLayout playBackRecord;
    private LinearLayout playBackCapture;
    private LinearLayout contrl_lay;
    private AlertDialog alertDialog; //信息框
    private int VIDEO_VIEW_COUNT = 1;//当前屏幕数
    private RecyclerView video_recyclerview;
    private AdapterVideoRecyView video_adapter;
    private ImageView one_view_img;
    private ImageView four_view_img;
    private ImageView nine_view_img;
    private ImageView add_video;
    List<VideoEntity> videList = null;
    private ImageView playBackRecord_img;
    private LinearLayout voice_intercom;
    private GridLayoutManager manager;
    private LinearLayout operation_lay;
    private RelativeLayout contrl_all_lay;
    private LinearLayout view_count_lay;

    private ImageView  close_btn;

    /****    预览相关 start  ***********************************************************/
    /**
     * 获取监控点信息成功
     */
    private static final int GET_CAMERA_INFO_SUCCESS = 1;
    /**
     * 获取监控点信息失败
     */
    private static final int GET_CAMERA_INFO_FAILURE = 2;
    /**
     * 开启语音对讲失败
     */
    private static final int OPEN_TALK_FAILURE = 3;
    /**
     * 开启语音对讲成功
     */
    private static final int OPEN_TALK_SUCCESS = 4;
    /**
     * 关闭语音对讲
     */
    private static final int CLOSE_TALK_SUCCESS = 5;
    /**
     * 预览控件
     */
    private CustomSurfaceView mSurfaceView = null;
    /**
     * 预览控制菜单
     */
    private LinearLayout mPreviewLayout;


    /**
     * 是否正在云台控制
     */
    private boolean mIsPtzStart;
    /**
     * 云台控制命令
     */
    private int mPtzCommand;
    /**
     * 码流类型
     */
    private int mStreamType = SDKConstant.LiveSDKConstant.MAIN_HIGH_STREAM;
    /**
     * 音频是否开启
     */
    private boolean mIsAudioOpen;
    /**
     * 语音对讲是否开启
     */
    private boolean mIsTalkOpen;
    /**
     * 是否正在录像
     */
    private boolean mIsRecord;
    /**
     * 监控点资源
     */
    private SubResourceNodeBean mCamera = null;
    /**
     * 视图更新处理Handler
     */
    private Handler mHandler = null;
    /**
     * 对讲通道数目
     */
    private int talkChannels;
    /**
     * 临时选择对讲通道数目
     */
    private String channelNoTemp;
    /**
     * 最终选择对讲通道数目
     */
    private int channelNo;

    private int PLAY_WINDOW_ONE = 1;//当前播放窗口
    private LoginPresenter presenter;
    private ImageView fd_btn;
    private ImageView sx_btn;


    /**
     * 视图更新处理器
     */
    private static class MyHandler extends Handler {

        WeakReference<NewVideoFragment> mFragmentReference;

        MyHandler(NewVideoFragment mFragment) {
            mFragmentReference = new WeakReference<>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            NewVideoFragment mFragment = mFragmentReference.get();
            if (mFragment != null) {
                switch (msg.what) {
                    case GET_CAMERA_INFO_SUCCESS:
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(mFragment.getActivity(), R.string.rtsp_success);
                        break;
                    case GET_CAMERA_INFO_FAILURE:
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(mFragment.getActivity(), R.string.rtsp_fail);
                        break;
                    case OPEN_TALK_FAILURE:
                        mFragment.mIsTalkOpen = false;
                        UIUtil.showToast(mFragment.getActivity(), R.string.start_Talk_fail);
//                        mFragment.mTalkBtn.setText(R.string.start_Talk);
                        break;
                    case OPEN_TALK_SUCCESS:
                        mFragment.mIsTalkOpen = true;
                        UIUtil.showToast(mFragment.getActivity(), R.string.start_Talk_success);
//                        mFragment.mTalkBtn.setText(R.string.stop_Talk);
                        break;
                    case CLOSE_TALK_SUCCESS:
                        mFragment.mIsTalkOpen = false;
                        UIUtil.showToast(mFragment.getActivity(), R.string.stop_Talk);
//                        activity.mTalkBtn.setText(R.string.start_Talk);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /****  预览相关 end **************************************************8*****/

    /***** 回放相关 start ********************************************************/

    /**
     * 获取监控点信息成功
     */
    private static final int CAMERA_INFO_SUCCESS = 1;
    /**
     * 获取监控点信息失败
     */
    private static final int CAMERA_INFO_FAILURE = 2;
    /**
     * 查找录像片段成功
     */
    private static final int QUERY_SUCCESS = 3;
    /**
     * 查找录像片段失败
     */
    private static final int QUERY_FAILURE = 4;
    /**
     * 开启回放成功
     */
    public static final int START_SUCCESS = 5;
    /**
     * 开启回放失败
     */
    public static final int START_FAILURE = 6;
    /**
     * 进度条最大值
     */
    private static final int PROGRESS_MAX_VALUE = 100;

    /**
     * 存储介质选择控件
     */
    private RadioGroup mStorageTypesRG;

    /**
     * 播放进度条控件
     */
    private SeekBar mProgressSeekBar = null;

    /**
     * 监控点详情
     */
    private CameraInfo mCameraInfo;
    /**
     * 存储介质
     */
    private int mStorageType;
    /**
     * 录像唯一标识Guid
     */
    private String mGuid;
    /**
     * 录像详情
     */
    private RecordInfo mRecordInfo;
    /**
     * 初始开始时间
     */
    private Calendar mFirstStartTime;
    /**
     * 开始时间
     */
    private Calendar mStartTime;
    /**
     * 结束时间
     */
    private Calendar mEndTime;
    /**
     * 录像片段
     */
    private RecordSegment mRecordSegment;
    /**
     * 定时器
     */
    private Timer mUpdateTimer = null;
    /**
     * 定时器执行的任务
     */
    private TimerTask mUpdateTimerTask = null;
    /**
     * 创建消息对象
     */
    private Handler mMessageHandler;


    /***
     * UI处理Handler
     */
    @SuppressLint("HandlerLeak")
    private class MyPlayBackHandler extends Handler {
        WeakReference<NewVideoFragment> mActivityReference;

        MyPlayBackHandler(NewVideoFragment newVideoFragment) {
            mActivityReference = new WeakReference<>(newVideoFragment);
        }
        public void handleMessage(Message msg) {
            NewVideoFragment newVideoFragment = mActivityReference.get();
            Activity activity=newVideoFragment.getActivity();
            if (activity != null) {
                switch (msg.what) {
                    case CAMERA_INFO_SUCCESS:
                        UIUtil.cancelProgressDialog();
                        //解析监控点录像信息
                        int[] mRecordPos = SDKUtil.processStorageType(mCameraInfo);
                        String[] mGuids = SDKUtil.processGuid(mCameraInfo);
                        //默认选取第一种存储类型进行查询
                        if (null != mRecordPos && 0 < mRecordPos.length) {
                            mStorageType = mRecordPos[0];
                        }
                        if (null != mGuids && 0 < mGuids.length) {
                            mGuid = mGuids[0];
                        }
                        if (null != mRecordPos && 0 < mRecordPos.length) {
                            queryRecordSegment();
                        } else {
                            UIUtil.showToast(activity, "录像文件查询失败");
                        }
                        break;
                    case CAMERA_INFO_FAILURE:
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(activity, R.string.loading_camera_info_failure);
                        break;
                    case QUERY_SUCCESS:
                        //录像片段查询成功
                        gotoPlayBack();
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(activity, "录像文件查询成功");
                        break;
                    case QUERY_FAILURE:
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(activity, "录像文件查询失败");
                        break;
                    case START_SUCCESS:
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(activity, R.string.rtsp_success);
                        startUpdateTimer();
                        break;
                    case START_FAILURE:
                        UIUtil.cancelProgressDialog();
                        UIUtil.showToast(activity, R.string.rtsp_fail);
                        break;
                    case SDKConstant.PlayBackSDKConstant.MSG_REMOTELIST_UI_UPDATE:
                        //更新播放进度条
                        updateRemotePlayUI();
                        break;

                }
            }
        }
    }
    /**
     * 初始化回放数据
     */
    private void initPlayBackData() {
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if(mMessageHandler==null) {
            mMessageHandler = new MyPlayBackHandler(this);
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mFirstStartTime = Calendar.getInstance();
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
        mFirstStartTime.set(year, month, day, 0, 0, 0);
        mStartTime.set(year, month, day, 0, 0, 0);
        mEndTime.set(year, month, day, 23, 59, 59);
        getCameraInfo();
    }
    /**
     * 获取监控点详细信息
     */
    private void getCameraInfo() {
        if (null == mCamera) {
            Log.d("Alan","getCameraInfo()...  mCamera=null");
            return;
        }
        if (null == mCamera.getSysCode()) {
            Log.d("Alan","getCameraInfo()...  mCamera.getSysCode()=null");
            return;
        }
        VMSNetSDK.getInstance().getPlayBackCameraInfo(PLAY_WINDOW_ONE, mCamera.getSysCode(), new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(CAMERA_INFO_FAILURE);
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof CameraInfo) {
                    mCameraInfo = (CameraInfo) obj;
                    mMessageHandler.sendEmptyMessage(CAMERA_INFO_SUCCESS);
                }
            }
        });
    }
    /**
     * 查找录像片段
     */
    private void queryRecordSegment() {
        if (null == mCameraInfo) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar queryStartTime = Calendar.getInstance();
        Calendar queryEndTime = Calendar.getInstance();
        queryStartTime.set(year, month, day, 0, 0, 0);
        queryEndTime.set(year, month, day, 23, 59, 59);
        VMSNetSDK.getInstance().queryRecordSegment(PLAY_WINDOW_ONE, mCameraInfo, queryStartTime, queryEndTime, mStorageType, mGuid, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(QUERY_FAILURE);
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof RecordInfo) {
                    mRecordInfo = ((RecordInfo) obj);
                    if (null != mRecordInfo.getSegmentList() && 0 < mRecordInfo.getSegmentList().size()) {
                        mRecordSegment = mRecordInfo.getSegmentList().get(0);
                        //级联设备的时候使用录像片段中的时间
                        if (SDKConstant.CascadeFlag.CASCADE == mCameraInfo.getCascadeFlag()) {
                            mEndTime = SDKUtil.convertTimeString(mRecordSegment.getEndTime());
                            mStartTime = SDKUtil.convertTimeString(mRecordSegment.getBeginTime());
                            mFirstStartTime = mStartTime;
                        }
                        mMessageHandler.sendEmptyMessage(QUERY_SUCCESS);
                    } else {
                        mMessageHandler.sendEmptyMessage(QUERY_FAILURE);
                    }
                }
            }
        });

    }
    /***
     * 更新播放库UI
     */
    private void updateRemotePlayUI() {
        //获取播放进度
        long osd = VMSNetSDK.getInstance().getOSDTimeOpt(PLAY_WINDOW_ONE);
        if (osd != -1) {
            handlePlayProgress(osd);
        }
    }

    /***
     * 更新播放进度
     *
     * @param osd 播放进度
     */
    private void handlePlayProgress(long osd) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(osd);
        long begin = mFirstStartTime.getTimeInMillis();
        long end = mEndTime.getTimeInMillis();

        double x = ((osd - begin) * PROGRESS_MAX_VALUE) / (double) (end - begin);
        int progress = (int) (x);
        mProgressSeekBar.setProgress(progress);
    }
    /**
     * 启动定时器
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {
                mMessageHandler.sendEmptyMessage(SDKConstant.PlayBackSDKConstant.MSG_REMOTELIST_UI_UPDATE);
            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }

    /**
     * 停止定时器
     */
    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }
    /***** 回放相关 end ********************************************************/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            presenter = new LoginPresenter(this);
            view = inflater.inflate(R.layout.video_layout, container, false);
            live_lay = (LinearLayout) view.findViewById(R.id.live_lay);
            huifang_lay = (LinearLayout) view.findViewById(R.id.huifang_lay);
            live_view = (View) view.findViewById(R.id.live_view);
            huifang_view = (View) view.findViewById(R.id.huifang_view);
            voice_intercom = (LinearLayout) view.findViewById(R.id.voice_intercom); //对讲
            contrl_lay = (LinearLayout) view.findViewById(R.id.contrl_lay); //云台控制
            playBackRecord = (LinearLayout) view.findViewById(R.id.playBackRecord); //本地录像
            playBackRecord_img = (ImageView) view.findViewById(R.id.playBackRecord_img);
            playBackCapture = (LinearLayout) view.findViewById(R.id.playBackCapture); //本地截图
            operation_lay= (LinearLayout) view.findViewById(R.id.operation_lay); //功能界面
            contrl_all_lay= (RelativeLayout) view.findViewById(R.id.contrl_all_lay); //云台控制界面
            view_count_lay= (LinearLayout) view.findViewById(R.id.view_count_lay);
            close_btn= (ImageView) view.findViewById(R.id.close_btn);
            fd_btn= (ImageView) view.findViewById(R.id.fd_btn);//放大
            sx_btn= (ImageView) view.findViewById(R.id.sx_btn);//缩小

            one_view_img = (ImageView) view.findViewById(R.id.one_view_img);
            four_view_img = (ImageView) view.findViewById(R.id.four_view_img);
            nine_view_img = (ImageView) view.findViewById(R.id.nine_view_img);

            fd_btn.setOnClickListener(this);
            sx_btn.setOnClickListener(this);
            close_btn.setOnClickListener(this);
            voice_intercom.setOnClickListener(this);
            one_view_img.setOnClickListener(this);
            four_view_img.setOnClickListener(this);
            nine_view_img.setOnClickListener(this);
            live_lay.setOnClickListener(this);
            huifang_lay.setOnClickListener(this);
            playBackRecord.setOnClickListener(this);
            playBackCapture.setOnClickListener(this);
            contrl_lay.setOnClickListener(this);
            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    dm.widthPixels,
                    dm.widthPixels
            );

            video_recyclerview = (RecyclerView) view.findViewById(R.id.video_recyclerview);
            video_recyclerview.setLayoutParams(linearParams);
            setGrilView(VIDEO_VIEW_COUNT, 1);
            //云台控制圆盘
            // 21：云台转上 (使摄像头向上转动）
            // 22：云台转下 (使摄像头向下转动）
            // 23：云台转左 (使摄像头向左转动）
            // 24：云台转右 (使摄像头向右转动）
            // 25：云台转左上 (使摄像头向左上转动）
            // 27：云台转左下 (使摄像头向左下转动）
            // 26：云台转右上 (使摄像头向右上转动）
            // 28：云台转右下 (使摄像头向右下转动）
            // 29：自动巡航 (使摄像头自动左右转动）
            // 11：焦距变大 (倍率变大）
            // 12：焦距变小 (倍率变小）
            RockerView rockerViewLeft = (RockerView) view.findViewById(R.id.rockerView);
            if (rockerViewLeft != null) {
                rockerViewLeft.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
                rockerViewLeft.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void direction(RockerView.Direction direction) {
                        switch (direction) {
                            case DIRECTION_LEFT:
//                                message = "左";
                                mPtzCommand = SDKConstant.PTZCommandConstant.CUSTOM_CMD_LEFT;
                                break;
                            case DIRECTION_RIGHT:
//                                message = "右";
                                mPtzCommand = SDKConstant.PTZCommandConstant.CUSTOM_CMD_RIGHT;
                                break;
                            case DIRECTION_UP:
//                                message = "上";
                                mPtzCommand = SDKConstant.PTZCommandConstant.CUSTOM_CMD_UP;
                                break;
                            case DIRECTION_DOWN:
//                                message = "下";
                                mPtzCommand = SDKConstant.PTZCommandConstant.CUSTOM_CMD_DOWN;
                                break;
                            case DIRECTION_UP_LEFT:
//                                message = "左上";
                                mPtzCommand = 25;

                                break;
                            case DIRECTION_UP_RIGHT:
//                                message = "右上";
                                mPtzCommand = 26;
                                break;
                            case DIRECTION_DOWN_LEFT:
//                                message = "左下";
                                mPtzCommand = 27;
                                break;
                            case DIRECTION_DOWN_RIGHT:
//                                message = "右下";
                                mPtzCommand = 28;
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFinish() {
                        contrlDirectional(mPtzCommand);
                    }
                });
            }
            mProgressSeekBar = (SeekBar) view.findViewById(R.id.progress_seekbar);
            mProgressSeekBar.setVisibility(View.GONE);
            mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                /**
                 * 拖动条停止拖动的时候调用
                 */
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                    if(mFirstStartTime==null||mEndTime==null){
                        UIUtil.showToast(getActivity(),"未开始播放");
                        return;
                    }
                    VMSNetSDK.getInstance().stopPlayBackOpt(PLAY_WINDOW_ONE);
                    stopUpdateTimer();
                    int progress = arg0.getProgress();
                    long begin = mFirstStartTime.getTimeInMillis();
                    long end = mEndTime.getTimeInMillis();
                    long avg = (end - begin) / PROGRESS_MAX_VALUE;
                    long trackTime = begin + (progress * avg);
                    Calendar track = Calendar.getInstance();
                    track.setTimeInMillis(trackTime);
                    mStartTime = track;
                    VMSNetSDK.getInstance().startPlayBackOpt(PLAY_WINDOW_ONE, mSurfaceView, mRecordInfo.getSegmentListPlayUrl(), mStartTime, mEndTime, new OnVMSNetSDKBusiness() {
                        @Override
                        public void onFailure() {
                            mMessageHandler.sendEmptyMessage(START_FAILURE);
                        }

                        @Override
                        public void onSuccess(Object obj) {
                            mMessageHandler.sendEmptyMessage(START_SUCCESS);
                        }

                        @Override
                        public void onStatusCallback(int status) {
                            //录像片段回放结束
                            if (status == RtspClient.RTSPCLIENT_MSG_PLAYBACK_FINISH) {
                                mMessageHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        UIUtil.showToast(getActivity(),"回放结束");
                                    }
                                });
                            }
                        }
                    });
                }

                /**
                 * 拖动条开始拖动的时候调用
                 */
                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                /**
                 * 拖动条进度改变的时候调用
                 */
                @Override
                public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

                }
            });
        }
        return view;
    }
    //云台控制方向
    private void contrlDirectional(final int mPtzCommand) {
        Log.d("Alan","mPtzCommand=-="+mPtzCommand);
        //开始云台操作
        VMSNetSDK.getInstance().sendPTZCtrlCommand(PLAY_WINDOW_ONE, true, SDKConstant.PTZCommandConstant.ACTION_START, mPtzCommand, 256, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
            }

            @Override
            public void onSuccess(Object obj) {
                //停止
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //执行的方法
                        VMSNetSDK.getInstance().sendPTZCtrlCommand(PLAY_WINDOW_ONE, true, SDKConstant.PTZCommandConstant.ACTION_STOP, mPtzCommand, 256, new OnVMSNetSDKBusiness() {
                            @Override
                            public void onFailure() {
                            }

                            @Override
                            public void onSuccess(Object obj) {
                            }
                        });
                    }
                }, 1000);//3秒后执行Runnable中的run方法
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_lay:
                if(mIsRecord){
                    UIUtil.showToast(getActivity(),"处于录像状态");
                    return;
                }
                myInit();
                mProgressSeekBar.setVisibility(View.GONE);
                palyType = 1;
                live_view.setVisibility(View.VISIBLE);
                huifang_view.setVisibility(View.INVISIBLE);
                break;
            case R.id.huifang_lay:
                if(mIsRecord){
                    UIUtil.showToast(getActivity(),"处于录像状态");
                    return;
                }
                operation_lay.setVisibility(View.VISIBLE);
                contrl_all_lay.setVisibility(View.GONE);
                myInit();
                mProgressSeekBar.setVisibility(View.VISIBLE);
                palyType = 2;
                live_view.setVisibility(View.INVISIBLE);
                huifang_view.setVisibility(View.VISIBLE);
                break;
            case R.id.playBackRecord://本地录像
                if(palyType==1){
                    playBackRecordLive();
                }else{
                    playBackRecordBack();
                }
                break;
            case R.id.playBackCapture://本地截图
                //抓拍按钮点击操作
                Log.d("Alan",FileUtils.getPictureDirPath().getAbsolutePath()+"=-="+"Picture" + System.currentTimeMillis() + ".jpg");
                int opt = VMSNetSDK.getInstance().captureLiveOpt(PLAY_WINDOW_ONE, FileUtils.getPictureDirPath().getAbsolutePath(), "Picture" + System.currentTimeMillis() + ".jpg");
                switch (opt) {
                    case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                        UIUtil.showToast(getActivity(), R.string.sd_card_fail);
                        break;
                    case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                        UIUtil.showToast(getActivity(), R.string.sd_card_not_enough);
                        break;
                    case SDKConstant.LiveSDKConstant.CAPTURE_FAILED:
                        UIUtil.showToast(getActivity(), R.string.capture_fail);
                        break;
                    case SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS:
                        UIUtil.showToast(getActivity(), R.string.capture_success);
                        break;
                }
                break;
            case R.id.contrl_lay://云台控制
                if (palyType == 1) {
                    operation_lay.setVisibility(View.GONE);
                    contrl_all_lay.setVisibility(View.VISIBLE);
                    view_count_lay.setVisibility(View.GONE);
                } else {
                    UIUtil.showToast(getActivity(), "远程回放不支持云台控制");
                }
                break;
            case R.id.close_btn:
                operation_lay.setVisibility(View.VISIBLE);
                view_count_lay.setVisibility(View.VISIBLE);
                contrl_all_lay.setVisibility(View.GONE);
                break;
            case R.id.fd_btn:
                contrlDirectional(11);
                break;
            case R.id.sx_btn:
                contrlDirectional(12);
                break;

            case R.id.one_view_img://一屏
                if (VIDEO_VIEW_COUNT != 1) {
                    if(mIsRecord){
                        UIUtil.showToast(getActivity(),"处于录像状态");
                        return;
                    }
                    VIDEO_VIEW_COUNT = 1;
                    for (int i = 0; i < videList.size(); i++) {
                        videList.get(i).setRowCout(1);
                    }
                    manager.setSpanCount(1);
                    video_adapter.notifyDataSetChanged();
                    one_view_img.setBackgroundResource(R.drawable.one_2);
                    four_view_img.setBackgroundResource(R.drawable.four_2);
                    nine_view_img.setBackgroundResource(R.drawable.nine_1);
                    showFirstVideo();
                }

                break;
            case R.id.four_view_img://四屏
                if (VIDEO_VIEW_COUNT != 4) {
                    if(mIsRecord){
                        UIUtil.showToast(getActivity(),"处于录像状态");
                        return;
                    }
                    VIDEO_VIEW_COUNT = 4;
                    for (int i = 0; i < videList.size(); i++) {
                        videList.get(i).setRowCout(2);
                    }
                    manager.setSpanCount(2);
                    video_adapter.notifyDataSetChanged();
                    one_view_img.setBackgroundResource(R.drawable.one_1);
                    four_view_img.setBackgroundResource(R.drawable.four_1);
                    nine_view_img.setBackgroundResource(R.drawable.nine_1);
                    showFirstVideo();
                }
                break;
            case R.id.nine_view_img://九屏
                if (VIDEO_VIEW_COUNT != 9) {
                    if(mIsRecord){
                        UIUtil.showToast(getActivity(),"处于录像状态");
                        return;
                    }
                    VIDEO_VIEW_COUNT = 9;
                    for (int i = 0; i < videList.size(); i++) {
                        videList.get(i).setRowCout(3);
                    }
                    manager.setSpanCount(3);
                    video_adapter.notifyDataSetChanged();
                    one_view_img.setBackgroundResource(R.drawable.one_1);
                    four_view_img.setBackgroundResource(R.drawable.four_2);
                    nine_view_img.setBackgroundResource(R.drawable.nine_2);
                    showFirstVideo();
                }
                break;

            case R.id.voice_intercom:
//                if (mIsTalkOpen) {
//                    VMSNetSDK.getInstance().closeLiveTalkOpt(PLAY_WINDOW_ONE);
//                    mHandler.sendEmptyMessage(CLOSE_TALK_SUCCESS);
//                } else {
//                    try {
//                        talkChannels = VMSNetSDK.getInstance().getTalkChannelsOpt(PLAY_WINDOW_ONE);
//                        if (talkChannels <= 0) {
//                            UIUtil.showToast(getActivity(), R.string.no_Talk_channels);
//                        } else if (talkChannels > 1) {
//                            showChannelSelectDialog();
//                        } else {
//                            channelNo = 1;
//                            startTalk();
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        Log.d("Alan","未开启播放");
//                    }
//
//                }
                UIUtil.showToast(getActivity(),"正在建设中..");
                break;
        }
    }
    /**
     * 选择通道号开始语音对讲
     *
     * @author lvlingdi 2016-5-18 上午10:29:36
     */
    private void showChannelSelectDialog() {
        // 创建对话框
        final AlertDialog mChannelSelectDialog = new AlertDialog.Builder(getActivity()).create();
        // 显示对话框
        mChannelSelectDialog.show();
        mChannelSelectDialog.setCanceledOnTouchOutside(false);
        final Window window = mChannelSelectDialog.getWindow();
        window.setContentView(R.layout.dialog_channle_select);
        RadioGroup channels = (RadioGroup) window.findViewById(R.id.rg_channels);

        for (int i = 1; i <= talkChannels; i++) {
            RadioButton rb = new RadioButton(window.getContext());
            rb.setTag(i);
            //应ui设计要求，自定义RadioButton样式图片
            rb.setButtonDrawable(R.drawable.selector_radiobtn);
            String name = getResources().getString(R.string.analog_channel, i);
            rb.setText(name);
            rb.setPadding(0, 10, 10, 10);
            channels.addView(rb);
        }

        channels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) window.findViewById(radioButtonId);
                channelNoTemp = rb.getTag().toString();
            }
        });
        Button cancel_btn = (Button) window.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mChannelSelectDialog.cancel();
            }
        });

        Button confirm_btn = (Button) window.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                channelNo = Integer.valueOf(channelNoTemp);
                startTalk();
                mChannelSelectDialog.cancel();
            }
        });
    }

    /**
     * 开启语音播放
     */
    private void startTalk() {
        VMSNetSDK.getInstance().openLiveTalkOpt(PLAY_WINDOW_ONE, channelNo, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(OPEN_TALK_FAILURE);
            }

            @Override
            public void onSuccess(Object obj) {
                mHandler.sendEmptyMessage(OPEN_TALK_SUCCESS);
            }
        });
    }

    //预览录像
    private void playBackRecordLive() {
        playBackRecord_img.setBackgroundResource(R.drawable.luxiang);
        if (!mIsRecord) {
            int recordOpt = VMSNetSDK.getInstance().startLiveRecordOpt(PLAY_WINDOW_ONE, FileUtils.getVideoDirPath().getAbsolutePath(), "Video" + System.currentTimeMillis() + ".mp4");
            switch (recordOpt) {
                case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                    UIUtil.showToast(getActivity(), R.string.sd_card_fail);
                    break;
                case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                    UIUtil.showToast(getActivity(), R.string.sd_card_not_enough);
                    break;
                case SDKConstant.LiveSDKConstant.RECORD_FAILED:
                    mIsRecord = false;
                    UIUtil.showToast(getActivity(), R.string.start_record_fail);
                    break;
                case SDKConstant.LiveSDKConstant.RECORD_SUCCESS:
                    mIsRecord = true;
                    playBackRecord_img.setBackgroundResource(R.drawable.lupin);
                    UIUtil.showToast(getActivity(), R.string.start_record_success);
                    break;
            }
        } else {
            VMSNetSDK.getInstance().stopLiveRecordOpt(PLAY_WINDOW_ONE);
            mIsRecord = false;
            UIUtil.showToast(getActivity(), R.string.stop_record_success);
        }
    }

    //回放录像
    private void playBackRecordBack() {
        playBackRecord_img.setBackgroundResource(R.drawable.luxiang);
        //录像按钮点击操作
        if (!mIsRecord) {
            int recordOpt = VMSNetSDK.getInstance().startPlayBackRecordOpt(PLAY_WINDOW_ONE, FileUtils.getVideoDirPath().getAbsolutePath(), "Video" + System.currentTimeMillis() + ".mp4");
            switch (recordOpt) {
                case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                    UIUtil.showToast(getActivity(), R.string.sd_card_fail);
                    break;
                case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                    UIUtil.showToast(getActivity(), R.string.sd_card_not_enough);
                    break;
                case SDKConstant.PlayBackSDKConstant.RECORD_FAILED:
                    mIsRecord = false;
                    UIUtil.showToast(getActivity(), R.string.start_record_fail);
                    break;
                case SDKConstant.PlayBackSDKConstant.RECORD_SUCCESS:
                    mIsRecord = true;
                    playBackRecord_img.setBackgroundResource(R.drawable.lupin);
                    UIUtil.showToast(getActivity(), R.string.start_record_success);
                    break;
            }
        } else {
            VMSNetSDK.getInstance().stopPlayBackRecordOpt(PLAY_WINDOW_ONE);
            mIsRecord = false;
            UIUtil.showToast(getActivity(), R.string.stop_record_success);
        }
    }

    //生成对应video_view
    private void setGrilView(int viewCount, int rowCount) {
        videList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setRowCout(rowCount);
            if (i == 0) {
                videoEntity.setSelect(true);
            } else {
                videoEntity.setSelect(false);
            }
            videList.add(videoEntity);
        }

        //适配器
        video_adapter = new AdapterVideoRecyView(getActivity(), videList);
        video_recyclerview.setAdapter(video_adapter);
        manager = new GridLayoutManager(getActivity(), rowCount);
        //布局管理器
        video_recyclerview.setLayoutManager(manager);

        //条目点击监听
        video_adapter.setOnItemClickListener(new AdapterVideoRecyView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PLAY_WINDOW_ONE = position + 1;
                //设置SurfaceView为选中状态
                LinearLayout itemView = (LinearLayout) view;
                mSurfaceView = itemView.findViewById(R.id.surfaceView);
                add_video = (ImageView) itemView.findViewById(R.id.add_monitory);
                if (videList.get(position).getCamera() != null) {
                    mCamera = videList.get(position).getCamera();
                }
                if (videList.get(position).isSelect()) {//之前已被选中
                    intentAddM();
                } else {
                    for (int i = 0; i < videList.size(); i++) {
                        if (i == position) {
                            videList.get(i).setSelect(true);
                            itemView.setBackgroundResource(R.drawable.item_select_style);
                        } else {
                            videList.get(i).setSelect(false);
                            // positions是RecyclerView中每个item的位置
                            RecyclerView.LayoutManager layoutManager = video_recyclerview.getLayoutManager();
                            View view1 = layoutManager.findViewByPosition(i);
                            if (view1 != null) {
                                view1.setBackground(null);
                            }
                        }
                    }
                }

            }
        });


    }

    private final int RECULET_CODE = 1;//选择完监控点回调

    //跳转到选择监控点界面
    private void intentAddM() {
        Intent intent = new Intent(getActivity(), AddCamerActivity.class);
        startActivityForResult(intent, RECULET_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECULET_CODE://监控列表回调
                if (resultCode == RESULT_OK) {
                    if (null != add_video) {
                        add_video.setVisibility(GONE);
                    }
                    Bundle extras = data.getExtras();
                    SubResourceNodeBean camera = (SubResourceNodeBean) extras.get("camera");
                    mCamera = camera;
                    videList.get(PLAY_WINDOW_ONE - 1).setCamera(mCamera);
                    String loginUrl = TempDatas.getIns().getLoginAddr();
                    //模拟登录
                    MenuTree menuTree = (MenuTree) extras.get("item");
                    String macAddress = MyApplication.getIns().getMacAddress();
                    String videoip = menuTree.getVideoip();
                    String videoUser = menuTree.getVideouser();
                    String videoPassword = menuTree.getVideopassword();
                    if (loginUrl == null) {//从没登录过
                        presenter.login(getString(R.string.https_et) + videoip + ":443", videoUser, videoPassword, macAddress);
                        return;
                    }
                    Log.e("Alan", loginUrl + "=-=" + videoip);
                    if (loginUrl.contains(videoip)) {//上次登录过
                        playCamera();
                    } else {//更换服务器登录
                        presenter.login(getString(R.string.https_et) + videoip + ":443", videoUser, videoPassword, macAddress);
                    }
                }
                break;
        }
    }

    //播放
    private void playCamera() {
        switch (palyType) {
            case 1:
                //  预览
                gotoLive(mCamera);
                break;
            case 2:
                // 回放
               initPlayBackData();
                break;
            default:
                break;
        }
    }
    //开始回放
    private void gotoPlayBack() {
        if (mRecordInfo == null&&mCamera==null) return;
        //开始回放按钮点击操作
        VMSNetSDK.getInstance().startPlayBackOpt(PLAY_WINDOW_ONE, mSurfaceView, mRecordInfo.getSegmentListPlayUrl(), mStartTime, mEndTime, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(START_FAILURE);
            }

            @Override
            public void onSuccess(Object obj) {
                mMessageHandler.sendEmptyMessage(START_SUCCESS);
            }

            @Override
            public void onStatusCallback(int status) {
                //录像片段回放结束
                if (status == RtspClient.RTSPCLIENT_MSG_PLAYBACK_FINISH) {

                }
            }
        });

    }

    //开始预览
    private void gotoLive(SubResourceNodeBean curCamer) {
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
        if (null == mCamera) {
            mHandler.sendEmptyMessage(GET_CAMERA_INFO_FAILURE);
            return;
        }
        UIUtil.showProgressDialog(getActivity(), R.string.loading_process_tip);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                VMSNetSDK.getInstance().startLiveOpt(PLAY_WINDOW_ONE, mCamera.getSysCode(), mSurfaceView, mStreamType, new OnVMSNetSDKBusiness() {
                    @Override
                    public void onFailure() {
                        mHandler.sendEmptyMessage(GET_CAMERA_INFO_FAILURE);
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        mHandler.sendEmptyMessage(GET_CAMERA_INFO_SUCCESS);
                        CNetSDKLog.info("获取监控点详情成功： " +
                                VMSNetSDK.getInstance().getLiveCameraInfo(PLAY_WINDOW_ONE).toString());
                    }
                });
                Looper.loop();
            }
        }.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) { //相当于Fragment的onPause
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            if(mIsRecord){//处于录像状态

                if(palyType==1){
                    VMSNetSDK.getInstance().stopLiveRecordOpt(PLAY_WINDOW_ONE);
                }else{
                    VMSNetSDK.getInstance().stopPlayBackRecordOpt(PLAY_WINDOW_ONE);
                }
                mIsRecord = false;
                playBackRecord_img.setBackgroundResource(R.drawable.luxiang);
            }
            palyType = 1;
            live_view.setVisibility(View.VISIBLE);
            huifang_view.setVisibility(View.INVISIBLE);
            mProgressSeekBar.setVisibility(View.GONE);
            myInit();
        } else { // 相当于Fragment的onResume

        }
    }

    private void myInit() {
        operation_lay.setVisibility(View.VISIBLE);
        view_count_lay.setVisibility(View.VISIBLE);
        contrl_all_lay.setVisibility(View.GONE);

        //停止预览
        stopVideo();
        VIDEO_VIEW_COUNT = 1;
        PLAY_WINDOW_ONE = 1;
        setGrilView(VIDEO_VIEW_COUNT, 1);
        one_view_img.setBackgroundResource(R.drawable.one_2);
        four_view_img.setBackgroundResource(R.drawable.four_2);
        nine_view_img.setBackgroundResource(R.drawable.nine_1);
        mCamera = null;
    }

    //若有视频在播放则带到带到多屏界面
    private void showFirstVideo() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layoutManager = video_recyclerview.getLayoutManager();
                final View itemView1 = layoutManager.findViewByPosition(0);
                if (mCamera != null && itemView1 != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSurfaceView = itemView1.findViewById(R.id.surfaceView);
                            add_video = (ImageView) itemView1.findViewById(R.id.add_monitory);
                            PLAY_WINDOW_ONE = 1;
                            if (palyType == 1) {//预览
                                gotoLive(mCamera);
                            } else if (palyType == 2) {//回放
                               initPlayBackData();
                            }
                            if (null != add_video) {
                                add_video.setVisibility(GONE);
                            }
                        }
                    });

                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);
    }

    @Override
    public void showLoginProgress() {

    }

    @Override
    public void cancelProgress() {

    }

    @Override
    public void onLoginFailed() {

    }

    @Override
    public void onLoginSuccess() {
        playCamera();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopVideo();
    }
    //停止预览和回放
    private void stopVideo() {
        for (int i = 0; i < videList.size(); i++) {
            boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(i + 1);
            if (stopLiveResult) {
                Log.d("Alan", "停止预览成功" + (i + 1));
            }
            boolean stopPlayBackOpt = VMSNetSDK.getInstance().stopPlayBackOpt(i + 1);
            if (stopPlayBackOpt) {
                stopUpdateTimer();
                Log.d("Alan", "停止回放成功" + (i + 1));
            }
        }
    }

}
