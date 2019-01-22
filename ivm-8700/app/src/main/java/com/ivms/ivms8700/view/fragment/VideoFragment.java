package com.ivms.ivms8700.view.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.consts.PTZCmd;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.SDKUtil;
import com.hikvision.sdk.utils.UtilAudioPlay;
import com.hikvision.sdk.utils.Utils;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.VideoEntity;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.live.LiveControl;
import com.ivms.ivms8700.live.PCRect;
import com.ivms.ivms8700.mysdk.MyVMSNetSDK;
import com.ivms.ivms8700.playback.ConstantPlayBack;
import com.ivms.ivms8700.playback.PlayBackCallBack;
import com.ivms.ivms8700.playback.PlayBackControl;
import com.ivms.ivms8700.playback.PlayBackParams;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.view.AddCamerActivity;
import com.ivms.ivms8700.view.AddMonitoryActivity;
import com.ivms.ivms8700.view.adapter.AdapterVideoRecyView;
import com.ivms.ivms8700.view.customui.CustomRect;
import com.ivms.ivms8700.view.customui.CustomSurfaceView;
import com.ivms.ivms8700.view.customui.CustomSurfaceView.OnZoomListener;

import org.MediaPlayer.PlayM4.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static com.hik.mcrsdk.MCRSDK.init;

public class VideoFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback, LiveControl.LiveCallBack, PlayBackCallBack {

    private final String TAG = "Alan";
    private final int RECULET_CODE = 1;//选择完监控点回调
    private View view;
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
     * 监控点
     */
    private Camera mCamera = null;

    /**
     * sdk实例
     */
    private MyVMSNetSDK mVMSNetSDK = null;
    /**
     * 监控点详细信息
     */
    private CameraInfo cameraInfo = new CameraInfo();
    Map cameraMap = new HashMap<Integer, CameraInfo>();
    /**
     * 监控点关联的监控设备信息
     */
    private DeviceInfo deviceInfo = null;
    Map deviceMap = new HashMap<>();

    /**
     * 预览控件 --当前点击的 CustomSurfaceView
     */
    private CustomSurfaceView curSurfaceView = null;


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
    private int mStreamType = ConstantLiveSDK.MAIN_HING_STREAM;

    /**
     * 是否正在云台控制
     */
    private boolean mIsPtzStart;
    /**
     * 云台控制命令
     */
    private int mPtzcommand;
    //--------回放相关-------

    private static final int PROGRESS_MAX_VALUE = 100;

    /**
     * 存储介质选择
     */
    private RadioGroup mStorageTypesRG;
    /**
     * 控制层对象
     */
    private PlayBackControl mPlayBackControl;
    /**
     * 控制层对象
     */
    private PlayBackControl tmPlayBackControl;
    /**
     * 创建消息对象
     */
    private Handler mMessageHandler;
    /**
     * 是否正在录像
     */
    private boolean mIsRecord;

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
//    private SeekBar mProgressSeekbar = null;
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
    boolean isZoom = false;

    /**
     * 控制层对象
     */
    private LiveControl mLiveControl = null;
    private LiveControl tmLiveControl = null;
    private Handler liveHandler = null;
    private LinearLayout live_lay;
    private LinearLayout huifang_lay;
    private View live_view;
    private View huifang_view;
    private int palyType = 1;//1代表预览，2代表远程回放
    private LinearLayout playBackRecord;
    private LinearLayout playBackCapture;
    private LinearLayout contrl_lay;
    private AlertDialog alertDialog; //信息框
    private int VIDEO_VIEW_COUNT = 1;
    private int mCurIndex = 0;//当前显示的下标

