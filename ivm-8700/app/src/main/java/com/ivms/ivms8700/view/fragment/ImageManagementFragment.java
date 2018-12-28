package com.ivms.ivms8700.view.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.NoDoubleClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ImageManagementFragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.llVideoScreenshot)
    LinearLayout llVideoScreenshot;
    @BindView(R.id.llVideoVideo)
    LinearLayout llVideoVideo;
    @BindView(R.id.flfragment)
    FrameLayout flfragment;
    @BindView(R.id.live_view)
    View liveView;
    @BindView(R.id.local_view)
    View localView;
    @BindView(R.id.ivscreenshot)
    ImageView ivScreen;
    private View view;
    private ScreenshotFragment screenshotFragment;
    private LocalVideoFragment localVideoFragment;
    private int selectWhat = 0;
    private FragmentManager fragmentManager;
    private boolean showEd = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.im_layout, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        setListener();
        initFragment(0, false);
        return view;
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if(!hidden){
//            if(showEd){
//                initFragment(0,true);
//            }else{
//                initFragment(0,false);
//
//            }
//            showEd = true;
////            if (screenshotFragment != null) {
////                screenshotFragment.onDestroy();
////                screenshotFragment = null;
////            }
////            if (localVideoFragment != null) {
////                localVideoFragment.onDestroy();
////                localVideoFragment = null;
////            }
//
//        }
//    }

    private void setListener() {
        llVideoScreenshot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                selectWhat = 0;
                initFragment(0, false);
            }
        });
        llVideoVideo.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                selectWhat = 1;
                initFragment(1, false);
            }
        });
        ivScreen.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (selectWhat == 0) {
                    screenshotFragment.changeShow();
                    screenshotFragment.choose();

                } else {
                    localVideoFragment.changeShow();
                    localVideoFragment.choose();

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        if (screenshotFragment != null) {
            screenshotFragment.onDestroy();
            screenshotFragment = null;
        }
        if (localVideoFragment != null) {
            localVideoFragment.onDestroy();
            localVideoFragment = null;
        }

    }


    private void initFragment(int index, boolean needReStart) {
        // 由于是引用了V4包下的Fragment，所以这里的管理器要用getSupportFragmentManager获取
        if (fragmentManager == null) {

            fragmentManager = getChildFragmentManager();
        }
        // 开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (needReStart) {
            transaction.remove(screenshotFragment);
            if (null != screenshotFragment) {
                screenshotFragment.clearDate();
            }
            transaction.remove(localVideoFragment);
            if (null != localVideoFragment) {
                screenshotFragment.clearDate();
            }
        }
        // 隐藏所有Fragment
        hideFragment(transaction);
        switch (index) {
            case 0:
                if (screenshotFragment == null) {
                    screenshotFragment = new ScreenshotFragment();
                    transaction.add(R.id.flfragment, screenshotFragment);


                } else {
                    transaction.show(screenshotFragment);
                }
                liveView.setVisibility(View.VISIBLE);
                localView.setVisibility(View.GONE);
                break;
            case 1:
                if (localVideoFragment == null) {
                    localVideoFragment = new LocalVideoFragment();
                    transaction.add(R.id.flfragment, localVideoFragment);
                } else {
                    transaction.show(localVideoFragment);
                }
                liveView.setVisibility(View.GONE);
                localView.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }

        // 提交事务
        transaction.commit();

    }

    //隐藏Fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (screenshotFragment != null) {
            transaction.hide(screenshotFragment);
        }
        if (localVideoFragment != null) {
            transaction.hide(localVideoFragment);
        }

    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (selectWhat == 0) {
            screenshotFragment.onKeyDown(keyCode, event);

        } else {
            localVideoFragment.onKeyDown(keyCode, event);

        }

    }
}
