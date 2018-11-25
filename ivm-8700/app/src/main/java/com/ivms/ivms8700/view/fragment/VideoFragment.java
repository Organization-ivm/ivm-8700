package com.ivms.ivms8700.view.fragment;


import android.app.Dialog;
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
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.live.LiveControl;
import com.ivms.ivms8700.multilevellist.TreeAdapter;
import com.ivms.ivms8700.multilevellist.TreePoint;
import com.ivms.ivms8700.multilevellist.TreeUtils;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.view.customui.CustomSurfaceView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class VideoFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SurfaceHolder.Callback, LiveControl.LiveCallBack{
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

    TreeAdapter adapter;
    /**
     * 控制层对象
     */
    private LiveControl mLiveControl = null;
    private Handler mHandler = null;
    private Handler liveHandler = null;

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
                liveHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_failure);

            }

            @Override
            public void loading() {
                liveHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);

            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof DeviceInfo) {
                    deviceInfo = (DeviceInfo) data;
                    liveHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
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

//                                    switch (which) {
//                                        case 0:
                                            // 预览
                                            if (VMSNetSDK.getInstance().isHasLivePermission(camera)) {
                                                gotoLive(camera);
                                            } else {
                                                UIUtil.showToast(getActivity(), R.string.no_permission);
                                            }
//                                            break;
//                                        case 1:
//                                            // 回放
//                                            if (VMSNetSDK.getInstance().isHasPlayBackPermission(camera)) {
//                                                gotoPlayBack(camera);
//                                            } else {
//                                                UIUtil.showToast(ResourceListActivity.this, R.string.no_permission);
//                                            }
//                                            break;
//                                        default:
//                                            break;
//                                    }

                    }else{

                        getSubResourceList(showList.get(position).getSubResourceNodeBean().getNodeType(), showList.get(position).getSubResourceNodeBean().getId()
                                , position, showList.get(position));
                    }

                }


            }
        });
    }
   //进入预览
    private void gotoLive(Camera camera) {
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
        liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo_failure);

            }

            @Override
            public void loading() {
                liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo);

            }
            @Override
            public void onSuccess(Object data) {
                if (data instanceof CameraInfo) {
                    cameraInfo = (CameraInfo) data;
                    liveHandler.sendEmptyMessage(Constants.Live.getCameraInfo_Success);
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
