package com.ivms.ivms8700.view;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;


public class SetingActivity extends Activity implements View.OnClickListener {

    private ImageView back_btn;
    private TextView save_btn;
    private LocalDbUtil localDbUtil=null;
    private EditText url_et;
    private EditText port_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        initView();
    }

    private void initView() {
        localDbUtil=new LocalDbUtil(this);
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn=(TextView)findViewById(R.id.right_btn);
        save_btn.setOnClickListener(this);

        url_et=(EditText)findViewById(R.id.url_et);
        port_et=(EditText)findViewById(R.id.port_et);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                  finish();
                break;
            case R.id.right_btn://保存
                String url=url_et.getText().toString().trim();
                String port=port_et.getText().toString().trim();
                 if(checkData(url,port)){
                     localDbUtil.setString("local_url",url);
                     localDbUtil.setString("local_port",port);
                     finish();
                 }
                break;

        }
    }
    //判断参数
    private boolean checkData(String url,String port) {

        if(url.isEmpty()){
            UIUtil.showToast(this,"地址不能为空");
            return false;
        }
         if(port.isEmpty()){
             UIUtil.showToast(this,"端口不能为空");
             return false;
         }

         return true;
    }
}
