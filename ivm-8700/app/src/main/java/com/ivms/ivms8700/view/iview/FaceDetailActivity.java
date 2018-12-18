package com.ivms.ivms8700.view.iview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.FaceEntity;
import com.ivms.ivms8700.utils.UIUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/****
 * 人脸识别详情
 *  by Alan
 * **/
public class FaceDetailActivity extends Activity implements View.OnClickListener {

    private TextView num_txt;
    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;
    private TextView name_txt;
    private ImageView user_img;
    private String imageUrl="";
    private Bitmap bitmap;
    private TextView sex_txt;
    private TextView dw_txt;
    private TextView bm_txt;
    private TextView phone_txt;
    private TextView rq_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detail);
        initView();
        initData();
    }
    //初始化控件
    private void initView() {
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn=(TextView)findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        title_txt=(TextView)findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.renlianshibie_kaoqin_detail));
        num_txt=(TextView)findViewById(R.id.num_txt);
        name_txt=(TextView)findViewById(R.id.name_txt);
        user_img=(ImageView)findViewById(R.id.user_img);
        sex_txt=(TextView)findViewById(R.id.sex_txt);
        dw_txt=(TextView)findViewById(R.id.dw_txt);
        bm_txt=(TextView)findViewById(R.id.bm_txt);
        phone_txt=(TextView)findViewById(R.id.phone_txt);
        rq_txt=(TextView)findViewById(R.id.rq_txt);
    }
    //初始化数据
    private void initData() {
            FaceEntity faceEntity  =(FaceEntity)getIntent().getSerializableExtra("entity");
            num_txt.setText(faceEntity.getEmployeeNumber());
            name_txt.setText(faceEntity.getName());
            sex_txt.setText(faceEntity.getSex());
            dw_txt.setText(faceEntity.getOfficeName());
            bm_txt.setText(faceEntity.getDepartment());
            phone_txt.setText(faceEntity.getPhone());
            rq_txt.setText(faceEntity.getDate());
            imageUrl ="http://222.66.82.4:80/shm/"+faceEntity.getFaceCapture();
            Log.i("Alan","url="+imageUrl);
            Thread imageViewHander = new Thread(new NetImageHandler());
            imageViewHander.start();
    }

    //控件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;


        }
    }

    class NetImageHandler implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                //发送消息，通知UI组件显示图片
                handler.sendEmptyMessage(0);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                user_img.setImageBitmap(bitmap);
            }
        }
    };
}
