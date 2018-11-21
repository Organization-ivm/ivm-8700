package com.ivms.ivms8700.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.view.fragment.ImageManagementFragment;
import com.ivms.ivms8700.view.fragment.MessageFragment;
import com.ivms.ivms8700.view.fragment.MyFragment;
import com.ivms.ivms8700.view.fragment.VideoFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
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
    private ImageView my_img;
    private ImageView video_img;
    private TextView video_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFragment(0);
    }

    private void initView() {
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
        message_txt=(TextView)findViewById(R.id.message_txt);
        my_img =(ImageView)findViewById(R.id.my_img);
        my_txt=(TextView)findViewById(R.id.my_txt);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_lay:

                updateUI(0);
                initFragment(0);
                break;
            case R.id.image_management_lay:
                updateUI(1);
                initFragment(1);
                break;
            case R.id.message_lay:
                updateUI(2);
                initFragment(2);
                break;
            case R.id.my_lay:
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
            video_img.setBackgroundResource(R.drawable.xuanzhong);
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
}
