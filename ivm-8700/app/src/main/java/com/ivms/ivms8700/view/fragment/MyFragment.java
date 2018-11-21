package com.ivms.ivms8700.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ivms.ivms8700.R;

public class MyFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ImageView message_btn;
    private boolean isMessage=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.my_layout,container,false);
            message_btn=(ImageView)view.findViewById(R.id.message_btn);
            message_btn.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.message_btn:
                  if(isMessage){
                      isMessage=false;
                      message_btn.setBackgroundResource(R.drawable.anniu_1);
                  }else{
                      isMessage=true;
                      message_btn.setBackgroundResource(R.drawable.anniu_2);
                  }
                break;

        }
    }
}
