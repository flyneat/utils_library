package com.utils;


import java.lang.reflect.Method;
import java.util.Locale;

import com.nlutils.util.LoggerUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 通讯设置工具类
 *
 * @author jianshengd
 * @date 2018/3/26
 */
public class CommunicationUtils {

    final static private int CDMA = 0;
    final static private int GPRS = 1;
    final static private int WIFI = 2;
    final static private int CM = 1;
    final static private int CU = 2;
    final static private int CT = 3;

    static private String sName;
    static private String sApn;
    static private String sMcc;
    static private String sMnc;


    /**
     * 获取移动数据开关状态
     *
     * @return 移动数据开关
     */
    static private boolean getMobileDataStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isOpen = false;

        try {
            Method method = connectivityManager.getClass().getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            isOpen = (boolean) method.invoke(connectivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    /**
     * 系统通讯开关初始化
     *
     * @param type    收单应用的通讯方式
     * @param context 上下文
     */
    public static void commSwitchInit(int type, Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        switch (type) {
            case CDMA:
            case GPRS:
                LoggerUtils.d("type CDMA or GPRS");
                wifiManager.setWifiEnabled(false);
                break;
            case WIFI:
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    wifiManager.setWifiEnabled(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前WiFiSSID
     */
    public static String getPreferWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }
        int wifiState = wifiManager.getWifiState();
        if (wifiState != WifiManager.WIFI_STATE_ENABLED) {
            return null;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }

    /**
     * 设置默认APN
     *
     * @param context 设备上下文
     */
    public static void setDefaultApnEntity(Context context) {
        sName = getApnNameBySim(context, "中国移动");
        sApn = getApnBySim(context, "cmnet");
        sMcc = "460";
        sMnc = getMncBySim(context, "02");
    }

    /**
     * 返回SIM卡运营商
     *
     * @param context 设备上下文
     */
    public static int getServiceProvider(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        if (telManager == null) {
            return -1;
        }
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if ("46000".equals(operator) || "46002".equals(operator)) {
                return CM;
            } else if ("46001".equals(operator)) {
                return CU;
            } else if ("46003".equals(operator)) {
                return CT;
            }
        }
        return -1;
    }

    /**
     * 返回默认apn
     *
     * @param context    设备上下文
     * @param defaultApn 默认信息
     * @return 网络
     */
    public static String getApnBySim(Context context, String defaultApn) {
        int type = getServiceProvider(context);

        switch (type) {
            case CT:
                return "ctnet";
            case CM:
                return "cmnet";
            case CU:
                return "3gnet";
            default:
                return defaultApn;
        }
    }

    /**
     * 返回默认apn名称
     *
     * @param context        设备上下文
     * @param defaultApnName 默认信息
     * @return 运营商名字
     */
    public static String getApnNameBySim(Context context, String defaultApnName) {
        int type = getServiceProvider(context);

        switch (type) {
            case CT:
                return "中国电信";
            case CM:
                return "中国移动";
            case CU:
                return "中国联通";
            default:
                return defaultApnName;
        }
    }

    /**
     * 获取SIM卡MNC
     *
     * @param context    设备上下文
     * @param defaultMnc 默认信息
     * @return mnc信息
     */
    public static String getMncBySim(Context context, String defaultMnc) {
        if (checkSimAndGetMccMnc(context)) {
            return sMnc;
        }

        LoggerUtils.d("无sim卡");
        return defaultMnc;
    }

    /**
     * 返回默认mnc
     */
    public static boolean checkSimAndGetMccMnc(Context context) {
        if (context.getResources().getConfiguration().mcc == 0) {
            return false;
        }
        sMcc = String.valueOf(context.getResources().getConfiguration().mcc);
        sMnc = String.format(Locale.getDefault(), "%02d", context.getResources().getConfiguration().mnc);

        LoggerUtils.d("mcc:" + sMcc);
        LoggerUtils.d("mnc:" + sMnc);

        return true;
    }

    /**
     * 获取apn名称
     */
    public static String getName() {
        return sName;
    }

    /**
     * 设置apn名称
     */
    public static void setName(String nameString) {
        sName = nameString;
    }


    /**
     * 获取apn
     */
    public static String getApn() {
        return sApn;
    }

    /**
     * 设置apn
     */
    public static void setApn(String apnString) {
        sApn = apnString;
    }

    /**
     * 获取mcc
     */
    public static String getMcc() {
        return sMcc;
    }

    /**
     * 设置mcc
     */
    public static void setMcc(String mccString) {
        sMcc = mccString;
    }

    /**
     * 获取mnc
     */
    public static String getMnc() {
        return sMnc;
    }

    /**
     * 设置mnc
     */
    public static void setMnc(String mncString) {
        sMnc = mncString;
    }

}
