package com.ivms.ivms8700.view.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.hikvision.sdk.utils.SDKUtil;
import com.hikvision.sdk.utils.UtilAudioPlay;
import com.hikvision.sdk.utils.Utils;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.live.LiveControl;
import com.ivms.ivms8700.multilevellist.TreeAdapter;
import com.ivms.ivms8700.multilevellist.TreePoint;
import com.ivms.ivms8700.multilevellist.TreeUtils;
import com.ivms.ivms8700.playback.ConstantPlayBack;
import com.ivms.ivms8700.playback.PlayBackCallBack;
import com.ivms.ivms8700.playback.PlayBackControl;
import com.ivms.ivms8700.playback.PlayBackParams;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.view.customui.CustomSurfaceView;

import org.MediaPlayer.PlayM4.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class VideoFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SurfaceHolder.Callback, LiveControl.LiveCallBack,PlayBackCallBack {
    private View view;
    private LinearLayout ll_jiankong;
    private RelativeLayout rl_select_btn;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private ListView video_listView;
    /**
     * 列表map。唯一索引。
     */
    private int ID = 100;

    /**
     * 跟节点数据
     */
    private List<TreePoint> genList = new ArrayList<>();
    private HashMap<String, TreePoint> pointMap = new HashMap<>();
    private List<TreePoint> showList = new ArrayList<>();
    /**
     * 监控点
     */
    private Camera mCamera = null;
    /**
     * sdk实例
     */
    private VMSNetSDK mVMSNetSDK = null;
    /**
     * 监控点详细信息
     */
    private CameraInfo cameraInfo = new CameraInfo();

    /**
     * 监控点关联的监控设备信息
     */
    private DeviceInfo deviceInfo = null;

    /**
     * 预览控件
     */
    private CustomSurfaceView mSurfaceView = null;
    /**
     * 进度条
     */
    private ProgressBar progressBar = null;

    /**
     * 设备账户名
     */
    private String username = null;
    /**
     * 设备登录密码
     */
    private String password = null;

    /**
     * 码流类型
     */
    private int mStreamType  = ConstantLiveSDK.MAIN_HING_STREAM;


    //--------回放相关-------

    private static final int PROGRESS_MAX_VALUE = 100;

    /**
     * 存储介质选择
     */
    private RadioGroup          mStorageTypesRG;
    /**
     * 开始按钮
     */
    private Button mStartButton;
    /**
     * 停止按钮
     */
    private Button              mStopButton;
    /**
     * 暂停按钮
     */
    private Button              mPauseButton;
    /**
     * 抓拍按钮
     */
    private Button              mCaptureButton;
    /**
     * 录像按钮
     */
    private Button              mRecordButton;
    /**
     * 音频按钮
     */
    private Button              mAudioButton;
    /**
     * 控制层对象
     */
    private PlayBackControl mPlayBackControl;
    /**
     * 创建消息对象
     */
    private Handler             mMessageHandler;
    /**
     * 是否暂停标签
     */
    private boolean             mIsPause;

    /**
     * 音频是否开启
     */
    private boolean             mIsAudioOpen;
    /**
     * 是否正在录像
     */
    private boolean             mIsRecord;


    /**
     * 录像存储介质
     */
    private int[] mRecordPos;
    /**
     * 录像唯一标识,与录像存储介质一一对应
     */
    private String[] mGuids;
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
     * 开始时间
     */
    private Calendar mStartTime;
    /**
     * 结束时间
     */
    private Calendar mEndTime;

    /**
     * 回放时的参数对象
     */
    private PlayBackParams mParamsObj;
    /**
     * 录像片段
     */
    private RecordSegment mRecordSegment;
    /**
     * 播放进度条
     */
    private SeekBar mProgressSeekbar = null;
    /**
     * 定时器
     */
    private Timer mUpdateTimer = null;
    /**
     * 定时器执行的任务
     */
    private TimerTask mUpdateTimerTask = null;
    /**
     * 电子放大
     */
    private CheckBox mZoom;

    TreeAdapter adapter;
    /**
     * 控制层对象
     */
    private LiveControl mLiveControl = null;
    private Handler mHandler = null;
    private Handler liveHandler = null;
    private LinearLayout live_lay;
    private LinearLayout huifang_lay;
    private View live_view;
    private View huifang_view;
    private int palyType=1;//1代表预览，2代表远程回放
    private LinearLayout playBackRecord;
    private LinearLayout playBackCapture;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mLiveControl) {
            mLiveControl.stop();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void onMessageCallback(int message) {
        sendMessageCase(message);
    }

    private class ViewHandler extends Handler {

        /**
         * Handle system messages here.
         *
         * @param msg
         */
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case Constants.Resource.SHOW_LOADING_PROGRESS:
                    showLoadingProgress();
                    break;
                case Constants.Resource.CANCEL_LOADING_PROGRESS:
                    cancelLoadingProgress();
                    break;
                case Constants.Resource.LOADING_SUCCESS:
                    cancelLoadingProgress();
                    onloadingSuccess();
                    break;
                case Constants.Resource.LOADING_FAILED:
                    cancelLoadingProgress();
                    onloadingFailed();
                    break;
                    case Constants.Resource.LOADING_SUCCESS_TIER:
                    cancelLoadingProgress();
                    break;
            }
        }
    }

    /**
     * 视图更新处理器
     */
    class LiveViewHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.Live.getCameraInfo:
                    UIUtil.showProgressDialog(getActivity(), R.string.loading_camera_info);
                    break;
                case Constants.Live.getCameraInfo_Success:
                    UIUtil.cancelProgressDialog();
                    getDeviceInfo();
                    break;
                case Constants.Live.getCameraInfo_failure:
                    UIUtil.cancelProgressDialog();
                    UIUtil.showToast(getActivity(), R.string.loading_camera_info_failure);
                    break;
                case Constants.Live.getDeviceInfo:
                    UIUtil.showProgressDialog(getActivity(), R.string.loading_device_info);
                    break;
                case Constants.Live.getDeviceInfo_Success:
                    UIUtil.cancelProgressDialog();
                    username = deviceInfo.getUserName();
                    password = deviceInfo.getPassword();
                    Log.i("ivms8700", "device infomation : username:" + username + "  password" + password);

                    clickStartBtn();
                    break;
                case Constants.Live.getDeviceInfo_failure:
                    UIUtil.cancelProgressDialog();
                    UIUtil.showToast(getActivity(), R.string.loading_device_info_failure);
                    break;

                // 视频控制层回调的消息
                case ConstantLiveSDK.RTSP_FAIL:
                    UIUtil.showToast(getActivity(), R.string.rtsp_fail);
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (null != mLiveControl) {
                        mLiveControl.stop();
                    }
                    break;
                case ConstantLiveSDK.RTSP_SUCCESS:
                    UIUtil.showToast(getActivity(), R.string.rtsp_success);
                    break;
                case ConstantLiveSDK.STOP_SUCCESS:
                    UIUtil.showToast(getActivity(), R.string.live_stop_success);
                    break;
                case ConstantLiveSDK.START_OPEN_FAILED:
                    UIUtil.showToast(getActivity(), R.string.start_open_failed);
                    break;
                case ConstantLiveSDK.PLAY_DISPLAY_SUCCESS:
                    UIUtil.showToast(getActivity(), R.string.play_display_success);
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private void sendMessageCase(int i) {
        if (null != mMessageHandler) {
            mMessageHandler.sendEmptyMessage(i);
        }
    }
    //回放处理
    class HuifangHandler extends Handler {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.PlayBack.getCameraInfo:
                    UIUtil.showProgressDialog(getActivity(), R.string.loading_camera_info);
                    break;
                case Constants.PlayBack.getCameraInfo_Success:
                    UIUtil.cancelProgressDialog();
                    mRecordPos = processStorageType(cameraInfo);
                    mGuids = processGuid(cameraInfo);
                    if (null != mRecordPos && 0 < mRecordPos.length) {
                        mStorageType = mRecordPos[0];
                    }
                    if (null != mGuids && 0 < mGuids.length) {
                        mGuid = mGuids[0];
                    }
                    getDeviceInfo();
                    break;
                case Constants.PlayBack.getCameraInfo_failure:
                    UIUtil.cancelProgressDialog();
                    UIUtil.showToast(getActivity(), R.string.loading_camera_info_failure);
                    break;
                case Constants.PlayBack.getDeviceInfo:
                    UIUtil.showProgressDialog(getActivity(), R.string.loading_device_info);
                    break;
                case Constants.PlayBack.getDeviceInfo_Success:
                    UIUtil.cancelProgressDialog();
                    initStorageTypeView();
                    if (null != mRecordPos && 0 < mRecordPos.length) {
                        queryRecordSegment();
                    }
                    break;
                case Constants.PlayBack.getDeviceInfo_failure:
                    UIUtil.cancelProgressDialog();
                    UIUtil.showToast(getActivity(), R.string.loading_device_info_failure);
                    break;

                case Constants.PlayBack.queryRecordSegment_Success:
                    UIUtil.cancelProgressDialog();
                    setParamsObj();
                    startBtnOnClick();
                    break;

                case Constants.PlayBack.queryRecordSegment_failure:
                    UIUtil.cancelProgressDialog();
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    UIUtil.showToast(getActivity(), "录像文件查询失败");
                    break;
                case ConstantPlayBack.START_RTSP_SUCCESS:
                    UIUtil.showToast(getActivity(), "启动取流库成功");
                    startUpdateTimer();
                    break;

                case ConstantPlayBack.START_RTSP_FAIL:

                    UIUtil.showToast(getActivity(), "启动取流库失败");
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    break;

                case ConstantPlayBack.PAUSE_SUCCESS:
                    UIUtil.showToast(getActivity(), "暂停成功");
//                    mPauseButton.setText("恢复");
//                    mIsPause = true;
                    break;

                case ConstantPlayBack.PAUSE_FAIL:
                    UIUtil.showToast(getActivity(), "暂停失败");
//                    mPauseButton.setText("暂停");
//                    mIsPause = false;

                    break;

                case ConstantPlayBack.RESUEM_FAIL:
                    UIUtil.showToast(getActivity(), "恢复播放失败");
//                    mPauseButton.setText("恢复");
//                    mIsPause = true;
                    break;

                case ConstantPlayBack.RESUEM_SUCCESS:
                    UIUtil.showToast(getActivity(), "恢复播放成功");
//                    mPauseButton.setText("暂停");
//                    mIsPause = false;
                    break;

                case ConstantPlayBack.START_OPEN_FAILED:
                    UIUtil.showToast(getActivity(), "启动播放库失败");
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    break;

                case ConstantPlayBack.PLAY_DISPLAY_SUCCESS:
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    break;
                case ConstantPlayBack.CAPTURE_FAILED_NPLAY_STATE:
                    UIUtil.showToast(getActivity(), "非播状态不能抓怕");
                    break;
                case ConstantPlayBack.PAUSE_FAIL_NPLAY_STATE:
                    UIUtil.showToast(getActivity(), "非播放状态不能暂停");
                    break;
                case ConstantPlayBack.RESUEM_FAIL_NPAUSE_STATE:
                    UIUtil.showToast(getActivity(), "非播放状态");
                    break;

                case RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION:
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    UIUtil.showToast(getActivity(), "RTSP链接异常");
                    break;

                case ConstantPlayBack.MSG_REMOTELIST_UI_UPDATE:
                    updateRemotePlayUI();
                    break;

            }
        }
    }
    private void updateRemotePlayUI() {
        if (null == mPlayBackControl) {
            return;
        }
        Player palyer = mPlayBackControl.getPlayer();
        int status = mPlayBackControl.getPlayBackState();
        if (palyer != null && status == PlayBackControl.PLAYBACK_PLAY) {
            long osd = mPlayBackControl.getOSDTime();
            handlePlayProgress(osd);
        }
    }
    /**
     * 开启播放
     * @author lvlingdi 2016-4-19 下午5:01:22
     */
    private void startBtnOnClick() {
        progressBar.setVisibility(View.VISIBLE);
        if (null != mPlayBackControl) {
            mPlayBackControl.startPlayBack(mParamsObj);
        }
    }
    /**
     * 设置回放参数
     * @author lvlingdi 2016-4-21 下午4:41:19
     */
    private void setParamsObj() {
        if (null != deviceInfo) {
            mParamsObj.name = deviceInfo.getUserName() == null ? "" : deviceInfo.getUserName() ;
            mParamsObj.passwrod = deviceInfo.getPassword() == null ? "" : deviceInfo.getPassword();



        }
        if (null != mRecordInfo) {
            String rtspUri = VMSNetSDK.getInstance().getPlayBackRtspUrl(cameraInfo, mRecordInfo.getSegmentListPlayUrl(), mStartTime, mEndTime);
            mParamsObj.startTime = SDKUtil.calendarToABS(mStartTime);
            mParamsObj.endTime = SDKUtil.calendarToABS(mEndTime);
            mParamsObj.url = rtspUri;

        }
    }
    /**
     *
     * @author lvlingdi 2016-4-27 下午3:39:33
     * @param
     */
    private void handlePlayProgress(long osd) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(osd);
        long begin = mStartTime.getTimeInMillis();
        long end = mEndTime.getTimeInMillis();

        double x = ((osd - begin) * PROGRESS_MAX_VALUE) / (double) (end - begin);
        int progress = (int) x;
        mProgressSeekbar.setProgress(progress);
        int beginTimeClock = (int) ((osd - begin) / 1000);
