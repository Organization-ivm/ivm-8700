package com.ivms.ivms8700.view;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.MenuTree;
import com.ivms.ivms8700.utils.GetData;

import java.util.ArrayList;
import java.util.List;

public class AddCamerActivity extends Activity implements View.OnClickListener {

    private TreeViewAdapter treeViewAdapter = null;
    /** 当前所显示的目录列表 */
    private List<MenuTree> menuTreeShowList = new ArrayList<MenuTree>();
    /** 所有的目录列表 */
    private List<MenuTree> menutreeList = new ArrayList<MenuTree>();
    private ListView camer_list;
    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camer);
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn = (TextView) findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        title_txt = (TextView) findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.select_monitoringpoint));
        camer_list=(ListView)findViewById(R.id.camer_list);
        menutreeList = (ArrayList<MenuTree>) GetData.getAllFolders();

        for (MenuTree tree : menutreeList) {
            // 添加最顶层目录
            if (tree.getParentId() == null || "".equals(tree.getParentId())) {
                menuTreeShowList.add(tree);
            }
        }
        treeViewAdapter = new TreeViewAdapter(this, R.layout.ouyline, menuTreeShowList);
        camer_list. setAdapter(treeViewAdapter);
        camer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!menuTreeShowList.get(position).isHasChild()) {// 没有子节点
                    Toast.makeText(AddCamerActivity.this, menuTreeShowList.get(position).getText(), Toast.LENGTH_SHORT).show();
                    // 监控点资源
                    SubResourceNodeBean mCamera = new SubResourceNodeBean();

                    mCamera.setPid(menuTreeShowList.get(position).getId().toString());
                    mCamera.setName(menuTreeShowList.get(position).getText().toString());
                    mCamera.setSysCode(menuTreeShowList.get(position).getSyscode().toString());

                    // 设置返回数据
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    // 把Persion数据放入到bundle中
                    bundle.putSerializable("camera",mCamera);
                    bundle.putSerializable("item",(MenuTree)menuTreeShowList.get(position));
                    intent.putExtras(bundle);
                    // 返回intent
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (menuTreeShowList.get(position).isExpanded()) {// 展开状态,点击关闭
                    menuTreeShowList.get(position).setExpanded(false);
                    MenuTree tree = menuTreeShowList.get(position);

                    List<MenuTree> temp = new ArrayList<MenuTree>();

                    for (int i = position + 1; i < menuTreeShowList.size(); i++) {
                        if (tree.getLevel() >= menuTreeShowList.get(i).getLevel()) {
                            break;
                        }
                        temp.add(menuTreeShowList.get(i));
                    }

                    menuTreeShowList.removeAll(temp);

                    treeViewAdapter.notifyDataSetChanged();
                } else {// 关闭状态,点击打开
                    menuTreeShowList.get(position).setExpanded(true);

                    int j = 1;
                    for (MenuTree mt : menutreeList) {
                        if (mt.getParentId().equals(menuTreeShowList.get(position).getId())) {
                            menuTreeShowList.add(position + j, mt);
                            j++;
                        }
                    }
                    treeViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;

        }
    }

    private class TreeViewAdapter extends ArrayAdapter {
        private LayoutInflater mInflater;
        private List<MenuTree> mfilelist;

        public TreeViewAdapter(Context context, int textViewResourceId, List list) {
            super(context, textViewResourceId, list);
            mInflater = LayoutInflater.from(context);
            mfilelist = list;
        }

        public int getCount() {
            return mfilelist.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text;
            ImageView icon;
            RelativeLayout rl_menu;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ouyline, null);
            }
            text = (TextView) convertView.findViewById(R.id.text);
            icon = (ImageView) convertView.findViewById(R.id.icon);
            rl_menu = (RelativeLayout) convertView.findViewById(R.id.rl_menu);

            MenuTree mt = mfilelist.get(position);
            int level = mt.getLevel();

            rl_menu.setPadding(25 * level + 15, 30, 0, 30);
            text.setText(mt.getText());

            text.setTextColor(getResources().getColor(R.color.Translucency));

            if (mt.isHasChild()) {
                if (mt.isExpanded()) {
                    icon.setImageResource(R.mipmap.right_jiantou);
                } else {
                    icon.setImageResource(R.mipmap.down_jiantou);
                }
            } else if (!mt.isHasChild()) {
                icon.setImageResource(R.drawable.yuandian);
                Log.i("Alan","是否在线："+mt.getIsonline());
                if(null!=mt.getIsonline()&&!mt.getIsonline().equals("0")){
                    text.setTextColor(getResources().getColor(R.color.main_text_a0a0a0));
                }
            }
            return convertView;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!menuTreeShowList.get(position).isHasChild()) {// 没有子节点
            Toast.makeText(this, menuTreeShowList.get(position).getText(), Toast.LENGTH_LONG).show();
            return;
        } else if (menuTreeShowList.get(position).isExpanded()) {// 展开状态,点击关闭
            menuTreeShowList.get(position).setExpanded(false);
            MenuTree tree = menuTreeShowList.get(position);

            List<MenuTree> temp = new ArrayList<MenuTree>();

            for (int i = position + 1; i < menuTreeShowList.size(); i++) {
                if (tree.getLevel() >= menuTreeShowList.get(i).getLevel()) {
                    break;
                }
                temp.add(menuTreeShowList.get(i));
            }

            menuTreeShowList.removeAll(temp);

            treeViewAdapter.notifyDataSetChanged();
        } else {// 关闭状态,点击打开
            menuTreeShowList.get(position).setExpanded(true);

            int j = 1;
            for (MenuTree mt : menutreeList) {
                if (mt.getParentId().equals(menuTreeShowList.get(position).getId())) {
                    menuTreeShowList.add(position + j, mt);
                    j++;
                }
            }
            treeViewAdapter.notifyDataSetChanged();
        }
    }
}