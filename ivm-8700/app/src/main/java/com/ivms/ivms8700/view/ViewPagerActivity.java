/*
 Copyright 2011, 2012 Chris Banes.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.ivms.ivms8700.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.PhotoVideoManager.bean.Bean;
import com.ivms.ivms8700.view.adapter.ImagePreviewAdapter;

import java.util.ArrayList;


public class ViewPagerActivity extends FragmentActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    private static ArrayList<Bean> list;
    private int item;
    private int pagerPosition;
    ViewPager mPager;
    private int   mCurrentPosition;
    ImagePreviewAdapter mAdapter;
    private LinearLayout main_linear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        initView();
        renderView();
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
            mPager.setCurrentItem(pagerPosition);
        }
        setLinstener();


    }

    private void initView() {
        mPager = findViewById(R.id.pager);
        list = getIntent().getParcelableArrayListExtra("list");
        item = getIntent().getIntExtra("position", 0);
        mCurrentPosition = item;
        main_linear = findViewById(R.id.main_linear);
        getData();
    }

    private void setLinstener() {
        main_linear.getChildAt(mCurrentPosition).setEnabled(true);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideAllIndicator(position);
                main_linear.getChildAt(position).setEnabled(true);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void  hideAllIndicator(int position){
        for(int i=0;i<list.size();i++){
            if(i!=position){
                main_linear.getChildAt(i).setEnabled(false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }
    private void renderView() {
        if(list==null) return;
        if(list.size()==1){
            main_linear.setVisibility(View.GONE);
        }else {
            main_linear.setVisibility(View.VISIBLE);
        }
        mAdapter = new ImagePreviewAdapter(this,list,mCurrentPosition);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mCurrentPosition);
    }

    /**
     * 获取数据
     */
    private void getData() {

        View view;
        for (Bean pic : list) {

            //创建底部指示器(小圆点)
            view = new View(ViewPagerActivity.this);
            view.setBackgroundResource(R.drawable.indicator);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
            //设置间隔
            if (!pic.getFilePath().equals(list.get(0).getFilePath())) {
                layoutParams.leftMargin = 20;
            }
            //添加到LinearLayout
            main_linear.addView(view, layoutParams);
        }
    }


}

