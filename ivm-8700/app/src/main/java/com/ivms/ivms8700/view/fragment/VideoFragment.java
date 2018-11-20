package com.ivms.ivms8700.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ivms.ivms8700.R;

public class VideoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private LinearLayout ll_jiankong;
    private RelativeLayout rl_select_btn;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.video_layout,container,false);
            ll_jiankong=(LinearLayout)view.findViewById(R.id.ll_jiankong);
            rl_select_btn=(RelativeLayout)view.findViewById(R.id.rl_select_btn);
            rl_select_btn.setOnClickListener(this);

        }
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
