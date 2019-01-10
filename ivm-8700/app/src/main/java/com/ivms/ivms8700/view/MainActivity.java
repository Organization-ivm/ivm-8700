package com.ivms.ivms8700.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.EventEntity;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.service.MsgService;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.fragment.ImageManagementFragment;
import com.ivms.ivms8700.view.fragment.MessageFragment;
import com.ivms.ivms8700.view.fragment.MyFragment;
import com.ivms.ivms8700.view.fragment.VideoFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OkHttpClientManager.JsonStringCallback {
    // 底部菜单4个Linearlayout
      private LinearLayout ll_video;
      private LinearLayout ll_image_management;
      private LinearLayout ll_message;
      private LinearLayout ll_my;

    // 4个Fragment
      private VideoFragment videoFragment;
      private ImageManagementFragment imFragment;
      private MessageFragment messageFragment;
      private MyFragment myFragment;
    private ImageView image_management_img;
    private TextView image_management_txt;
    private ImageView message_img;
    private TextView my_txt;
    private TextView message_txt;
    private TextView msg_num;
    private ImageView my_img;
    private ImageView video_img;
    private TextView video_txt;
    private int select = 0;
    private LocalDbUtil localDbUtil;
    private String local_url;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        initFragment(0);
        Intent ServiceIntent = new Intent(MainActivity.this, MsgService.class);
        startService(ServiceIntent);
    }

    private void initView() {
        localDbUtil=new LocalDbUtil(this);
        local_url=localDbUtil.getString("local_url");
        userName=localDbUtil.getString("userName");
        ll_video=(LinearLayout)findViewById(R.id.video_lay);
        ll_image_management=(LinearLayout)findViewById(R.id.image_management_lay);
        ll_message=(LinearLayout)findViewById(R.id.message_lay);
        ll_my=(LinearLayout)findViewById(R.id.my_lay);

        ll_video.setOnClickListener(this);
        ll_image_management.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_my.setOnClickListener(this);

        video_img=(ImageView)findViewById(R.id.video_img);
        video_txt=(TextView)findViewById(R.id.video_txt);
        image_management_img =(ImageView)findViewById(R.id.image_management_img);
        image_management_txt=(TextView)findViewById(R.id.image_management_txt);
        message_img =(ImageView)findViewById(R.id.message_img);
        msg_num=(TextView) findViewById(R.id.msg_num);
        message_txt=(TextView)findViewById(R.id.message_txt);
        my_img =(ImageView)findViewById(R.id.my_img);
        my_txt=(TextView)findViewById(R.id.my_txt);


    }
  @Subscribe(threadMode = ThreadMode.MAIN)
   public void getEvent(EventEntity eventEntity){
        int msgType=eventEntity.getType();
        if(msgType==Constants.Event.getMsg){//有新消息
            try {
                JSONObject msgObj=eventEntity.getJsonObject();
                JSONObject data = null;
                data = msgObj.getJSONObject("data");
                JSONArray list = data.getJSONArray("list");
                if(list.length()>0){
                    msg_num.setVisibility(View.VISIBLE);
                    msg_num.setText(list.length()+"");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUtil.cancelProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_lay:
                select = 0;

                updateUI(0);
                initFragment(0);
                break;
            case R.id.image_management_lay:
                select = 1;
                updateUI(1);
                initFragment(1);
                break;
            case R.id.message_lay:
                msg_num.setVisibility(View.GONE);
                select = 2;
                updateUI(2);
                initFragment(2);
                break;
            case R.id.my_lay:
                select = 3;
                updateUI(3);
                initFragment(3);
                break;
        }
    }



    private void initFragment(int index) {
                 // 由于是引用了V4包下的Fragment，所以这里的管理器要用getSupportFragmentManager获取
                 FragmentManager fragmentManager = getSupportFragmentManager();
                 // 开启事务
                 FragmentTransaction transaction = fragmentManager.beginTransaction();
                // 隐藏所有Fragment
                 hideFragment(transaction);
                switch (index) {
                     case 0:
                             if (videoFragment == null) {
                                 videoFragment = new VideoFragment();
                                    transaction.add(R.id.frag_cont, videoFragment);
                                 } else {
                                    transaction.show(videoFragment);
                                }
                             break;
                     case 1:
                             if ( imFragment== null) {
                                 imFragment = new ImageManagementFragment();
                                     transaction.add(R.id.frag_cont, imFragment);
                                 } else {
                                     transaction.show(imFragment);
                                 }

                             break;
                     case 2:
                             if (messageFragment == null) {
                                 messageFragment = new MessageFragment();
                                     transaction.add(R.id.frag_cont, messageFragment);
                                } else {
                                     transaction.show(messageFragment);
                                 }

                            break;
                     case 3:
                             if (myFragment == null) {
                                 myFragment = new MyFragment();
                                    transaction.add(R.id.frag_cont, myFragment);
                                } else {
                                     transaction.show(myFragment);
                                }

                             break;

                     default:
                             break;
                     }

                // 提交事务
                 transaction.commit();

             }

             //隐藏Fragment
             private void hideFragment(FragmentTransaction transaction) {
                 if (videoFragment != null) {
                         transaction.hide(videoFragment);
                     }
                 if (imFragment != null) {
                         transaction.hide(imFragment);
                     }
                 if (messageFragment != null) {
                         transaction.hide(messageFragment);
                     }
                 if (myFragment != null) {
                         transaction.hide(myFragment);
                     }

             }

    //更新底部按钮状态
    @SuppressLint("ResourceAsColor")
    private void updateUI(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        if(i==0){
            video_img.setBackgroundResource(R.drawable.shipin_1);
            video_txt.setTextColor((getResources().getColor(R.color.text_select_color)));
            image_management_img.setBackgroundResource(R.drawable.tuxiangguanli);
            image_management_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            message_img.setBackgroundResource(R.drawable.xiaoxi_1);
            message_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            my_img.setBackgroundResource(R.drawable.wode);
            my_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
        }
        if(i==1){
            video_img.setBackgroundResource(R.drawable.shi);
            video_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            image_management_img.setBackgroundResource(R.drawable.tuxiangguanli_1);
            image_management_txt.setTextColor((getResources().getColor(R.color.text_select_color)));
            message_img.setBackgroundResource(R.drawable.xiaoxi_1);
            message_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            my_img.setBackgroundResource(R.drawable.wode);
            my_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
        }
        if(i==2){
            video_img.setBackgroundResource(R.drawable.shi);
            video_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            image_management_img.setBackgroundResource(R.drawable.tuxiangguanli);
            image_management_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            message_img.setBackgroundResource(R.drawable.xiaoxi);
            message_txt.setTextColor((getResources().getColor(R.color.text_select_color)));
            my_img.setBackgroundResource(R.drawable.wode);
            my_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
        }
        if(i==3){
            video_img.setBackgroundResource(R.drawable.shi);
            video_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            image_management_img.setBackgroundResource(R.drawable.tuxiangguanli);
            image_management_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            message_img.setBackgroundResource(R.drawable.xiaoxi_1);
            message_txt.setTextColor((getResources().getColor(R.color.text_noselect_color)));
            my_img.setBackgroundResource(R.drawable.wode_1);
            my_txt.setTextColor((getResources().getColor(R.color.text_select_color)));
        }
            }
        });
    }

    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (select == 1) {
//            imFragment.onKeyDown(keyCode, event);
//        }
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                OkHttpClientManager.getInstance().asyncJsonStringByURL(local_url+"/shm/loginout?userName="+userName+"&token="+ Constants.APP_TOKEN, this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResponse(String result) {
        exitAPP();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void exitAPP() {
        ActivityManager activityManager = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
        // appTaskList.get(0).finishAndRemoveTask();
        System.exit(0);
    }
}
