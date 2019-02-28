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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.CNetSDKLog;
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
    private LinearLayout fangda_lay;
    private ImageView fangda_img;
    private LinearLayout voice_intercom;
    private GridLayoutManager manager;
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
     * 录像按钮
     */
    private Button mRecordBtn;
    /**
     * 音频按钮
     */
    private Button mAudioBtn;
    /**
     * 语音对讲按钮
     */
    private Button mTalkBtn;
    /**
     * 云台控制
     */
    private Button mPtzBtn;
    /**
     * 云台控制菜单
     */
    private LinearLayout mPtzLayout;
    /**
     * 云台控制命令组one
     */
    private RadioGroup mPtzRadioGroup;
    /**
     * 云台控制命令组two
     */
    private RadioGroup mPtzTwoRadioGroup;
    /**
     * 云台控制命令组three
     */
    private RadioGroup mPtzThreeRadioGroup;
    /**
     * 云台控制命令组four
     */
    private RadioGroup mPtzFourRadioGroup;
    /**
     * 预置点输入框
     */
    private EditText mPresetEdit;
    /**
     * 电子放大控件
     */
    private CheckBox mZoom;

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
//                    case OPEN_TALK_FAILURE:
//                        mFragment.mIsTalkOpen = false;
//                        UIUtil.showToast(mFragment, R.string.start_Talk_fail);
//                        mFragment.mTalkBtn.setText(R.string.start_Talk);
//                        break;
//                    case OPEN_TALK_SUCCESS:
//                        mFragment.mIsTalkOpen = true;
//                        UIUtil.showToast(mFragment, R.string.start_Talk_success);
//                        mFragment.mTalkBtn.setText(R.string.stop_Talk);
//                        break;
//                    case CLOSE_TALK_SUCCESS:
//                        activity.mIsTalkOpen = false;
//                        UIUtil.showToast(activity, R.string.stop_Talk);
//                        activity.mTalkBtn.setText(R.string.start_Talk);
//                        break;
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
     * 暂停按钮
     */
    private Button mPauseButton;
    /**
     * 录像按钮
     */
    private Button mRecordButton;
    /**
     * 音频按钮
     */
    private Button mAudioButton;
    /**
     * 播放进度条控件
     */
    private SeekBar mProgressSeekBar = null;

    /**
     * 是否暂停标志
     */
    private boolean mIsPause;
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
                        activity.finish();
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
        mMessageHandler = new MyPlayBackHandler(this);
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
            fangda_lay = (LinearLayout) view.findViewById(R.id.fangda_lay);
            fangda_img = (ImageView) view.findViewById(R.id.fangda_img);

            one_view_img = (ImageView) view.findViewById(R.id.one_view_img);
            four_view_img = (ImageView) view.findViewById(R.id.four_view_img);
            nine_view_img = (ImageView) view.findViewById(R.id.nine_view_img);

            voice_intercom.setOnClickListener(this);
            one_view_img.setOnClickListener(this);
            four_view_img.setOnClickListener(this);
            nine_view_img.setOnClickListener(this);
            live_lay.setOnClickListener(this);
            huifang_lay.setOnClickListener(this);
            playBackRecord.setOnClickListener(this);
            playBackCapture.setOnClickListener(this);
            contrl_lay.setOnClickListener(this);
            fangda_lay.setOnClickListener(this);
            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    dm.widthPixels,
                    dm.widthPixels
            );

            video_recyclerview = (RecyclerView) view.findViewById(R.id.video_recyclerview);
            video_recyclerview.setLayoutParams(linearParams);
            setGrilView(VIDEO_VIEW_COUNT, 1);
        }
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_lay:
                palyType = 1;
                live_view.setVisibility(View.VISIBLE);
                huifang_view.setVisibility(View.INVISIBLE);
                break;
            case R.id.huifang_lay:
                palyType = 2;
                live_view.setVisibility(View.INVISIBLE);
                huifang_view.setVisibility(View.VISIBLE);
                break;
            case R.id.playBackRecord://本地录像

                break;
            case R.id.fangda_lay://电子放大

                break;
            case R.id.playBackCapture://本地截图

                break;
            case R.id.contrl_lay://云台控制
                if (palyType == 1) {

                } else {
                    UIUtil.showToast(getActivity(), "远程回放不支持云台控制");
                }
                break;
            case R.id.one_view_img://一屏
                if (VIDEO_VIEW_COUNT != 1) {
                    fangda_img.setBackgroundResource(R.drawable.fangda);
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
                    fangda_img.setBackgroundResource(R.drawable.fangda);
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
                    fangda_img.setBackgroundResource(R.drawable.fangda);
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
                UIUtil.showToast(getActivity(), "正在建设中..");
                break;
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
            palyType = 1;
            live_view.setVisibility(View.VISIBLE);
            huifang_view.setVisibility(View.INVISIBLE);
            myInit();
        } else { // 相当于Fragment的onResume

        }
    }

    private void myInit() {
        //停止预览
        for (int i = 0; i < videList.size(); i++) {
            boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(i + 1);
            if (stopLiveResult) {
                Log.d("Alan", "停止预览成功" + (i + 1));
            }
        }
        fangda_img.setBackgroundResource(R.drawable.fangda);
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
        //停止预览
        for (int i = 0; i < videList.size(); i++) {
            boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(i + 1);
            if (stopLiveResult) {
                Log.d("Alan", "surfaceDestroyed() 停止预览成功" + (i + 1));
            }
        }
    }
}
