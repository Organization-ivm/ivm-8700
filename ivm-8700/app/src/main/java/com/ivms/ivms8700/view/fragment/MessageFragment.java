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

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.MessageEntity;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.NoDoubleClickListener;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
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
    private String deviceId;
    private List<MessageEntity.Msg> msgList = new ArrayList<>();
    /**
     * 0,安全帽，
     * 1，人脸
     */
    private int safeOrFace = 0;

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

                liveView.setVisibility(View.VISIBLE);
                localView.setVisibility(View.GONE);
                safeOrFace = 0;
                initDate();
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
        url += "&deviceID=" + deviceId ;
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
        adapter.setData(msgList);
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
        if(null!= MyApplication.getIns().getMsgJSONObject()){
            jsonObject=MyApplication.getIns().getMsgJSONObject();
        }
        try {
            String result = jsonObject.getString("result");
            msgList.clear();
            if (result.equals("success")) {
                rlContent.setVisibility(View.VISIBLE);
//            String str = jsonObject.toString();
//            String str="{\n" +
//                    "    \"data\": {\n" +
//                    "        \"list\": [\n" +
//                    "            {\n" +
//                    "                \"type\": \"safeCapRecognize\",\n" +
//                    "                \"stationCode\": \"310000L15S01\",\n" +
//                    "                \"stationName\": \" 顾 村 公 园 站 \",\n" +
//                    "                \"recognizeTime\": \"2019-01-08 09:51:24\"\n" +
//                    "            },\n" +
//                    "            {\n" +
//                    "                \"type\": \"faceRecognize\",\n" +
//                    "                \"stationCode\": \"310000L14S13\",\n" +
//                    "                \"stationName\": \"豫园站\",\n" +
//                    "                \"recognizeTime\": \"2019-01-08 10: 33: 24\"\n" +
//                    "            }\n" +
//                    "        ]\n" +
//                    "    },\n" +
//                    "    \"msg\": \"获取成功\",\n" +
//                    "    \"result\": \"success\"\n" +
//                    "}";
//            JSONObject s=new JSONObject(str);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray list = data.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject obj = list.getJSONObject(i);
                    MessageEntity.Msg msg = new MessageEntity.Msg();
                    msg.setType(obj.getString("type"));
                    msg.setStationCode(obj.getString("stationCode"));
                    msg.setRecognizeTime(obj.getString("recognizeTime"));
                    msg.setStationName(obj.getString("stationName"));

                    if (safeOrFace == 0 && "safeCapRecognize".equals(obj.getString("type"))) {
                        msgList.add(msg);
                    }
                    if (safeOrFace == 1 && "faceRecognize".equals(obj.getString("type"))) {
                        msgList.add(msg);
                    }

                }
            } else {
                UIUtil.showToast(getActivity(), jsonObject.getString("msg"));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
