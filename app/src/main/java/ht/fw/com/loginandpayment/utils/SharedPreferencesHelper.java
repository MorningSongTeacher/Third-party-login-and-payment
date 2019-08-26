package ht.fw.com.loginandpayment.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 36570 on 2018/6/12.
 */

public class SharedPreferencesHelper {

    public static final String SHAREDPREFERENCES_NAME = "cf_zhgs";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 存储
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object getSharedPreference(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * SharedPreferences 存map集合
     */
    public static void putHashMapData(Context context, String key, Map<String, String> datas) {
        JSONArray mJsonArray = new JSONArray();
        Iterator<Map.Entry<String, String>> iterator = datas.entrySet().iterator();
        JSONObject object = new JSONObject();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {

            }
        }
        mJsonArray.put(object);
        SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, mJsonArray.toString());
        editor.commit();
    }

    public static Map<String, String> getHashMapData(Context context, String key) {
        Map<String, String> datas = new HashMap<>();
        SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        datas.put(name, value);
                    }
                }
            }
        } catch (JSONException e) {
        }
        return datas;
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

}
