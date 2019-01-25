package com.utils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nlutils.util.LoggerUtils;

/**
 * 输入法工具类
 *
 * @author CB
 * @date 2014/7/2
 */
public class InputUtils {

    public static final int STATUS_OPEN = 1;
    public static final int STATUS_CLOSE = 0;


    /**
     * 显示虚拟键盘
     *
     * @param v 视图对象
     */
    public static void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 设置软键盘状态
     *
     * @param tvSearchKey 编辑框
     * @param status      状态
     */
    public static void setKeyBoardStatus(final EditText tvSearchKey, final int status) {
        ScheduledThreadPoolExecutor stp = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "setKeyBoardStatus #");
            }
        });
        stp.schedule(new Runnable() {

            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) tvSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (m == null) {
                    return;
                }
                if (status == STATUS_OPEN) {
                    m.showSoftInput(tvSearchKey, InputMethodManager.SHOW_FORCED);
                } else {
                    m.hideSoftInputFromWindow(tvSearchKey.getWindowToken(), 0);
                }
            }
        }, 300, TimeUnit.MILLISECONDS);
    }

    /**
     * 通过定时器强制隐藏虚拟键盘
     *
     * @param v 键盘关联控件
     */
    public static void timerHideKeyboard(final View v) {
        ScheduledThreadPoolExecutor stp = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "TimeHideKeyBoard #");
            }
        });
        stp.schedule(new Runnable() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
            }
        }, 10, TimeUnit.MILLISECONDS);
    }

    /**
     * 判断输入法是否显示
     *
     * @param edittext 输入框
     * @return 是否显示
     */
    public static boolean isShowKeyBoard(EditText edittext) {
        InputMethodManager imm = (InputMethodManager) edittext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.isActive();
    }

    /**
     * 设置最大值
     *
     * @param editText  编辑框
     * @param maxLength 长度
     */
    public static void setMaxLength(EditText editText, int maxLength) {
        if (editText != null) {
            InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
            editText.setFilters(filters);
        }
    }

    /**
     * 不使用键盘（在弹出键盘前使用）,onCreatt
     *
     * @param editText 关联控件
     */
    public static void alwaysHideKeyBoard(EditText editText, int inputType) {
        if (editText != null) {
            editText.setInputType(inputType);
            editText.setTextIsSelectable(true);
        }
    }

    /**
     * 完全隐藏虚拟键盘。在onCreate使用。范围:整个activity
     *
     * @param window window
     */
    public static void hideKeyboard(Window window) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    /**
     * 如果键盘打开，则隐藏虚拟键盘。在onCreate使用。范围:整个activity
     *
     * @param activity 组件
     */
    public static void hideKeyboard(Activity activity) {
        // 判断隐藏软键盘是否弹出
        Window window = activity.getWindow();
        LoggerUtils.e("softInputMode1:" + window.getAttributes().softInputMode);
        if (window.getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
            // 隐藏软键盘
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    /**
     * 将弹出键盘关闭
     *
     * @param v 键盘关联控件
     */
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (Objects.requireNonNull(imm).isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    /**
     * editText失去焦点，隐藏系统输入法
     *
     * @param editText 输入框
     */
    public static void hideSystemInput(EditText editText) {
        int sdkCur = Build.VERSION.SDK_INT;
        if (sdkCur >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            try {
                Method method = editText.getClass().getDeclaredMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