    private RecyclerView video_recyclerview;
    private AdapterVideoRecyView video_adapter;
    private ImageView one_view_img;
    private ImageView four_view_img;
    private ImageView nine_view_img;
    private ImageView add_video;
    List<VideoEntity> videList = null;
    private Camera curCamer; //当前camer
    private ImageView playBackRecord_img;
    private LinearLayout fangda_lay;
    private ImageView fangda_img;
    private LinearLayout voice_intercom;
    /**
     * 语音对讲是否开启
     */
    private boolean mIsTalkOpen;
    private SeekBar mProgressSeekbar;//回放进度条
    private GridLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
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
            mProgressSeekbar = (SeekBar) view.findViewById(R.id.progress_seekbar);
            mProgressSeekbar.setVisibility(View.GONE);
            mProgressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                /**
                 * 拖动条停止拖动的时候调用
                 */
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                    if (null != mParamsObj && null != mStartTime && null != mEndTime) {
                        stopBtnOnClick();
                        int progress = arg0.getProgress();
                        if (progress == PROGRESS_MAX_VALUE) {
                            stopBtnOnClick();
                            return;
                        }
                        long begin = mStartTime.getTimeInMillis();
                        long end = mEndTime.getTimeInMillis();
                        long avg = (end - begin) / PROGRESS_MAX_VALUE;
                        long trackTime = begin + (progress * avg);
                        Calendar track = Calendar.getInstance();
                        track.setTimeInMillis(trackTime);
                        mParamsObj.startTime = SDKUtil.calendarToABS(track);
                        startBtnOnClick();
                    }
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) { //相当于Fragment的onPause
            if (liveHandler != null) {
                liveHandler.removeCallbacksAndMessages(null);
            }
            if (mMessageHandler != null) {
                mMessageHandler.removeCallbacksAndMessages(null);
            }

            mProgressSeekbar.setVisibility(View.GONE);

            palyType = 1;
            live_view.setVisibility(View.VISIBLE);
            huifang_view.setVisibility(View.INVISIBLE);
            myInit();
        } else { // 相当于Fragment的onResume

        }
    }

    //生成对应video_view
    private void setGrilView(int viewCount, int rowCount) {
        videList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setRowCout(rowCount);
            LiveControl liveControl = new LiveControl();
            liveControl.setLiveCallBack(this);
            videoEntity.setmLiveControl(liveControl);
            PlayBackControl playBackControl = new PlayBackControl();
            playBackControl.setPlayBackCallBack(this);
            videoEntity.setmPlayBackControl(playBackControl);
            if(i==0){
                videoEntity.setSelect(true);
            }else{
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
                mCurIndex = position;
                //设置SurfaceView为选中状态
                LinearLayout itemView = (LinearLayout) view;
                curSurfaceView = itemView.findViewById(R.id.surfaceView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.live_progress_bar);
                add_video = (ImageView) itemView.findViewById(R.id.add_monitory);
                mCurIndex = position;
                tmPlayBackControl = videList.get(position).getmPlayBackControl();
                mPlayBackControl=tmPlayBackControl;
                tmLiveControl = videList.get(position).getmLiveControl();
                mLiveControl=tmLiveControl;
            if(videList.get(position).isSelect()){//之前已被选中
                intentAddM();
            }else{
                for (int i=0;i<videList.size();i++){
                    if(i==position){
                        videList.get(i).setSelect(true);
                        itemView.setBackgroundResource(R.drawable.item_select_style);
                    }else{
                        videList.get(i).setSelect(false);
                        // positions是RecyclerView中每个item的位置
                        RecyclerView.LayoutManager layoutManager = video_recyclerview.getLayoutManager();
                        View view1 = layoutManager.findViewByPosition(i);
                        if(view1!=null){
                            view1.setBackground(null);
                        }

                    }
                }
//                video_adapter.notifyDataSetChanged();
            }

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_lay:
                mProgressSeekbar.setVisibility(View.GONE);
                palyType = 1;
                live_view.setVisibility(View.VISIBLE);
                huifang_view.setVisibility(View.INVISIBLE);
                myInit();
                break;
            case R.id.huifang_lay:
                mProgressSeekbar.setVisibility(View.VISIBLE);
                palyType = 2;
                live_view.setVisibility(View.INVISIBLE);
                huifang_view.setVisibility(View.VISIBLE);
                myInit();
                break;
            case R.id.playBackRecord://本地录像
                if (palyType == 1) {
                    recordBtnOnClick_live();
                } else {
                    recordBtnOnClick();
                }
                break;
            case R.id.fangda_lay://电子放大
                if (isZoom) {
                    isZoom = false;
                    fangda_img.setBackgroundResource(R.drawable.fangda);
                } else {
                    isZoom = true;
                    fangda_img.setBackgroundResource(R.drawable.fangda_select);
                }
                zoomBtnOnClick();
                break;
            case R.id.playBackCapture://本地截图
                if (palyType == 1) {
                    clickCaptureBtn_live();
                } else {
                    captureBtnOnClick();
                }
                break;
            case R.id.contrl_lay://云台控制
                if (palyType == 1) {
                    if (null != mLiveControl && mLiveControl.getLiveState() == LiveControl.LIVE_PLAY) {//处于播放状态
                        showList();
                    } else {
                        UIUtil.showToast(getActivity(), "未处于预览状态");
                    }
                } else {
                    UIUtil.showToast(getActivity(), "远程回放不支持云台控制");
                }
                break;
            case R.id.one_view_img://一屏
                if (VIDEO_VIEW_COUNT != 1) {
                    if (mIsRecord) {
                        UIUtil.showToast(getActivity(), getString(R.string.video_ing));
                        return;
                    }
                    isZoom = false;
                    fangda_img.setBackgroundResource(R.drawable.fangda);
                    initControl();
                    VIDEO_VIEW_COUNT = 1;
//                    setGrilView(VIDEO_VIEW_COUNT, 1);
                    for(int i=0;i<videList.size();i++){
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
                    if (mIsRecord) {
                        UIUtil.showToast(getActivity(), getString(R.string.video_ing));
                        return;
                    }
                    isZoom = false;
                    fangda_img.setBackgroundResource(R.drawable.fangda);
                    initControl();
                    VIDEO_VIEW_COUNT = 4;
//                    setGrilView(VIDEO_VIEW_COUNT, 2);
                    for(int i=0;i<videList.size();i++){
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
                    if (mIsRecord) {
                        UIUtil.showToast(getActivity(), getString(R.string.video_ing));
                        return;
                    }
                    isZoom = false;
                    fangda_img.setBackgroundResource(R.drawable.fangda);
                    initControl();
                    VIDEO_VIEW_COUNT = 9;
//                    setGrilView(VIDEO_VIEW_COUNT, 3);
                    for(int i=0;i<videList.size();i++){
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

    private void myInit() {
        if (mIsRecord) {
            //停止录像
            recordBtnOnClick_live();
        }
        isZoom = false;
        fangda_img.setBackgroundResource(R.drawable.fangda);
        initControl();
        VIDEO_VIEW_COUNT = 1;
        setGrilView(VIDEO_VIEW_COUNT, 1);

        one_view_img.setBackgroundResource(R.drawable.one_2);
        four_view_img.setBackgroundResource(R.drawable.four_2);
        nine_view_img.setBackgroundResource(R.drawable.nine_1);
        curCamer = null;
    }

    //若有视频在播放则带到带到多屏界面
    private void showFirstVideo() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layoutManager = video_recyclerview.getLayoutManager();
                final View itemView1 = layoutManager.findViewByPosition(0);
                if (curCamer != null && itemView1 != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //            AdapterVideoRecyView.ViewHolder myViewHolder = (AdapterVideoRecyView.ViewHolder) video_recyclerview.getChildViewHolder(itemView);
//            myViewHolder.autoClick();
                            curSurfaceView = itemView1.findViewById(R.id.surfaceView);
                            progressBar = (ProgressBar) itemView1.findViewById(R.id.live_progress_bar);
                            add_video = (ImageView) itemView1.findViewById(R.id.add_monitory);
                            mCurIndex = 0;
                            tmPlayBackControl = videList.get(0).getmPlayBackControl();
                            tmLiveControl = videList.get(0).getmLiveControl();
                            mPlayBackControl = tmPlayBackControl;
                            mLiveControl = tmLiveControl;
                            if (palyType == 1) {//预览
                                gotoLive(curCamer);
                            } else if (palyType == 2) {//回放
                                gotoPlayBack(curCamer);
                            }

                            if (null != progressBar) {
                                progressBar.setVisibility(View.GONE);
                            }
                            if (null != add_video) {
                                add_video.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);
    }


    /**
     * @author lvlingdi 2016-5-6 上午10:31:05
     */
    private void zoomBtnOnClick() {
        mLiveControl = tmLiveControl;
        if (isZoom && null != curSurfaceView) {
            curSurfaceView.setOnZoomListener(new OnZoomListener() {

                @Override
                public void onZoomChange(CustomRect original, CustomRect current) {
                    PCRect o = new PCRect(original.getLeft(), original.getTop(), original.getRight(), original.getBottom());
                    PCRect c = new PCRect(current.getLeft(), current.getTop(), current.getRight(), current.getBottom());
                    if (null != mLiveControl) {
                        mLiveControl.setDisplayRegion(true, o, c);
                    }
                }
            });
        } else {
            if (null != curSurfaceView) {
                curSurfaceView.setOnZoomListener(null);
                if (null != mLiveControl) {
                    mLiveControl.setDisplayRegion(false, null, null);
                }
            } else {
                isZoom = false;
                fangda_img.setBackgroundResource(R.drawable.fangda);
                UIUtil.showToast(getActivity(), "未开始播放");
            }
        }

    }

    //初始化控制层
    private void initControl() {
        for (int i = 0; i < videList.size(); i++) {
            if (null != videList.get(i).getmLiveControl()) {
                LiveControl liveControl = videList.get(i).getmLiveControl();
                liveControl.stop();
            }
            if (null != videList.get(i).getmPlayBackControl()) {
                PlayBackControl playBackControl = videList.get(i).getmPlayBackControl();
                playBackControl.stopPlayBack();
            }
        }
        if (null != mLiveControl) {
            mLiveControl.stop();
        }
        if (null != tmPlayBackControl) {
            tmPlayBackControl.stopPlayBack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case RECULET_CODE://监控列表回调
                if (resultCode == RESULT_OK) {
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (null != add_video) {
                        add_video.setVisibility(View.GONE);
                    }
                    mPlayBackControl = tmPlayBackControl;
                    mLiveControl = tmLiveControl;
                    Bundle extras = data.getExtras();
                    Camera camera = (Camera) extras.get("camera");
                    curCamer = camera;
                    switch (palyType) {
                        case 1:
                            //  预览
//                            if (MyVMSNetSDK.getInstance().isHasLivePermission(camera)) {
//
                            gotoLive(camera);
//                            } else {
//                                UIUtil.showToast(getActivity(), R.string.no_permission);
//                            }
                            break;
                        case 2:
                            // 回放
//                            if (MyVMSNetSDK.getInstance().isHasPlayBackPermission(camera)) {
                            gotoPlayBack(camera);
//                            } else {
//                                UIUtil.showToast(getActivity(), R.string.no_permission);
//                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
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

                    break;

                case ConstantPlayBack.PAUSE_FAIL:
                    UIUtil.showToast(getActivity(), "暂停失败");

                    break;

                case ConstantPlayBack.RESUEM_FAIL:
                    UIUtil.showToast(getActivity(), "恢复播放失败");

                    break;

                case ConstantPlayBack.RESUEM_SUCCESS:
                    UIUtil.showToast(getActivity(), "恢复播放成功");

                    break;

                case ConstantPlayBack.START_OPEN_FAILED:
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
     *
     * @author lvlingdi 2016-4-19 下午5:01:22
     */
    private void startBtnOnClick() {
        if (null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (null != mPlayBackControl) {
            mPlayBackControl.startPlayBack(mParamsObj);
        }
    }

    /**
     * 停止播放
     *
     * @author lvlingdi 2016-4-19 下午5:11:56
     */
    private void stopBtnOnClick() {
        if (null != progressBar) {
            progressBar.setVisibility(View.GONE);
        }
        if (null != mPlayBackControl) {
            mPlayBackControl.stopPlayBack();
        }
        stopUpdateTimer();
    }

    /**
     * 设置回放参数
     *
     * @author lvlingdi 2016-4-21 下午4:41:19
     */
    private void setParamsObj() {
        if (null != deviceInfo) {
            mParamsObj.name = deviceInfo.getUserName() == null ? "" : deviceInfo.getUserName();
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
     * @param
     * @author lvlingdi 2016-4-27 下午3:39:33
     */
    private void handlePlayProgress(long osd) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(osd);
        long begin = mStartTime.getTimeInMillis();
        long end = mEndTime.getTimeInMillis();

        double x = ((osd - begin) * PROGRESS_MAX_VALUE) / (double) (end - begin);
        int progress = (int) x;
//        if(null!=mProgressSeekbar){
//            mProgressSeekbar.setProgress(progress);
//        }

//        int beginTimeClock = (int) ((osd - begin) / 1000);
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
     *
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

    }

    /**
     * 查找录像片段
     *
     * @author lvlingdi 2016-4-21 下午3:30:18
     */
    private void queryRecordSegment() {
        if (null == cameraInfo) {
            Log.e("ivms8700", "queryRecordSegment==>>cameraInfo is null");
            return;
        }
        if (null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
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
                    mRecordInfo = ((RecordInfo) obj);

                    //级联设备的时候
                    if (null != mRecordInfo.getSegmentList() && 0 < mRecordInfo.getSegmentList().size()) {
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
     *
     * @param cameraInfo
     * @author lvlingdi 2016-4-21 上午10:07:33
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
     *
     * @param cameraInfo
     * @author lvlingdi 2016-4-21 上午10:09:12
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
                if (palyType == 1) {
                    liveHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_failure);
                } else {
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.getDeviceInfo_failure);
                }
            }

            @Override
            public void loading() {
                if (palyType == 1) {
                    liveHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
                } else {
                    mMessageHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof DeviceInfo) {
                    deviceInfo = (DeviceInfo) data;
                    if (palyType == 1) {
                        liveHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
                    } else {
                        mMessageHandler.sendEmptyMessage(Constants.PlayBack.getDeviceInfo_Success);
                    }

                }
            }
        });

        boolean flag = mVMSNetSDK.getDeviceInfo(cameraInfo.getDeviceID());
    }


    /**
     * 回放监控点
     *
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
     *
     * @author lvlingdi 2016-4-19 下午5:20:50
     */
    private void initData(Camera camera) {
        mMessageHandler = new HuifangHandler();
        mVMSNetSDK = MyVMSNetSDK.getInstance();
        // 初始化远程回放控制层对象
//        mPlayBackControl = new PlayBackControl();

        // 创建远程回放需要的参数
        mParamsObj = new PlayBackParams();
        // 播放控件
        mParamsObj.surfaceView = curSurfaceView;
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
        mVMSNetSDK = MyVMSNetSDK.getInstance();
        liveHandler = new LiveViewHandler();

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
        if (palyType == 1) {
            liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
        } else {
            mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo);
        }

        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                if (palyType == 1) {
                    liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo_failure);
                } else {
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo_failure);
                }
            }

            @Override
            public void loading() {
                if (palyType == 1) {
                    liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
                } else {
                    mMessageHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof CameraInfo) {
                    cameraInfo = (CameraInfo) data;
                    cameraMap.put(mCurIndex, cameraInfo);
                    if (palyType == 1) {
                        liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo_Success);
                    } else {
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

        if (null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
        String liveUrl = VMSNetSDK.getInstance().getPlayUrl(cameraInfo, mStreamType);
        mLiveControl.setLiveParams(liveUrl, null == username ? "" : username, null == password ? "" : password);
        if (LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
            mLiveControl.stop();
        }

        if (LiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
            if (null != progressBar) {
                progressBar.setVisibility(View.GONE);
            }
            mLiveControl.startLive(curSurfaceView);
        }
    }


    //云台控制方法
    private void ptzBtnOnClick() {
        if (null != mLiveControl && LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
//            if (mIsPtzStart) {
//                MyVMSNetSDK.getInstance().sendPTZCtrlCmd(cameraInfo, PTZCmd.ACTION_STOP, mPtzcommand);
//                mIsPtzStart = false;
//            } else {
            cameraInfo = (CameraInfo) cameraMap.get(mCurIndex);
            MyVMSNetSDK.getInstance().sendPTZCtrlCmd(cameraInfo, PTZCmd.ACTION_START, mPtzcommand);
            mIsPtzStart = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //执行的方法
                    MyVMSNetSDK.getInstance().sendPTZCtrlCmd(cameraInfo, PTZCmd.ACTION_STOP, mPtzcommand);
//                mIsPtzStart = false;
                }
            }, 1000);//3秒后执行Runnable中的run方法


//            }
        }
    }

    //显示控制方向弹窗
    public void showList() {
        final String[] items = {"上", "下", "左", "右"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        alertBuilder.setTitle("请选择控制方向");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        mPtzcommand = PTZCmd.CUSTOM_CMD_UP;
                        break;
                    case 1:
                        mPtzcommand = PTZCmd.CUSTOM_CMD_DOWN;
                        break;
                    case 2:
                        mPtzcommand = PTZCmd.CUSTOM_CMD_LEFT;
                        break;
                    case 3:
                        mPtzcommand = PTZCmd.CUSTOM_CMD_RIGHT;
                        break;
                }
                ptzBtnOnClick();
                alertDialog.dismiss();
            }
        });
        alertDialog = alertBuilder.create();
        alertDialog.show();
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
                Log.e("ivms8700", "clickCaptureBtn():: 抓拍失败");
            }
        }
    }

    /**
     * 录像 void
     *
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
                playBackRecord_img.setBackgroundResource(R.drawable.lupin);
                UIUtil.showToast(getActivity(), "启动录像成功");
//                mRecordBtn.setText("停止录像");
            } else {
                mLiveControl.stopRecord();
                mIsRecord = false;
                playBackRecord_img.setBackgroundResource(R.drawable.luxiang);
                UIUtil.showToast(getActivity(), "停止录像成功");
//                mRecordBtn.setText("开始录像");
            }
        }
    }

    /**
     * 抓拍
     *
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
     *
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

    //跳转到选择监控点界面
    private void intentAddM() {

        Intent intent = new Intent(getActivity(), AddCamerActivity.class);
        startActivityForResult(intent, RECULET_CODE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        initControl();
    }

    @Override
    public void onMessageCallback(int message) {
        sendMessageCase(message);
    }

}

