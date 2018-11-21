package com.ivms.ivms8700.view.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.entity.ClassA;
import com.ivms.ivms8700.entity.ClassB;
import com.ivms.ivms8700.entity.ClassC;
import com.ivms.ivms8700.entity.ClassD;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.view.adapter.MonitoryAdapter;
import com.lijianxun.multilevellist.adapter.MultiLevelAdapter;
import com.lijianxun.multilevellist.model.MultiLevelModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VideoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private LinearLayout ll_jiankong;
    private RelativeLayout rl_select_btn;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private ListView video_listView;
    List<ClassA> list = new ArrayList<>();
    MonitoryAdapter adapter;

    /**
     * listitem显示数据
     */
    private ArrayList<String> data_all = new ArrayList<String>();
    private ArrayList<String> data_tier = new ArrayList<String>();
    /**
     * 资源源数据
     */
    private ArrayList<Object> source_tier = new ArrayList<Object>();
    /**
     * 资源源数据
     */
    private ArrayList<Object> source = new ArrayList<Object>();



    private Dialog dialog = null;
    private Handler mHandler = null;
    private int currentLevel=0;
    private int _position=0;


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
        if(view==null){
            view=inflater.inflate(R.layout.video_layout,container,false);
            ll_jiankong=(LinearLayout)view.findViewById(R.id.ll_jiankong);
            rl_select_btn=(RelativeLayout)view.findViewById(R.id.rl_select_btn);
            rl_select_btn.setOnClickListener(this);
            //获取所有层级监控点列表

            //监控点列表
            video_listView=(ListView)view.findViewById(R.id.video_listView);
            adapter = new MonitoryAdapter(getActivity(), true, true
                    , 1);
            adapter.setOnMultiLevelListener(new MultiLevelAdapter.OnMultiLevelListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id
                        , MultiLevelModel current, MultiLevelModel max) {
                    Toast.makeText(getActivity(), "position = " + position + "" +
                                    " , current level = " + current.getLevel() + " , outside level = "
                                    + max.getLevel()
                            , Toast.LENGTH_SHORT).show();
                }
            });
            video_listView.setOnItemClickListener(adapter);
            video_listView.setAdapter(adapter);
        }
        // 初次加载根节点数据
        getRootControlCenter();
        setAnimation();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_select_btn:
                if(ll_jiankong.getVisibility()==View.GONE){
                    ll_jiankong.startAnimation(mShowAction);
                    ll_jiankong.setVisibility(View.VISIBLE);
                }else{
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
                    source.clear();
                    data_all.clear();
                    source.add((RootCtrlCenter)obj);
                    data_all.add(((RootCtrlCenter)obj).getName());
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
     * @param pId 父节点ID
     *            level   当前层级
     */
    private void getSubResourceList(int parentNodeType, int pId) {
        boolean flag = false;

        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
             */
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof List<?>) {
                    List<SubResourceNodeBean> list = new  ArrayList<SubResourceNodeBean>();
                    list.addAll((Collection<? extends SubResourceNodeBean>) obj);


                    if (null != list) {
                        for (SubResourceNodeBean bean : list) {
                                data_tier.add(bean.getName());
                                source_tier.add(bean);
                        }
                        mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS_TIER);
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

        flag = VMSNetSDK.getInstance().getSubResourceList( 1,
                15, HttpConstants.SysType.TYPE_VIDEO, parentNodeType, pId + "");
    }



   private void updateData(){
        list.clear();
        int count = 0;
        for (int i = 0; i < data_all.size(); i++) {
            ClassA a = new ClassA(i, data_all.get(i));
            a.setChildren(new ArrayList());
            list.add(a);
            count += 1;
            Log.e("TAG 1", a.getName());
        }
        Log.e("count", "= " + count);
       adapter.setList(list);
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
        updateData();
        UIUtil.showToast(getActivity(), R.string.loading_success);
        adapter.notifyDataSetChanged();
    }

    private  void setAnimation(){
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
}
