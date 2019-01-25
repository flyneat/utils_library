package com.newland.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.payment.R;
import com.newland.payment.interfaces.ThreadCallBack;
import com.newland.payment.ui.activity.MainActivity;
import com.utils.DisplayUtils;


/**
 * 自定义吐司
 *
 * @author unknow
 */
public class ToastUtils {
    //	private static Drawable sIconLeft;

    private static Toast mToastView;

    /**
     * 显示toast提示
     *
     * @param context  上下文
     * @param info     内容
     * @param duration 显示时间
     */
    @SuppressLint("InflateParams")
    private static void show(Context context, Object info, int duration) {

        if (info == null) {
            return;
        }
        if (mToastView != null) {
            mToastView.cancel();
        }
        mToastView = new Toast(context);
        View toastContentView = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        TextView toastMessageView = toastContentView.findViewById(R.id.toast_message);
//		ImageView toastIconView =  toastContentView.findViewById(R.id.toast_icon);
//		if(sIconLeft != null){
//			toastIconView.setImageDrawable(sIconLeft);
//		}
        mToastView.setDuration(duration > Toast.LENGTH_LONG ? Toast.LENGTH_LONG : duration);
        mToastView.setView(toastContentView);
        mToastView.setGravity(Gravity.TOP, 0, DisplayUtils.getDiaplay(context).heightPixels / 3);
        toastMessageView.setText(info.toString());
        mToastView.show();
    }

    /**
     * 显示toast提示
     *
     * @param context 上下文
     * @param info    内容
     */
    public static void show(Context context, Object info) {
        show(context, info, Toast.LENGTH_SHORT);
    }

    /**
     * 显示toast提示(时间长一点)
     *
     * @param context 上下文
     * @param info    内容
     */
    public static void showLong(Context context, Object info) {
        show(context, info, Toast.LENGTH_LONG);
    }

    /**
     * show
     *
     * @param context 上下文
     * @param resId   内容文本资源
     */
    public static void show(Context context, int resId) {
        show(context, context.getString(resId));
    }


    public static void showOnUIThread(final Object info) {
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    show(MainActivity.getInstance(), info);
                }
            });
        }
    }

}
