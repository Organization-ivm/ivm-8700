package com.ivms.ivms8700.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivms.ivms8700.R;
/****
 * 安全帽识别
 *  by Alan
 * **/
public class HelmetIdentActivity extends Activity implements View.OnClickListener {

    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helmet_ident);
        initView();
    }

    private void initView() {
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn=(TextView)findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        save_btn.setOnClickListener(this);
        title_txt=(TextView)findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.helmet_identification));
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
