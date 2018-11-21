package com.ivms.ivms8700.model;

import android.os.Handler;
import android.os.Message;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.ivms.ivms8700.control.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VideoModel {
    /**
     * 发送消息的对象
     */
    private Handler mHandler = new ViewHandler();
    /**
     * 资源源数据
     */
    private static ArrayList<Object> source = new ArrayList<Object>();
    private static ArrayList<Object> source_tier = new ArrayList<Object>();

    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> data_tier = new ArrayList<String>();

//    List<ClassA> list = new ArrayList<>();

    private static int source_index=0;
    public final class ViewHandler extends Handler {

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

                    break;
                case Constants.Resource.CANCEL_LOADING_PROGRESS:

                    break;
                case Constants.Resource.LOADING_SUCCESS://加载根节点成功
                    //遍历根节点
                    getTierData();
                    break;
                case Constants.Resource.LOADING_SUCCESS_TIER://加载子节点成功
                    //将刚获取的子节点数据加到list里
                    addListToSource();
                    break;

                case Constants.Resource.LOADING_FAILED:

                    break;
            }
        }




    }
    /**
     * 获取根控制中心
     */
    public void getVideoData() {

        boolean flag = false;
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
             */
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof RootCtrlCenter) {
                    data.clear();
                    source.clear();
                    source.add((RootCtrlCenter)obj);
                    data.add(((RootCtrlCenter)obj).getName());
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
                    data_tier.clear();
                    source_tier.clear();
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
    //遍历根节点数据依次获取二级节点
    public void getTierData() {
          if(0<=source_index&&source_index<source.size()-1){
               // 请求此item的下级资源
              Object obj = source.get(source_index);
              int parentNodeType = 0;
              int pId = 0;
              if (obj instanceof RootCtrlCenter) {
                  parentNodeType = Integer.parseInt(((RootCtrlCenter)obj).getNodeType());
                  pId = ((RootCtrlCenter)obj).getId();
              } else
              if (obj instanceof SubResourceNodeBean) {
                  parentNodeType = ((SubResourceNodeBean)obj).getNodeType();
                  pId = ((SubResourceNodeBean)obj).getId();
              }
              getSubResourceList(parentNodeType,pId);
          }
    }

    //将刚获取的子节点数据加到list里
    private void addListToSource() {

//        for (int i = 0; i < data.size(); i++) {
//            ClassA a = new ClassA(i, " A" + i);
//            a.setChildren(new ArrayList());
//            list.add(a);
//        }

    }

}
