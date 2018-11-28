//package com.ivms.ivms8700.utils.PhotoVideoManager;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.StrictMode;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.os.Bundle;
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
//import com.photochoose.ph.adapter.ImageAdapter;
//import com.photochoose.ph.bean.Bean;
//import com.photochoose.ph.utils.PhotoUtils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends Activity implements ImageAdapter.OnShowItemClickListener {
//
//    private GridView gridView;
//
//    private String path = Environment.getExternalStorageDirectory() + "/test/";
//    private TextView tv_sel;
//    private ImageView iv_del;
//    private ImageView iv_background;
//    public List<Bean> dataList, selectedList;
//    private List<String> paths = new ArrayList<String>();
//    private List<String> items = new ArrayList<String>();
//    private ImageAdapter myAdapter;
//    private static boolean isShow;
//    private boolean isSel = true;
//
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
//
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
//                        file = new File(selectedList.get(i).getFilePath());
//                        if (file.exists()) {
//                            file.delete();
//                        } else {
//                            Toast.makeText(MainActivity.this, "文件已删除", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    for (int i = 0; i < selectedList.size(); i++) {
//                        Log.i("tag", "==selectedList===" + selectedList.get(i).getFilePath());
//                    }
//                    dataList.removeAll(selectedList);
//                    selectedList.clear();
//                    dataList.clear();
//                    myAdapter.clearData();
//                    getAllFiles(path);
//                    Log.i("tag", "==items.size===" + items.size());
//                    for (int i = 0; i < paths.size(); i++) {
//                        Bean bean = new Bean();
//                        bean.setFilePath(paths.get(i));
//                        bean.setChecked(false);
//                        bean.setShow(isShow);
//                        dataList.add(bean);
//                    }
//                    isAuto(!isShow);
//                    myAdapter.setItems(dataList);
//                    gridView.setAdapter(myAdapter);
//                    myAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(MainActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
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
//                    Bean item = dataList.get(position);
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
//                    file = new File(paths.get(position));
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + file), "image/*");
//                    startActivity(intent);
//                }
//
//            }
//        });
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
//                for (Bean item : dataList) {
//                    isShow = false;
//                    item.setChecked(false);
//                }
//                myAdapter.notifyDataSetChanged();
//            } else {
//                finish();
//            }
//            for (Bean item : dataList) {
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
//        if (paths.size() == 0 || dataList.size() == 0) {
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
//        selectedList = new ArrayList<Bean>();
//        iv_background = (ImageView) findViewById(R.id.background2);
//        dataList = new ArrayList<Bean>();
//        initDataList();
//        myAdapter = new ImageAdapter(this, paths, gridView, dataList);
//        myAdapter.setOnShowItemClickListener(MainActivity.this);
//        gridView.setAdapter(myAdapter);
//    }
//
//    private void initDataList() {
//        getAllFiles(path);
//        for (int i = 0; i < paths.size(); i++) {
//            Bean bean = new Bean();
//            bean.setFilePath(paths.get(i));
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
//            for (Bean item : dataList) {
//                isShow = false;
//                item.setChecked(false);
//            }
//            selectedList.clear();
//            myAdapter.notifyDataSetChanged();
//        }
//        for (Bean item : dataList) {
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
//                    if (name.length == 2 && name[1].equals("jpg") || name[1].equals("png")) {
//                        items.add(files[i].getName());
//                        paths.add(files[i].getPath());
//                    }
//                }
//            }
//            return;
//        }
//    }
//
//    @Override
//    public void onShowItemClick(Bean bean) {
//        if (bean.getFilePath().equals(paths.get(0))) {
//            selectedList.add(bean);
//        }
//        if (bean.isChecked()) {
//            selectedList.add(bean);
//        } else {
//            selectedList.remove(bean);
//        }
//    }
//}
