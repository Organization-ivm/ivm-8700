package com.ivms.ivms8700.view;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.MenuTree;
import com.ivms.ivms8700.utils.GetData;

import java.util.ArrayList;
import java.util.List;

public class AddCamerActivity extends ListActivity {

    private TreeViewAdapter treeViewAdapter = null;
    /** 当前所显示的目录列表 */
    private List<MenuTree> menuTreeShowList = new ArrayList<MenuTree>();
    /** 所有的目录列表 */
    private List<MenuTree> menutreeList = new ArrayList<MenuTree>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menutreeList = (ArrayList<MenuTree>) GetData.getAllFolders();

        for (MenuTree tree : menutreeList) {
            // 添加最顶层目录
            if (tree.getParentId() == null || "".equals(tree.getParentId())) {
                menuTreeShowList.add(tree);
            }
        }

        treeViewAdapter = new TreeViewAdapter(this, R.layout.ouyline, menuTreeShowList);
        setListAdapter(treeViewAdapter);
        registerForContextMenu(getListView());
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

//            rl_menu.setPadding(25 * level + 15, icon.getPaddingTop(), 0, icon.getPaddingBottom());
            text.setText(mt.getText());
            if (mt.isHasChild()) {
                if (mt.isExpanded()) {
                    icon.setImageResource(R.drawable.arrow);
                } else {
                    icon.setImageResource(R.drawable.down);
                }
            } else if (!mt.isHasChild()) {
                icon.setImageResource(R.drawable.yuandian);
            }
            return convertView;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
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