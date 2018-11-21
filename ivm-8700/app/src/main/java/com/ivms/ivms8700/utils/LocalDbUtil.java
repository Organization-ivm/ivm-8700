package com.ivms.ivms8700.utils;


import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author zsq2018年11月21日
 * 本地轻量级存储
 */
public class LocalDbUtil {

    private Context context;
    private SharedPreferences sharedPreferences;

    /**
     * 默认的存储
     *
     * @param context
     */
    public LocalDbUtil(Context context) {
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences("ivms", Context.MODE_PRIVATE);
    }
    /**
     * 指定文件存储
     *
     * @param context
     * @param file
     * @param mode
     */

    public LocalDbUtil(Context context, String file, int mode) {
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(file, mode);
    }
    public void setString(String key, String value) {
        this.sharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key) {
        return this.sharedPreferences.getString(key, "");
    }

    public void setInteger(String key, int value) {
        this.sharedPreferences.edit().putInt(key, value).commit();
    }

    public int getInteger(String key) {
        return this.sharedPreferences.getInt(key, 0);
    }

    public void setFloat(String key, float value) {
        this.sharedPreferences.edit().putFloat(key, value).commit();
    }

    public float getFloat(String key) {
        return this.sharedPreferences.getFloat(key, 0);
    }

    public void setLong(String key, long value) {
        this.sharedPreferences.edit().putLong(key, value).commit();
    }

    public long getLong(String key) {
        return this.sharedPreferences.getLong(key, 0);
    }

    public Map<String, Object> getSpecific(String[] specificField) {
        Map<String, Object> specific = new HashMap<String, Object>();
        Map<String, ?> allStrored = this.sharedPreferences.getAll();
        for (String key : specificField) {
            specific.put(key, allStrored.get(key));
        }
        return specific;
    }

    public void setBoolean(String key, boolean value) {
        this.sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return this.sharedPreferences.getBoolean(key, false);
    }

    public void remove(String key) {
        this.sharedPreferences.edit().remove(key).commit();
    }

    public void setMap(Map<String, ?> map) {
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Integer) {
                this.sharedPreferences.edit().putInt(key, (Integer) value);
            } else if (value instanceof String) {
                this.sharedPreferences.edit().putString(key, (String) value);
            } else if (value instanceof Long) {
                this.sharedPreferences.edit().putLong(key, (Long) value);
            } else if (value instanceof Float) {
                this.sharedPreferences.edit().putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                this.sharedPreferences.edit().putBoolean(key, (Boolean) value);
            }
        }
        this.sharedPreferences.edit().commit();
    }

    public Object getValueByKey(String key) {
        if (this.sharedPreferences.contains(key)) {
            Map<String, ?> all = this.sharedPreferences.getAll();
            return all.get(key);
        }
        return null;
    }

    public void flush() {
        this.sharedPreferences.edit().clear().commit();
    }

}
