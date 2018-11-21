package com.ivms.ivms8700.view.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.multilevellist.TreeAdapter;
import com.ivms.ivms8700.multilevellist.TreePoint;
import com.ivms.ivms8700.multilevellist.TreeUtils;
import com.ivms.ivms8700.utils.UIUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class VideoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private LinearLayout ll_jiankong;
    private RelativeLayout rl_select_btn;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private ListView video_listView;
    //    List<ClassA> list = new ArrayList<>();
    TreeAdapter adapter;

//    /**
//     * listitem显示数据
//     */
//    private ArrayList<String> data_all = new ArrayList<String>();
//    private ArrayList<String> data_tier = new ArrayList<String>();
//    /**
//     * 资源源数据
//     */
//    private ArrayList<Object> source_tier = new ArrayList<Object>();
//    /**
//     * 资源源数据
//     */
//    private ArrayList<Object> source = new ArrayList<Object>();

    /**
     * 跟节点数据
     */
    private List<TreePoint> genList = new ArrayList<>();
    private HashMap<String, TreePoint> pointMap = new HashMap<>();
    private List<TreePoint> showList = new ArrayList<>();


    private Dialog dialog = null;
    private Handler mHandler = null;


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
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.video_layout, container, false);
            ll_jiankong = (LinearLayout) view.findViewById(R.id.ll_jiankong);
            rl_select_btn = (RelativeLayout) view.findViewById(R.id.rl_select_btn);
            rl_select_btn.setOnClickListener(this);
            //获取所有层级监控点列表

            //监控点列表
            video_listView = (ListView) view.findViewById(R.id.video_listView);
            adapter = new TreeAdapter(getContext(), genList,pointMap);
            video_listView.setAdapter(adapter);
            setListener();
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
                    ,position,showList.get(position));
                } else {
                    getSubResourceList(showList.get(position).getSubResourceNodeBean().getNodeType(), showList.get(position).getRootCtrlCenter().getId()
                            ,position,showList.get(position));
                }


            }
        });
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
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
             */
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof RootCtrlCenter) {
                    genList.clear();
                    showList.clear();
                    TreePoint t = new TreePoint(((RootCtrlCenter) obj).getId(), ((RootCtrlCenter) obj).getName(), 0, 0, false, 0, true, null, (RootCtrlCenter) obj);
                    pointMap.put(((RootCtrlCenter) obj).getId() + "", t);
                    genList.add(t);
                    showList.add(t);
//                    source.clear();
//                    data_all.clear();
//                    source.add((RootCtrlCenter) obj);
//                    data_all.add(((RootCtrlCenter) obj).getName());
                    mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS);
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


                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            subList.add(new TreePoint(((SubResourceNodeBean) obj).getId(), ((SubResourceNodeBean) obj).getName(),
                                    treePoint.getID(), i, false, 0, true, (SubResourceNodeBean) obj, null));
                        }
                        if (!treePoint.isExpand()) {//点击项有子数据但未展开，展开操作。
                            for (int i = 0; i < subList.size(); i++) {
                                pointMap.put(subList.get(i).getID() + "", subList.get(i));
                                genList.add(new TreePoint(subList.get(i).getID(), getSubmitResult(subList.get(i)), treePoint.getID(), i, false, getLayer(treePoint) + 1, true));
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


//                        for (SubResourceNodeBean bean : list) {
////                            data_tier.add(bean.getName());
////                            source_tier.add(bean);
//                        }
                        mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS_TIER);
                    } else {
                        showList.get(position).setHasSubDatas(false);
                        return;
                    }
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


//    private void updateData() {
//        list.clear();
//        int count = 0;
//        for (int i = 0; i < data_all.size(); i++) {
//            ClassA a = new ClassA(i, data_all.get(i));
//            a.setChildren(new ArrayList());
//            list.add(a);
//            count += 1;
//            Log.e("TAG 1", a.getName());
//        }
//        Log.e("count", "= " + count);
//        adapter.setList(list);
//    }


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


    private int getLayer(TreePoint treePoint) {
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
