package com.ivms.ivms8700.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.DiscernEntity;
import com.ivms.ivms8700.bean.MessageEntity;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.NoDoubleClickListener;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.adapter.DiscernAdapter;
import com.ivms.ivms8700.view.adapter.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MessageFragment extends Fragment implements OkHttpClientManager.JsonObjectCallback {

    @BindView(R.id.rltitle)
    RelativeLayout rltitle;
    @BindView(R.id.live_view)
    View liveView;
    @BindView(R.id.llSafetyHat)
    LinearLayout llSafetyHat;
    @BindView(R.id.local_view)
    View localView;
    @BindView(R.id.llFace)
    LinearLayout llFace;
    @BindView(R.id.titlechoose)
    LinearLayout titlechoose;
    @BindView(R.id.RvMessage)
    RecyclerView RvMessage;
   @BindView(R.id.rlContent)
    RelativeLayout rlContent;
    Unbinder unbinder;
    private View view;
    private LocalDbUtil localDbUtil;
    private String local_url;
    private String userName;
    private MessageAdapter adapter;
    private List<String> list = new ArrayList<>();
    private String deviceId;
    private List<MessageEntity.Msg> msgList;
    /**
     * 0,安全帽，
     * 1，人脸
     */
    private int safeOrFace = 0;
    private String testStr = "{\"data\":{\"list\":[{\"msg\":\"{'type':'faceRecognize','stationCode':'310000L14S13','recognizeTime':'2018-12-13 16:37:42'}\"},{\"msg\":\"{'type':'safeCapRecognize','stationCode':'310000L14S13','recognizeTime':'2018-11-07 11:22:42'}\"}]},\"msg\":\"获取成功!\",\"result\":\"success\"}";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.message_layout, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        initView();
        initDate();
        setListener();


        return view;
    }

    private void setListener() {
        llSafetyHat.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                initDate();
                liveView.setVisibility(View.VISIBLE);
                localView.setVisibility(View.GONE);

                safeOrFace=  0;

            }
        });
        llFace.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                liveView.setVisibility(View.GONE);
                localView.setVisibility(View.VISIBLE);
                safeOrFace = 1;

                initDate();

            }
        });

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initDate();
        }

    }

    private void initDate() {
        UIUtil.showProgressDialog(getActivity(), R.string.loading_process_tip);
        String url = "";
        url += local_url + "/shm/msgPush?";
        url += "&token=" + Constants.APP_TOKEN;
        url += "&deviceID=" + deviceId;
        url += "&userName=" + userName;

        Log.i("Alan", "消息url=-=" + url);

        OkHttpClientManager.getInstance().asyncJsonObjectByUrl(url, this);
    }

    private void initView() {
        localDbUtil = new LocalDbUtil(getContext());
        local_url = localDbUtil.getString("local_url");
        deviceId = localDbUtil.getString("local_deviceId");
        userName = localDbUtil.getString("userName");
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getContext());
        //设置RecyclerView 布局
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        RvMessage.setLayoutManager(layoutmanager);
        //设置Adapter
        adapter = new MessageAdapter(getContext());
        RvMessage.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        UIUtil.cancelProgressDialog();
        try {
//            JSONObject jsonObject1 = new JSONObject(testStr);
            String result = jsonObject.getString("result");
//            String result = jsonObject1.getString("result");
            list.clear();
            if (result.equals("success")) {
                rlContent.setVisibility(View.VISIBLE);
                String str = jsonObject.toString();
                Gson gson = new Gson();
                MessageEntity enty = gson.fromJson(str, MessageEntity.class);
                msgList = new ArrayList<>();

                for (int i = 0; i < enty.getData().getList().size(); i++) {
                    JSONObject json = new JSONObject(enty.getData().getList().get(i).getMsg());

                    MessageEntity.Msg msg = new MessageEntity.Msg();
                    msg.setType(json.getString("type"));
                    msg.setStationCode(json.getString("stationCode"));
                    msg.setRecognizeTime(json.getString("recognizeTime"));
                    msgList.add(msg);
                }
                adapter.setData(msgList);
//                JSONObject data = jsonObject.getJSONObject("data");
//                JSONArray list = data.getJSONArray("list");
//                for (int i = 0; i < list.length(); i++) {
//                    JSONObject obj = list.getJSONObject(i);
//                    JSONObject object = obj.getJSONObject("msg");
//
//
//
//
//
//
//                    String lineCode = obj.getString("msg");
//                    String lineName = obj.getString("lineName");
//                    JSONArray stations = obj.getJSONArray("stations");
//                    for (int j = 0; j < stations.length(); j++) {
//                        JSONObject obj1 = stations.getJSONObject(j);
//                        String stationCode = obj1.getString("stationCode");
//                        String stationName = obj1.getString("stationName");
//                        JSONArray photos = obj1.getJSONArray("photos");
//                        for (int k = 0; k < photos.length(); k++) {
//                            JSONObject obj2 = photos.getJSONObject(k);
//                            String captureTime = obj2.getString("captureTime");
//                            String safeCapCapture = obj2.getString("safeCapCapture");
//
//                            DiscernEntity discernEntity = new DiscernEntity();
//                            discernEntity.setLineCode(lineCode);
//                            discernEntity.setLineName(lineName);
//                            discernEntity.setStationCode(stationCode);
//                            discernEntity.setStationName(stationName);
//                            discernEntity.setCaptureTime(captureTime);
//                            discernEntity.setSafeCapCapture(safeCapCapture);
//                            mDiscernList.add(discernEntity);
//                        }
//                    }
//                }
                adapter.notifyDataSetChanged();
            } else {
                UIUtil.showToast(getActivity(), jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
