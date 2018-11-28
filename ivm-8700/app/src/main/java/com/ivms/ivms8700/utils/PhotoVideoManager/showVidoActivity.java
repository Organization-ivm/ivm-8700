//package com.ivms.ivms8700.utils.PhotoVideoManager;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.StrictMode;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.photochoose.ph.adapter.VideoAdapter;
//import com.photochoose.ph.utils.VideoUtils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class showVidoActivity extends Activity implements VideoAdapter.OnShowItemClickListener {
//
//    private GridView gridView;
//
//    private String path = Environment.getExternalStorageDirectory() + "/test/";
//    private TextView tv_sel;
//    private ImageView iv_del;
//    private ImageView iv_background;
//    public List<VideoInfo> dataList, selectedList;
//    //    private List<String> paths = new ArrayList<String>();
////    private List<String> items = new ArrayList<String>();
//    private List<VideoInfo> videoList = new ArrayList<>();
//
//    private VideoAdapter myAdapter;
//    private static boolean isShow;
//    private boolean isSel = true;
//    private int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1001;
//
//    private File file;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        isShow = false;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
//        }
//        setContentView(R.layout.activity_main);
//
//        requestPremission();
//
//
//        initView();
//        isshowImage();
//        //删除图标的监听
//        iv_del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (selectedList != null && selectedList.size() > 0) {
//                    for (int i = 0; i < selectedList.size(); i++) {
//                        file = new File(selectedList.get(i).getBath());
//                        if (file.exists()) {
//                            file.delete();
//                        } else {
//                            Toast.makeText(showVidoActivity.this, "文件已删除", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    for (int i = 0; i < selectedList.size(); i++) {
//                        Log.i("tag", "==selectedList===" + selectedList.get(i).getBath());
//                    }
//                    dataList.removeAll(selectedList);
//                    selectedList.clear();
//                    dataList.clear();
//                    myAdapter.clearData();
//                    getAllFiles(path);
//                    Log.i("tag", "==video.size===" + videoList.size());
//                    for (int i = 0; i < videoList.size(); i++) {
//                        VideoInfo bean = new VideoInfo();
//                        bean.setBath(videoList.get(i).getBath());
//                        bean.setChecked(false);
//                        bean.setShow(isShow);
//                        dataList.add(bean);
//                    }
//                    isAuto(!isShow);
//                    myAdapter.setItems(dataList);
//                    gridView.setAdapter(myAdapter);
//                    myAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(showVidoActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
//                }
//                isshowImage();
//            }
//        });
//        //选择的监听
//        tv_sel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isShow = !isShow;
//                isAuto(isShow);
//            }
//        });
//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                isShow = !isShow;
//                isAuto(isShow);
//                return true;
//            }
//        });
////        gridVeiw 设置监听
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                //判断CheckBox是否显示，如果显示说明在选择状态，否则是浏览状态用图片浏览器打开
//                if (isShow) {
//                    VideoInfo item = dataList.get(position);
//                    boolean isChecked = item.isChecked();
//                    Log.i("tag", "===isChecked===" + isChecked);
//                    if (isChecked) {
//                        item.setChecked(false);
//                    } else {
//                        item.setChecked(true);
//                    }
//                    Log.i("tag", "===isChecked=after==" + isChecked);
//                    myAdapter.notifyDataSetChanged();
//                } else {
//                    file = new File(videoList.get(position).getBath());
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + file), "image/*");
//                    startActivity(intent);
//                }
//
//            }
//        });
//    }
//
//    private void requestPremission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
//            } else {
////                savePhoto();
//
////                showToast("权限已申请");
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                savePhoto();
//            } else {
//                Toast.makeText(MyApplication.getIns(), "权限已拒绝", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    }
//
//
//    //监听返回键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (tv_sel.getText().toString().equals("取消")) {
//                tv_sel.setText("选择");
//                selectedList.clear();
//                for (VideoInfo item : dataList) {
//                    isShow = false;
//                    item.setChecked(false);
//                }
//                myAdapter.notifyDataSetChanged();
//            } else {
//                finish();
//            }
//            for (VideoInfo item : dataList) {
//                item.setShow(isShow);
//            }
//            myAdapter.notifyDataSetChanged();
//            isSel = !isSel;
//        }
//        return true;
//    }
//
//    //是否显示背景
//    private void isshowImage() {
//        if (videoList.size() == 0 || dataList.size() == 0) {
//            iv_background.setVisibility(View.VISIBLE);
//        } else {
//            iv_background.setVisibility(View.GONE);
//        }
//    }
//
//    private void initView() {
//        gridView = (GridView) findViewById(R.id.gridview);
//        tv_sel = (TextView) findViewById(R.id.id_user_photoLibSel);
//        iv_del = (ImageView) findViewById(R.id.id_user_photoLib_del);
//        selectedList = new ArrayList<VideoInfo>();
//        iv_background = (ImageView) findViewById(R.id.background2);
//        dataList = new ArrayList<VideoInfo>();
//        initDataList();
//        myAdapter = new VideoAdapter(this, videoList, gridView, dataList);
//        myAdapter.setOnShowItemClickListener(this);
//        gridView.setAdapter(myAdapter);
//    }
//
//    private void initDataList() {
//        getAllFiles(path);
//        for (int i = 0; i < videoList.size(); i++) {
//            VideoInfo bean = new VideoInfo();
//            bean.setBath(videoList.get(i).getBath());
//            bean.setBitmap(videoList.get(i).getBitmap());
//            bean.setTime(videoList.get(i).getTime());
//            bean.setChecked(false);
//            bean.setShow(isShow);
//            dataList.add(bean);
//        }
//    }
//
//    private void isAuto(boolean is) {
//        isSel = is;
//        if (isSel) {
//            tv_sel.setText("取消");
//        } else {
//            tv_sel.setText("选择");
//            for (VideoInfo item : dataList) {
//                isShow = false;
//                item.setChecked(false);
//            }
//            selectedList.clear();
//            myAdapter.notifyDataSetChanged();
//        }
//        for (VideoInfo item : dataList) {
//            item.setShow(isShow);
//        }
//        myAdapter.notifyDataSetChanged();
//        isSel = !isSel;
//    }
//
//    private void getAllFiles(String path) {
//        File file = new File(path);
//        File files[] = file.listFiles();
//        if (files != null) {
//            for (int i = 0; i < files.length; i++) {
//                if (files[i].isDirectory()) {
//                    String path1 = files[i].getPath() + "/";
//                    getAllFiles(path1);
//                } else {
//                    String name[] = files[i].getName().split("\\.");
//                    if (name.length == 2 && name[1].equals("mp4")) {
//                        VideoInfo videoInfo = new VideoInfo();
//                        videoInfo.setName(files[i].getName());
//                        videoInfo.setBath(files[i].getPath());
//                        videoInfo.setBitmap(VideoUtils.getBitmapFromFile(files[i].getPath()));
//                        videoInfo.setTime(VideoUtils.getVideoDuration(files[i].getPath()));
//                        videoList.add(videoInfo);
//                    }
//                }
//            }
//            return;
//        }
//    }
//
//    @Override
//    public void onShowItemClick(VideoInfo bean) {
//        if (bean.getBath().equals(videoList.get(0).getBath())) {
//            selectedList.add(bean);
//        }
//        if (bean.isChecked()) {
//            selectedList.add(bean);
//        } else {
//            selectedList.remove(bean);
//        }
//    }
//}
