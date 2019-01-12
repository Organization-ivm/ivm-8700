package com.ivms.ivms8700.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ivms.ivms8700.R;

public class WelcomeActivity extends Activity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用LayoutInflater来加载activity_splash.xml视图
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_welcome, null);

        /**
         * 这里不能使用findViewById(R.layout.acitivyt_spash)方法来加载
         * 因为还没有开始调用setContentView()方法，也就是说还没给当前的Activity
         * 设置视图，当前Activity Root View为null，findViewById()方法是从当前
         * Activity的Root View中获取子视图，所以这时候会报NullPointerException异常
         *
         * View rootView = findViewById(R.layout.activity_splash);
         *
         */

        setContentView(rootView);
        mHandler = new Handler();

        //初始化渐变动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome);
        //设置动画监听器
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //当监听到动画结束时，开始跳转到MainActivity中去
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(i);
                        WelcomeActivity.this.finish();
                    }
                });
            }
        });

        //开始播放动画
        rootView.startAnimation(animation);
    }

}
