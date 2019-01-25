package com.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Android大小单位转换工具类
 *
 * @author wader
 */
public class DisplayUtils {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context 上下文
     * @param pxValue 像素
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context  上下文
     * @param dipValue dp
     * @return 像素
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context 上下文
     * @param pxValue 像素
     * @return dp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context 上下文
     * @param spValue sp
     * @return 像素
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context 上下文
     * @param pxValue 像素
     * @return dp
     */
    public static int px2dip(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context  上下文
     * @param dipValue dp
     * @return 像素
     */
    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context 上下文
     * @param pxValue 像素
     * @return sp
     */
    public static int px2sp(Context context, int pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context 上下文
     * @param spValue sp
     * @return 像素
     */
    public static int sp2px(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取dimens数值并转化为px
     *
     * @param context 上下文
     * @param res     空间id
     * @return 像素
     */
    public static int getDimensPx(Context context, int res) {
        return dip2px(context, context.getResources().getDimension(res));
    }

    /**
     * 屏幕信息
     *
     * @param context 上下文
     * @return 返回屏幕宽高，密度等相关信息
     */
    public static DisplayMetrics getDiaplay(Context context) {
        // 获取屏幕密度
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display;
    }

    /**
     * 设置控件文本透明
     *
     * @param tvView 目标控件
     */
    public static void setLucency(TextView tvView) {
        tvView.setTextColor(Color.argb(128, 255, 255, 255));
    }

    /**
     * * 判断横竖屏
     *
     * @param context 上下文
     * @return 1：竖 | 0：横
     */
    public static int getScreenType(Context context) {
        DisplayMetrics displayMetrics = getDiaplay(context);
        return displayMetrics.heightPixels - displayMetrics.widthPixels > 0 ? 1 : 0;
    }
}
