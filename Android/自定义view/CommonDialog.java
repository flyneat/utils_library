package com.newland.payment.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.device.service.helper.system.SystemModule;
import com.newland.payment.R;
import com.nlutils.util.LoggerUtils;
import com.utils.DisplayUtils;
import com.utils.ViewUtils;
import com.utils.thread.ThreadFuture;
import com.utils.thread.ThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用提示选择dialog
 *
 * @author CB
 * @date 2015/5/19
 */
public class CommonDialog extends Dialog {

    public enum TimeOutOper {
        /**
         * 不进行任何操作
         */
        NONE,
        /**
         * 取消
         */
        CANCEL,
        /**
         * 确定
         */
        SURE,
        /**
         * 中键
         */
        MIDDLE,
    }

    private View view;
    private LinearLayout llMain;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvCancel;
    private TextView tvSure;
    private TextView tvMiddle;
    private ImageView ivLine;
    private ImageView ivLineMiddle;

    private Context mContext;
    /**
     * 超时以秒为单位
     */
    private int mDefaultTimeOut = -1;
    /**
     * 当前计数器
     */
    private AtomicInteger mCountCur = new AtomicInteger(mDefaultTimeOut);
    /**
     * 超时以秒为单位
     */
    private TimeOutOper mTimeOutOper = TimeOutOper.NONE;

    CommonDialog(Context context) {
        super(context);
        this.mContext = context;

        view = View.inflate(context, R.layout.common_dialog_view, null);
        llMain = view.findViewById(R.id.ll_main);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvSure = view.findViewById(R.id.tv_sure);
        tvMiddle = view.findViewById(R.id.tv_middle);
        ivLine = view.findViewById(R.id.iv_line);
        ivLineMiddle = view.findViewById(R.id.iv_line_middle);
    }

    public CommonDialog(Context context, int timeOut, TimeOutOper timeOutOper) {
        this(context);
        mDefaultTimeOut = timeOut;
        mCountCur = new AtomicInteger(mDefaultTimeOut);
        this.mTimeOutOper = timeOutOper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);

        //关闭软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setCancelable(false);
        SystemModule.catchHomeButton(getWindow());
        setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dg, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_HOME;
            }
        });

        this.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                view.postInvalidate();
                startCountDown();
            }
        });
    }

    /**
     * 取消按钮监听器
     *
     * @param cancelText     取消按钮名称
     * @param listenerCancel 取消监听器
     */
    public void setCancelListener(String cancelText, final View.OnClickListener listenerCancel) {

        if (listenerCancel == null) {
            ivLine.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(cancelText);
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastClick()) {
                        return;
                    }
                    dismiss();
                    listenerCancel.onClick(v);
                }
            });
        }
    }

    public void setCancelListener(String cancelText, final View.OnClickListener listenerCancel, int size) {

        if (listenerCancel == null) {
            ivLine.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(cancelText);
            tvCancel.setTextSize(size);
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastClick()) {
                        return;
                    }
                    dismiss();
                    listenerCancel.onClick(v);
                }
            });
        }
    }

    /**
     * 中间按钮监听器
     *
     * @param middelText     中间按钮名称
     * @param listenerMiddle 中间按钮监听器
     */
    public void setMiddleListener(String middelText, final View.OnClickListener listenerMiddle) {

        if (listenerMiddle == null) {
            ivLineMiddle.setVisibility(View.GONE);
            tvMiddle.setVisibility(View.GONE);
        } else {
            ivLineMiddle.setVisibility(View.VISIBLE);
            tvMiddle.setVisibility(View.VISIBLE);
            tvMiddle.setText(middelText);
            tvMiddle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastClick()) {
                        return;
                    }
                    dismiss();
                    listenerMiddle.onClick(v);
                }
            });
        }
    }

    /**
     * 确定按钮监听器
     *
     * @param suerText     确认按钮名称
     * @param listenerSure 确认监听器
     */
    public void setSureListener(String suerText, final View.OnClickListener listenerSure) {

        if (listenerSure == null) {
            ivLine.setVisibility(View.GONE);
            tvSure.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            tvSure.setVisibility(View.VISIBLE);
            tvSure.setText(suerText);
            tvSure.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastClick()) {
                        return;
                    }
                    dismiss();
                    listenerSure.onClick(v);
                }
            });
        }
    }

    /**
     * 确定按钮监听器
     *
     * @param suerText     确认按钮名称
     * @param listenerSure 确认监听器
     * @param size         确认按钮文字大小
     */
    public void setSureListener(String suerText, final View.OnClickListener listenerSure, int size) {

        if (listenerSure == null) {
            ivLine.setVisibility(View.GONE);
            tvSure.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            tvSure.setVisibility(View.VISIBLE);
            tvSure.setText(suerText);
            tvSure.setTextSize(size);
            tvSure.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastClick()) {
                        return;
                    }
                    dismiss();
                    listenerSure.onClick(v);
                }
            });
        }
    }

    private ThreadFuture threadFuture = null;

    private void startCountDown() {
        synchronized (this) {
            if (threadFuture != null) {
                threadFuture.cancel(true);
                threadFuture = null;
            }
            if (mDefaultTimeOut < 0) {
                return;
            }
            threadFuture = ThreadPool.newThread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!this.equals(threadFuture.getRunnable())) {
                            LoggerUtils.d("countDownThread not equal this");
                            return;
                        }
                        int now = mCountCur.getAndDecrement();
                        if (now > 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                LoggerUtils.d("countDownThread InterruptedException now:" + now);
                                break;
                            }
                        } else {
                            if (now == 0) {
                                onTimeOut();
                            }
                            break;
                        }
                    }
                }
            });
            threadFuture.start();
        }
    }

    private void stopCountDown() {
        synchronized (this) {
            if (threadFuture != null) {
                threadFuture.cancel(true);
                threadFuture = null;
            }
        }
    }

    private void onTimeOut() {
        LoggerUtils.d("CommonDialog onTimeOut");
        //must be run on main thread ??
        view.post(new Runnable() {
            @Override
            public void run() {
                switch (mTimeOutOper) {
                    case CANCEL:
                        if (tvCancel.getVisibility() == View.VISIBLE) {
                            tvCancel.callOnClick();
                        }
                        break;
                    case MIDDLE:
                        if (tvMiddle.getVisibility() == View.VISIBLE) {
                            tvMiddle.callOnClick();
                        }
                        break;
                    case SURE:
                        if (tvSure.getVisibility() == View.VISIBLE) {
                            tvSure.callOnClick();
                        }
                        break;
                    case NONE:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void dismiss() {
        stopCountDown();
        super.dismiss();
    }

    public void setTitle(String title) {
        if (title != null) {
            tvTitle.setText(title);
        }
    }

    public void setContent(String content) {
        if (content != null) {
            tvContent.setText(content);
        }

    }

    /**
     * 设置dialog的宽度
     *
     * @param resDp 资源文件中dp的源
     */
    public void setWidth(int resDp) {
        FrameLayout.LayoutParams params = (LayoutParams) llMain.getLayoutParams();
        params.width = DisplayUtils.getDimensPx(mContext, resDp);
    }

}
