package com.ivms.ivms8700.view.customui;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivms.ivms8700.R;

/**
 * @author yyszsq2017年6月18日
 *  自定义加载动画
 */
public class MyProgressDialog extends Dialog{

    private Context context;
    private String message;
    private View myDialogView;
    private TextView progress_txt;
    private LinearLayout whole_layout;
    private LinearLayout aaabbb;

    public MyProgressDialog(Context context, String message) {
        super(context, R.style.my_progress_dialog);
        this.context = context;
        this.message = message;
    }
    public MyProgressDialog(Context context, int resId ){
        super(context, R.style.my_progress_dialog);
        this.context = context;
        this.message = context.getString(resId);
    }

    public MyProgressDialog(Context context) {
        super(context, R.style.my_progress_dialog);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //设置点击外部取消
        setCanceledOnTouchOutside(false);

        myDialogView = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout, null);
        progress_txt  = (TextView) myDialogView.findViewById(R.id.progress_txt);
        aaabbb  = (LinearLayout) myDialogView.findViewById(R.id.aaabbb);
        aaabbb.getBackground().setAlpha(210);

        if(!TextUtils.isEmpty(message)){
            progress_txt.setVisibility(View.VISIBLE);
            progress_txt.setText(message);
        }else{
            progress_txt.setVisibility(View.GONE);
        }
        setContentView(myDialogView);
    }


}
