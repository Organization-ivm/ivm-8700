package com.ivms.ivms8700.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;


import java.util.Calendar;

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;//这里设置不能超过多长时间
    private long lastClickTime = 0;
    protected abstract void onNoDoubleClick(View v);
    private  ScaleAnimation  scaleAnimation;
    public NoDoubleClickListener() {
          scaleAnimation = new ScaleAnimation(1.0f, 0.97f, 1.0f, 0.97f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
          scaleAnimation.setDuration(80);
          scaleAnimation.setRepeatMode(Animation.REVERSE);
          scaleAnimation.setRepeatCount(1);
    }
    @Override
    public void onClick(final View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
             final  int color= view.getDrawingCacheBackgroundColor();
            view.startAnimation(scaleAnimation);
            onNoDoubleClick(view);
        }
    }
}
