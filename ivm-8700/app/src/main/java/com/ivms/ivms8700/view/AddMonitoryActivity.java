package com.ivms.ivms8700.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.Camera;
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
import com.ivms.ivms8700.view.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AddMonitoryActivity extends Activity implements View.OnClickListener {
    private ListView video_listView;
    TreeAdapter adapter;
    private Handler mHandler = null;
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
    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_monitory);
        initView();
        // 初次加载根节点数据
        getRootControlCenter();
    }

    private void initView() {
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        title_txt=(TextView)findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.select_monitoringpoint));
        save_btn=(TextView)findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        //监控点列表
        video_listView = (ListView) findViewById(R.id.video_listView);
        adapter = new TreeAdapter(this, genList, pointMap);
        video_listView.setAdapter(adapter);
        setListener();
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
                    nodeType = ((SubResourceNodeBean) node).getNodeType();
                    Log.i("ivms8700", "nodeType=-=" + nodeType);

                    if (HttpConstants.NodeType.TYPE_CAMERA_OR_DOOR == nodeType) {
                        Log.i("ivms8700", "=-=准备进入播放");
                        // 构造camera对象
                        final Camera camera = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean) node);
                        // 设置返回数据
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent();
                        // 把Persion数据放入到bundle中
                        bundle.putSerializable("camera",camera);
                        intent.putExtras(bundle);
                        // 返回intent
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {

                        getSubResourceList(showList.get(position).getSubResourceNodeBean().getNodeType(), showList.get(position).getSubResourceNodeBean().getId()
                                , position, showList.get(position));
                    }

                }


            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn :
                finish();
                break;

        }
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
     * 加载进度条
     */
    private void showLoadingProgress() {
        UIUtil.showProgressDialog(this, R.string.loading_process_tip);
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
        UIUtil.showToast(this, R.string.loading_failed);
    }

    /**
     * 加载成功
     *
     * @author hanshuangwu 2016年1月21日 下午3:25:59
     */
    private void onloadingSuccess() {
//        updateData();
        UIUtil.showToast(this, R.string.loading_success);
        adapter.notifyDataSetChanged();
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
