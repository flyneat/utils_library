package com.newland.base;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import android.app.Activity;

import com.nlutils.util.LoggerUtils;

/**
 * Activity管理器
 *
 * @author CB
 * @date 2014/10/27
 */
public class ActivityManager {

    /**
     * 保存所有的activity，在程序退出的时候集中销毁，完全退出程序
     */
    private static List<Activity> sActivitys = new LinkedList<>();

    /**
     * 添加Activity到容器中
     */
    public static void addActivity(Activity activity) {
        sActivitys.add(activity);
    }

    /**
     * 销毁某个activity
     */
    public static void delActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            //销毁界面
            LoggerUtils.d("remove:" + activity);
            activity.finish();
        }
        sActivitys.remove(activity);
    }

    /**
     * 获取栈顶的activity
     */
    public static Activity getTopActivity() {
        Activity activity = null;
        if (sActivitys != null && sActivitys.size() > 0) {
            activity = sActivitys.get(sActivitys.size() - 1);
        }

        return activity;
    }

    /**
     * 结束(finish)所有的activity
     */
    public static void finishAllActivity() {
        //清空Activity
        Iterator<Activity> it = sActivitys.iterator();
        while (it.hasNext()) {
            Activity activity = it.next();
            if (!activity.isFinishing()) {
                //退出的时候，需要销毁之前所有的界面，这样做主要是为了清理缓存的界面，更时将数据更新为新登录的用户,并不是程序完全退出
                LoggerUtils.d("finish:" + activity);
                activity.finish();
            }
            it.remove();
        }
    }

    public static void finishAllActivityExcludeActivity(String classSimpleName) {
        //清空Activity
        Iterator<Activity> it = sActivitys.iterator();
        while (it.hasNext()) {
            Activity activity = it.next();
            if (!activity.getClass().getSimpleName().equals(classSimpleName)) {
                activity.finish();
                it.remove();
            }
        }
    }

    /**
     * 返回当前activity的数量
     */
    public static int size() {
        return sActivitys.size();
    }
}
