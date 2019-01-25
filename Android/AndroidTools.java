package com.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.view.WindowManager;

import com.nlutils.util.LoggerUtils;


/**
 * 关于android的一些工具类<br/>
 * 包括：<br/>
 * 1.判断是否联网 {@link #isHaveInternet(Context)}　<br/>
 * 2.判断是否WIFI联网 {@link #isWifiConnect(Context)}<br/>
 * 3.判断是否TYPE_MOBILE联网 {@link #isMobileConnect(Context)} <br/>
 * 4.获取当前应用版本号 {@link #getApplicationVersionName(Context)}<br/>
 * 5.当前应用是否在后台允许 {@link #isInBackground(Context)}<br/>
 * 6.当前应用重启 {@link #reStartApp(Context)}<br/>
 * 7.获取栈顶Fragment
 * 8.获取堆栈中的所有fragment
 * 9.获取堆栈中的MainFragment对象数量
 * 10.设置是否显示系统状态栏 {@link #setStatusBarVisiable(Activity, boolean)}<br/>
 *
 * @author michael
 */
public class AndroidTools {

    private static ConnectivityManager sConnectivityManager;

    private static NetworkInfo getNetworkInfo(Context context) {
        if (sConnectivityManager == null) {
            sConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (sConnectivityManager == null) {
            return null;
        }
        return sConnectivityManager.getActiveNetworkInfo();
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否联网，无视联网方式
     *
     * @param context 上下文
     * @return 是否联网
     */
    public static boolean isHaveInternet(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * 判断是否是wifi方式联网
     *
     * @param context 上下文
     * @return WIFI是否连接成功
     */
    public static boolean isWifiConnect(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected();
    }

    /**
     * 判断是否为手机的联网方式（GPRS,UMTS等）
     *
     * @param context 上下文
     * @return 是否移动网络连接成功
     */
    public static boolean isMobileConnect(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.getType() == ConnectivityManager.TYPE_MOBILE && info.isConnected();
    }

    /**
     * 获取当前应用版本号
     *
     * @param context 上下文
     * @return 当前应用版本号，在androidMainfest.xml中配置的版本号
     */
    public static String getApplicationVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {
            version = packInfo.versionName;
        }
        return version;
    }


    /**
     * 获取verison Code
     *
     * @param context 上下文
     * @return app版本 version code
     */
    public static int getApplicationVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        int code = 0;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {
            code = packInfo.versionCode;
        }
        return code;
    }

    /**
     * 是否切换到 后台了
     *
     * @param context 上下文
     * @return false 当前应用在前台，true 当前应用在后头
     */
    public static boolean isInBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE || appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 应用立即重启
     *
     * @param context 上下文
     */
    public static void reStartApp(Context context) {
        reStartApp(context, 0);
    }

    /**
     * 应用延时重启
     *
     * @param context    上下文
     * @param startDelay 启动延迟(单位毫秒)
     */
    public static void reStartApp(Context context, long startDelay) {
        //用本应用的包名获取本应用的启动Intent
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context.getApplicationContext(), -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (mgr != null) {
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + startDelay, restartIntent);
        }
    }


    /**
     * 设置activity界面上的状态栏是否显示
     *
     * @param activity   活动Activity
     * @param isVisiable 是否显示
     */
    public static void setStatusBarVisiable(Activity activity, boolean isVisiable) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (isVisiable) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(lp);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 根据包名查找启动Activity
     *
     * @param context     上下文
     * @param packageName 目标应用包名
     * @return 主Activity 全路径名称
     */
    public static String getMainActivity(Context context, String packageName) {
        // 主启动action name
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.LAUNCHER");
        //遍历系统所有应用的主启动action name
        List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(intent, 0);
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo resolveInfo = appList.get(i);
            String packageStr = resolveInfo.activityInfo.packageName;
            //匹配目标包名是否一致
            if (packageStr.equals(packageName)) {
                //这个就是你想要的那个Activity
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    /**
     * 可用存储空间不足
     *
     * @return true 存储空间不足
     */
    public static boolean lowAvailableRomSize() {
        long space = FileUtils.getAvailableRomSize();
        LoggerUtils.d("存储空间:" + space);
        // 80M
        return space < 80 * 1024 * 1024;
    }

    /**
     * 电量不足
     */
    public static boolean lowPower(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        if (batteryStatus == null) {
            return false;
        }
        //是否在充电
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        //当前剩余电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //电量最大值
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //电量百分比
        float batteryPct = level / (float) scale;
        //低于10%并且不再充电，为电量过低
        return batteryPct <= 0.01 && !isCharging;
    }
}
