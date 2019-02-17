package com.ivms.ivms8700.utils;

import com.ivms.ivms8700.bean.MenuTree;
import com.ivms.ivms8700.control.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetData {


    /**
     * 描述:获得模块列表,解析出层级目录
     * */
    public static List<MenuTree> getAllFolders() {
        List<MenuTree> list = new ArrayList<MenuTree>();
        try {
            JSONArray jsonArray =  MyApplication.getIns().getVideoList();
            list = parseJsonArray("", 0, jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<MenuTree> parseJsonArray(String parentId, int level, JSONArray jsonArray) throws JSONException {
        List<MenuTree> list = new ArrayList<MenuTree>();
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject item = jsonArray.getJSONObject(j);
            String id="";
            String text="";
            String sysCode="";
            String videoIP="";
            String videoPort="";
            String videoUser="";
            String videoPassword="";

            JSONArray children=null;
            if(level==0) {
                id = item.optString("lineCode");
                text = item.optString("lineName");
                children  = item.getJSONArray("stations");
            }else  if (level==1){
                id = item.optString("stationCode");
                text = item.optString("stationName");
                children  = item.getJSONArray("cameras");
            }else if (level==2){
                id = item.optString("cameraCode");
                text = item.optString("cameraName");
                sysCode = item.optString("sysCode");
                videoIP = item.optString("videoIP");
                videoPort = item.optString("videoPort");
                videoUser = item.optString("videoUser");
                videoPassword = item.optString("videoPassword");
                children  = null;
            }
            if (children != null && children.length() > 0) {
                MenuTree tree = new MenuTree(id, text, parentId, true, level);
                List<MenuTree> childList = parseJsonArray(id, level + 1, children);
                list.add(tree);
                list.addAll(childList);

            } else {
//                MenuTree tree = new MenuTree(id, text, parentId, false, level);
                MenuTree tree = new MenuTree();
                tree.setId(id);
                tree.setText(text);
                tree.setParentId(parentId);
                tree.setHasChild(false);
                tree.setLevel(level);
                tree.setSyscode(sysCode);
                tree.setVideoip(videoIP);
                tree.setVideoport(videoPort);
                tree.setVideouser(videoUser);
                tree.setVideopassword(videoPassword);

                list.add(tree);
            }
        }
        return list;
    }

}
