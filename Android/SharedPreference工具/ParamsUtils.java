package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.UtilsLib;
import com.newland.lib.utils.R;
import com.nlutils.util.LoggerUtils;

import java.util.Map;
import java.util.Set;

/**
 * 参数读写统一接口
 * 若存在MTMS客户端，参数保存在系统目录中文件，应用删除参数不丢失
 * 若不存在MTMS客户端，参数默认保存在应用目录中文件，应用删除参数丢失
 *
 * @author chenkh
 * @date 2015/3/12
 */
public class ParamsUtils {
    private static final String SHARE_FILE = "params_file";

    private static Context getContext() {
        Context context = UtilsLib.getInstance().getContext();
        if (context == null) {
            return null;
        } else {
            return context.getApplicationContext();
        }
    }

    /**
     * 参数保存到文件
     *
     * @param paramsMap 参数k-v内容
     */
    public static boolean save(Map<String, String> paramsMap) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> key = paramsMap.keySet();
        for (String s : key) {
            editor.putString(s, paramsMap.get(s));
        }
        return editor.commit();
    }

    /**
     * 将文件中的参数清空
     */
    public static boolean clean() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return editor.clear().commit();
    }

    /**
     * 获取参数文件中所有参数
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> get() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_FILE, Activity.MODE_PRIVATE);
        return (Map<String, String>) sharedPreferences.getAll();
    }

    /**
     * 获取参数文件中指定参数（用于获取字符串）
     */
    public static String getString(String key) {
        return getString(key, null);
    }

    /**
     * 获取参数文件中指定参数（用于获取字符串）
     */
    public static String getString(String key, String defaultValue) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_FILE, Activity.MODE_PRIVATE);
        if (key == null || "".equals(key)) {
            throw new RuntimeException(getContext().getString(R.string.error_param_error));
        }
        String value = sharedPreferences.getString(key, null);
        if (value == null) {
            value = defaultValue;
        }
        LoggerUtils.i("Preferences get->key:" + key + ", value:" + value);
        return value;
    }

    /**
     * 设置字符串的参数
     *
     * @param key   参数名
     * @param value 值
     */
    public static boolean setString(String key, String value) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_FILE, Activity.MODE_PRIVATE);
        if (key == null || "".equals(key)) {
            throw new RuntimeException(getContext().getString(R.string.error_key_param_null));
        }
        LoggerUtils.i("Preferences set->key:" + key + ", value:" + value);
        return sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 设置boolean的参数
     *
     * @param key   参数key
     * @param value 默认值
     */
    public static boolean setBoolean(String key, boolean value) {
        try {
            if (key != null && !"".equals(key)) {
                LoggerUtils.i("Preferences Set->key:" + key + ",value:" + value);
                String vString = value ? "1" : "0";
                return setString(key, vString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取boolean的参数，默认值false
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 获取boolean的参数
     *
     * @param key          参数key
     * @param defaultValue 默认值
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            boolean value = false;
            if (key != null && !"".equals(key)) {
                String strValue = getString(key);
                LoggerUtils.i("Preferences get->key:" + key + ",value:" + strValue);
                if ("1".equals(strValue)) {
                    value = true;
                } else if ("0".equals(strValue)) {
                    value = false;
                } else {
                    value = defaultValue;
                }
            }
            return value;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取参数文件中指定key的参数，默认值0
     */
    public static long getLong(String key) {
        return getLong(key, 0);
    }

    /**
     * 获取long的参数
     *
     * @param key          参数key
     * @param defaultValue 默认值
     */
    public static long getLong(String key, long defaultValue) {
        try {
            long value = defaultValue;
            if (key != null && !"".equals(key)) {
                String strValue = getString(key);
                value = Long.valueOf(strValue);
            }
            LoggerUtils.i("Preferences get->key:" + key + ",value:" + value);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * 获取参数文件中指定key的参数，默认值0
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取int的参数
     *
     * @param key          参数key
     * @param defaultValue 默认值
     */
    public static int getInt(String key, int defaultValue) {
        try {
            int value = defaultValue;
            if (key != null && !"".equals(key)) {
                String strValue = getString(key);
                value = Integer.valueOf(strValue);
            }
            LoggerUtils.i("Preferences get->key:" + key + ",value:" + value);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 设置int的参数
     */
    public static boolean setInt(String key, int value) {
        try {
            if (key != null && !"".equals(key)) {
                String strValue = String.valueOf(value);
                LoggerUtils.i("Preferences Set->key:" + key + ",value:" + strValue);
                return setString(key, strValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除已存内容
     *
     * @param key 参数key
     * @return 是否成功
     */
    public static boolean remove(String key) {
        try {
            if (key != null && !"".equals(key)) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_FILE, Activity.MODE_PRIVATE);
                LoggerUtils.i("Preferences remove->key:" + key);
                return sharedPreferences.edit().remove(key).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
