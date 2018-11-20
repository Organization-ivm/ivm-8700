package com.ivms.ivms8700.view;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.ivms.ivms8700.R;


public class SetingActivity extends Activity implements View.OnClickListener {

    private ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        initView();
    }

    private void initView() {
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                  finish();
                break;


        }
    }
}