//        updateTimeBucketBeginTime(beginTimeClock);
//        nextPlayPrompt(osd, end);
    }

    /**
     * 启动定时器
     *
     * @see
     * @since V1.0
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {
                mMessageHandler.sendEmptyMessage(ConstantPlayBack.MSG_REMOTELIST_UI_UPDATE);

            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }

    /**
     * 停止定时器
     * @author lvlingdi 2016-4-27 下午3:49:36
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
    /**
     * @author lvlingdi 2016-4-21 上午10:20:11
     */
    private void initStorageTypeView() {
        if (mRecordPos == null || mRecordPos.length <= 0) {
            return;
        }
//        for (int i = 0;i < mRecordPos.length;i++) {
//            RadioButton rb = new RadioButton(getActivity());
////                if (0 == i) {
////                    rb.setChecked(true);
////                }
//            rb.setTag(i);
//            switch (mRecordPos[i]) {
//                case com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_NVT:
//                    rb.setText(com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_NVT_STR);
//                    break;
//
//                case com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_PU:
//                    rb.setText(com.hikvision.sdk.consts.ConsstantPlayBack.PlayBack.RECORD_TYPE_PU_STR);
//                    break;
//
//                case com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_NVR:
//                    rb.setText(com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_NVR_STR);
//                    break;
//
//                case com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_CVM:
//                    rb.setText(com.hikvision.sdk.consts.ConstantPlayBack.PlayBack.RECORD_TYPE_CVM_STR);
//                    break;
//
//                default:
//                    break;
//            }
//            mStorageTypesRG.addView(rb);
//            mStorageTypesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//                @Override
//                public void onCheckedChanged(RadioGroup arg0, int arg1) {
//                    int radioButtonId = arg0.getCheckedRadioButtonId();
//                    RadioButton rb = (RadioButton)findViewById(radioButtonId);
//                    String type = rb.getTag().toString();
//                    int index = Integer.valueOf(type);
//                    if (null != mRecordPos && index < mRecordPos.length) {
//                        mStorageType = mRecordPos[index];
//                    }
//                    if (null != mGuids && index < mGuids.length) {
//                        mGuid = mGuids[index];
//                    }
//                    stopBtnOnClick();
//                    mProgressBar.setVisibility(View.VISIBLE);
//                    queryRecordSegment();
//                }
//            });
//        }

    }
    /**
     * 查找录像片段
     * @author lvlingdi 2016-4-21 下午3:30:18
     */
    private void queryRecordSegment() {
        if (null == cameraInfo) {
            Log.e("ivms8700", "queryRecordSegment==>>cameraInfo is null");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(Constants.PlayBack.queryRecordSegment_failure);
            }

            @Override
            public void loading() {


            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof RecordInfo) {
                    mRecordInfo = ((RecordInfo)obj);

                    //级联设备的时候
                    if (null != mRecordInfo.getSegmentList() && 0 < mRecordInfo.getSegmentList().size())  {
                        mRecordSegment = mRecordInfo.getSegmentList().get(0);
                    }
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.queryRecordSegment_Success);
                }
            }

        });
        mVMSNetSDK.queryRecordSegment(cameraInfo, mStartTime, mEndTime, mStorageType, mGuid);
    }
    /**
     * 解析录像存储类型
     * @author lvlingdi 2016-4-21 上午10:07:33
     * @param cameraInfo
     */
    private int[] processStorageType(CameraInfo cameraInfo) {
        String pos = cameraInfo.getRecordPos();
        if (SDKUtil.isEmpty(pos)) {
            return null;
        }
        String[] recordPos = pos.split(",");
        int[] types = new int[recordPos.length];
        for (int i = 0; i < recordPos.length; i++) {
            types[i] = Integer.valueOf(recordPos[i]);
        }
        return types;

    }
    /**
     * 解析Guid
     * @author lvlingdi 2016-4-21 上午10:09:12
     * @param cameraInfo
     */
    private String[] processGuid(CameraInfo cameraInfo) {
        String guid = cameraInfo.getGuid();
        if (SDKUtil.isEmpty(guid)) {
            return null;
        }
        String[] guids = guid.split(",");
        return guids;
    }
    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        if (null == cameraInfo) {
            Log.e("ivms8700", "getDeviceInfo==>>cameraInfo is null");
            return;
        }

        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                if(palyType==1) {
                    liveHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_failure);
                }else{
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.getDeviceInfo_failure);
                }
            }

            @Override
            public void loading() {
                if(palyType==1) {
                    liveHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
                }else{
                    mMessageHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof DeviceInfo) {
                    deviceInfo = (DeviceInfo) data;
                    if(palyType==1){
                        liveHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
                    }else{
                        mMessageHandler.sendEmptyMessage(Constants.PlayBack.getDeviceInfo_Success);
                    }

                }
            }
        });

        boolean flag = mVMSNetSDK.getDeviceInfo(cameraInfo.getDeviceID());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.video_layout, container, false);
            ll_jiankong = (LinearLayout) view.findViewById(R.id.ll_jiankong);
            rl_select_btn = (RelativeLayout) view.findViewById(R.id.rl_select_btn);
            rl_select_btn.setOnClickListener(this);
            //监控点列表
            video_listView = (ListView) view.findViewById(R.id.video_listView);
            adapter = new TreeAdapter(getContext(), genList, pointMap);
            video_listView.setAdapter(adapter);
            setListener();

            mSurfaceView = (CustomSurfaceView)view.findViewById(R.id.surfaceView);
            mSurfaceView.getHolder().addCallback(this);
            progressBar = (ProgressBar)view.findViewById(R.id.live_progress_bar);

            live_lay=(LinearLayout)view.findViewById(R.id.live_lay);
            huifang_lay=(LinearLayout)view.findViewById(R.id.huifang_lay);
            live_view=(View)view.findViewById(R.id.live_view);
            huifang_view=(View)view.findViewById(R.id.huifang_view);
            live_lay.setOnClickListener(this);
            huifang_lay.setOnClickListener(this);

            playBackRecord=(LinearLayout)view.findViewById(R.id.playBackRecord); //本地录像
            playBackCapture=(LinearLayout)view.findViewById(R.id.playBackCapture); //本地截图
            playBackRecord.setOnClickListener(this);
            playBackCapture.setOnClickListener(this);
        }
        // 初次加载根节点数据
        getRootControlCenter();
        setAnimation();
        return view;
    }

    private void setListener() {
        video_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getSubResourceList(Integer.parseInt(showList.get(position).getRootCtrlCenter().getNodeType()), showList.get(position).getRootCtrlCenter().getId()
                            , position, showList.get(position));
                } else {

                    int nodeType = 0;
                    Object node = showList.get(position).getSubResourceNodeBean();
                    nodeType = ((SubResourceNodeBean)node).getNodeType();
                    Log.i("ivms8700","nodeType=-="+nodeType);

                    if(HttpConstants.NodeType.TYPE_CAMERA_OR_DOOR == nodeType){
                        Log.i("ivms8700","=-=准备进入播放");
                        // 构造camera对象
                        final Camera camera = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean)node);

                                    switch (palyType) {
                                        case 1:
                                            // 预览
                                            if (VMSNetSDK.getInstance().isHasLivePermission(camera)) {
                                                ll_jiankong.startAnimation(mHiddenAction);
                                                ll_jiankong.setVisibility(View.GONE);
                                                gotoLive(camera);
                                            } else {
                                                UIUtil.showToast(getActivity(), R.string.no_permission);
                                            }
                                            break;
                                        case 2:
                                            // 回放
                                            if (VMSNetSDK.getInstance().isHasPlayBackPermission(camera)) {
                                                gotoPlayBack(camera);
                                            } else {
                                                UIUtil.showToast(getActivity(), R.string.no_permission);
                                            }
                                            break;
                                        default:
                                            break;
                                    }

                    }else{

                        getSubResourceList(showList.get(position).getSubResourceNodeBean().getNodeType(), showList.get(position).getSubResourceNodeBean().getId()
                                , position, showList.get(position));
                    }

                }


            }
        });
    }

    /**
     * 回放监控点
     * @param
     */
    private void gotoPlayBack(Camera camera) {
        if (camera == null) {
            Log.e(Constants.LOG_TAG, "gotoPlayBack():: fail");
            return;
        }
        initData(camera);
    }

    /**
     * 初始化回放数据
     * @author lvlingdi 2016-4-19 下午5:20:50
     */
    private void initData(Camera camera) {
        mMessageHandler = new HuifangHandler();
        mVMSNetSDK = VMSNetSDK.getInstance();
        // 初始化远程回放控制层对象
        mPlayBackControl = new PlayBackControl();
        // 设置远程回放控制层回调
        mPlayBackControl.setPlayBackCallBack(this);
        // 创建远程回放需要的参数
        mParamsObj = new PlayBackParams();
        // 播放控件
        mParamsObj.surfaceView = mSurfaceView;
        //监控点
        mCamera = camera;


        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();

        mStartTime.set(year, month, day, 0, 0, 0);
        mEndTime.set(year, month, day, 23, 59, 59);
        getCameraInfo();
    }
   //进入预览
    private void gotoLive(Camera camera) {
        if (camera == null) {
            Log.e(Constants.LOG_TAG, "gotoLive():: fail");
            return;
        }
        mCamera = camera;
        mVMSNetSDK = VMSNetSDK.getInstance();
        liveHandler = new LiveViewHandler();
        mLiveControl = new LiveControl();
        mLiveControl.setLiveCallBack(this);
        getCameraInfo();
    }
    /**
     * 获取监控点详细信息
     */
    private void getCameraInfo() {
        if (null == mCamera) {
            Log.e("ivms8700", "getCameraInfo==>>camera is null");
            return;
        }
        if(palyType==1){
            liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
        }else{
            mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo);
        }

        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
               if(palyType==1){
                   liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo_failure);
               }else{
                   mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo_failure);
               }


            }

            @Override
            public void loading() {
                if(palyType==1){
                    liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
                }else{
                    mMessageHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
                }

            }
            @Override
            public void onSuccess(Object data) {
                if (data instanceof CameraInfo) {
                    cameraInfo = (CameraInfo) data;
                    if(palyType==1){
                        liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo_Success);
                    }else{
                        mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo_Success);
                    }

                }
            }
        });
        boolean flag = mVMSNetSDK.getCameraInfo(mCamera);

    }

    /**
     * start play video
     */
    private void clickStartBtn() {
        progressBar.setVisibility(View.VISIBLE);
        String liveUrl = VMSNetSDK.getInstance().getPlayUrl(cameraInfo, mStreamType);
        mLiveControl.setLiveParams(liveUrl, null == username ? "" : username, null == password ? "" : password);
        if (LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
            mLiveControl.stop();
        }

        if (LiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
            progressBar.setVisibility(View.GONE);
            mLiveControl.startLive(mSurfaceView);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_btn:
                if (ll_jiankong.getVisibility() == View.GONE) {
                    ll_jiankong.startAnimation(mShowAction);
                    ll_jiankong.setVisibility(View.VISIBLE);
                } else {
                    ll_jiankong.startAnimation(mHiddenAction);
                    ll_jiankong.setVisibility(View.GONE);
                }
                break;
            case R.id.live_lay:
                palyType=1;
                live_view.setVisibility(View.VISIBLE);
                huifang_view.setVisibility(View.INVISIBLE);
                break;
            case R.id.huifang_lay:
                palyType=2;
                live_view.setVisibility(View.INVISIBLE);
                huifang_view.setVisibility(View.VISIBLE);
                break;
            case R.id.playBackRecord://本地录像
                if(palyType==1){
                    recordBtnOnClick_live();
                }else{
                    recordBtnOnClick();
                }


                break;
            case R.id.playBackCapture://本地截图
                if(palyType==1){
                    clickCaptureBtn_live();
                }else{
                    captureBtnOnClick();
                }

                break;

        }
    }

    /**
     * 获取根控制中心
     */
    public void getRootControlCenter() {
        mHandler = new ViewHandler();
        boolean flag = false;
//        showLoadingProgress();
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
             */
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);

                if (obj instanceof RootCtrlCenter) {
                    video_listView.setVisibility(View.VISIBLE);
                    genList.clear();
                    showList.clear();
//                    TreePoint t = new TreePoint(((RootCtrlCenter) obj).getId(), ((RootCtrlCenter) obj).getName(), 0, 0, false, 0, true, null, (RootCtrlCenter) obj);
                    TreePoint t = new TreePoint(ID, ((RootCtrlCenter) obj).getName(), 0, 0, false, 0, true, null, (RootCtrlCenter) obj);
                    pointMap.put(t.getID() + "", t);
                    genList.add(t);
                    showList.add(t);
                    adapter.setPointList(showList);
                    adapter.setPointMap(pointMap);
                }
                mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS);
            }

            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onFailure()
             */
            @Override
            public void onFailure() {
                super.onFailure();
                mHandler.sendEmptyMessage(Constants.Resource.LOADING_FAILED);
            }
        });
        flag = VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, HttpConstants.SysType.TYPE_VIDEO, 15);
    }

    /**
     * 获取下级资源列表
     *
     * @param parentNodeType 父节点类型
     * @param pId            父节点ID
     *                       level   当前层级
     */
    private void getSubResourceList(int parentNodeType, int pId, final int position, final TreePoint treePoint) {
        boolean flag = false;
        showLoadingProgress();

        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
             */
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof List<?>) {
                    List<SubResourceNodeBean> list = new ArrayList<SubResourceNodeBean>();
                    list.addAll((Collection<? extends SubResourceNodeBean>) obj);
                    List<TreePoint> subList = new ArrayList<>();
                    Log.i("ivm-8700", new Gson().toJson(list).toString());


                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            ID++;
                            subList.add(new TreePoint(ID, list.get(i).getName(),
                                    treePoint.getID(), i, false, 0, true, list.get(i), null));
                        }
                        if (!treePoint.isExpand()) {//点击项有子数据但未展开，展开操作。
                            for (int i = 0; i < subList.size(); i++) {
                                pointMap.put(subList.get(i).getID() + "", subList.get(i));
                                Log.i("ivm-8700", "" + new Gson().toJson(genList).toString());
                                genList.add(new TreePoint(subList.get(i).getID(), subList.get(i).getNNAME(), treePoint.getID(), i, false, getLayer(subList.get(i), pointMap), true, subList.get(i).getSubResourceNodeBean(), null));
                            }


                            int g = position;
                            for (int j = 1; j < genList.size(); j++) {
                                if (genList.get(j).getPARENTID() == treePoint.getID()) {
                                    g = g + 1;
                                    showList.add(g, genList.get(j));
                                }
                            }
                            showList.get(position).setExpand(true);
                            adapter.setPointMap(pointMap);
                            adapter.setPointList(showList);
                            adapter.notifyDataSetChanged();
                        } else if (treePoint.isExpand()) {
                            List<TreePoint> delList = new ArrayList<>();
                            for (int k = position + 1; k < showList.size(); k++) {
                                if (showList.get(k).getLayer() > treePoint.getLayer()) {
                                    delList.add(showList.get(k));
                                } else {
                                    break;
                                }
                            }
                            genList.removeAll(delList);
                            showList.removeAll(delList);
                            for (TreePoint rr : delList) {
                                pointMap.remove(rr.getID());
                            }
                            showList.get(position).setExpand(false);
                            adapter.setPointMap(pointMap);
                            adapter.setPointList(showList);
                            adapter.notifyDataSetChanged();
                        }

                    } else {

                        showList.get(position).setHasSubDatas(false);
                        return;
                    }
                    mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS_TIER);
                }
            }

            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onFailure()
             */
            @Override
            public void onFailure() {
                super.onFailure();
                mHandler.sendEmptyMessage(Constants.Resource.LOADING_FAILED);
            }
        });

        flag = VMSNetSDK.getInstance().getSubResourceList(1,
                15, HttpConstants.SysType.TYPE_VIDEO, parentNodeType, pId + "");
    }

    /**
     * capture picture from playing video
     */
    private void clickCaptureBtn_live() {
        if (null != mLiveControl) {
            // 随机生成一个1到10000的数字，用于抓拍图片名称的一部分，区分图片，开发者可以根据实际情况修改
            // 区分图片名称的方法
            int recordIndex = new Random().nextInt(10000);
            boolean ret = mLiveControl.capture(Utils.getPictureDirPath().getAbsolutePath(), "Picture" + recordIndex + ".jpg");
            if (ret) {
                UIUtil.showToast(getActivity(), "抓拍成功");
                UtilAudioPlay.playAudioFile(getActivity(), R.raw.paizhao);
            } else {
                UIUtil.showToast(getActivity(), "抓拍失败");
                Log.e("ivms8700" ,"clickCaptureBtn():: 抓拍失败");
            }
        }
    }

    /**
     * 录像 void
     * @author lvlingdi 2016-4-26 下午3:35:57
     */
    private void recordBtnOnClick_live() {
        if (null != mLiveControl) {
            if (!mIsRecord) {
                // 随即生成一个1到10000的数字，用于录像名称的一部分，区分图片，开发者可以根据实际情况修改区分录像名称的方法
                int recordIndex = new Random().nextInt(10000);
                mLiveControl.startRecord(Utils.getVideoDirPath().getAbsolutePath(), "Video" + recordIndex
                        + ".mp4");
                mIsRecord = true;
                UIUtil.showToast(getActivity(), "启动录像成功");
//                mRecordBtn.setText("停止录像");
            } else {
                mLiveControl.stopRecord();
                mIsRecord = false;
                UIUtil.showToast(getActivity(), "停止录像成功");
//                mRecordBtn.setText("开始录像");
            }
        }
    }

    /**
     * 抓拍
     * @author lvlingdi 2016-4-26 下午3:13:33
     */
    private void captureBtnOnClick() {
        if (null != mPlayBackControl) {
            // 随即生成一个1到10000的数字，用于抓拍图片名称的一部分，区分图片
            int recordIndex = new Random().nextInt(10000);
            boolean ret = mPlayBackControl.capture(Utils.getPictureDirPath().getAbsolutePath(), "Picture" + recordIndex
                    + ".jpg");
            if (ret) {
                UIUtil.showToast(getActivity(), "抓拍成功");
                UtilAudioPlay.playAudioFile(getActivity(), R.raw.paizhao);
            } else {
                UIUtil.showToast(getActivity(), "抓拍失败");
                Log.e("ivms8700", "captureBtnOnClick():: 抓拍失败");
            }
        }
    }

    /**
     * 录像 void
     * @author lvlingdi 2016-4-26 下午3:15:38
     */
    private void recordBtnOnClick() {
        if (null != mPlayBackControl) {
            if (!mIsRecord) {
                int recordIndex = new Random().nextInt(10000);
                boolean ret = mPlayBackControl.startRecord(Utils.getVideoDirPath().getAbsolutePath(), "Video" + recordIndex + ".mp4");
                if (ret) {
                    mIsRecord = true;
                    UIUtil.showToast(getActivity(), "启动录像成功");
//                    mRecordButton.setText("停止录像");
                } else {
                    mIsRecord = false;
                    UIUtil.showToast(getActivity(), "启动录像失败");
                }
            } else {
                mPlayBackControl.stopRecord();
                mIsRecord = false;
                UIUtil.showToast(getActivity(), "停止录像成功");
//                mRecordButton.setText("开始录像");
            }
        }
    }



    /**
     * 加载进度条
     */
    private void showLoadingProgress() {
        UIUtil.showProgressDialog(getActivity(), R.string.loading_process_tip);
    }

    /**
     * 取消进度条
     */
    private void cancelLoadingProgress() {
        UIUtil.cancelProgressDialog();
    }

    /**
     * 加载失败
     *
     * @author hanshuangwu 2016年1月21日 下午3:25:52
     */
    private void onloadingFailed() {
        UIUtil.showToast(getActivity(), R.string.loading_failed);
    }

    /**
     * 加载成功
     *
     * @author hanshuangwu 2016年1月21日 下午3:25:59
     */
    private void onloadingSuccess() {
//        updateData();
        UIUtil.showToast(getActivity(), R.string.loading_success);
        adapter.notifyDataSetChanged();
    }

    private void setAnimation() {
        //显示动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        //关闭动画
        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(500);
    }


    private int getLayer(TreePoint treePoint, HashMap<String, TreePoint> pointMap) {
        return TreeUtils.getLevel(treePoint, pointMap);
    }

    private String getSubmitResult(TreePoint treePoint) {
        StringBuilder sb = new StringBuilder();
        addResult(treePoint, sb);
        String result = sb.toString();
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private void addResult(TreePoint treePoint, StringBuilder sb) {
        if (treePoint != null && sb != null) {
            sb.insert(0, treePoint.getNNAME() + "-");
            if (!"0".equals(treePoint.getPARENTID())) {
                addResult(pointMap.get(treePoint.getPARENTID()), sb);
            }
        }
    }
}
