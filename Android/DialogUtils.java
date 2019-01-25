package com.newland.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

import com.data.mvc.dao.UserDao;
import com.data.mvc.dao.impl.UserDaoImpl;
import com.data.mvc.model.User;
import com.data.mvc.service.UserService;
import com.data.mvc.service.impl.UserServiceImpl;
import com.payment.appconst.params.ParamsConst;
import com.newland.payment.R;
import com.newland.payment.interfaces.ThreadCallBack;
import com.newland.payment.ui.listener.LongValueChangeListener;
import com.newland.payment.ui.listener.OnListDialogClickListener;
import com.newland.payment.ui.view.CommonDialog;
import com.newland.payment.ui.view.CommonDialog.TimeOutOper;
import com.newland.payment.ui.view.CommonInputDialog;
import com.newland.payment.ui.view.CommonListDialog;
import com.newland.payment.ui.view.OperatorPasswordDialog;
import com.newland.payment.ui.view.OperatorPasswordDialog.InputEventListener;
import com.newland.payment.ui.view.wheelview.DateWheelView2;
import com.newland.payment.ui.view.wheelview.TimeWheelView;
import com.data.mvc.model.constant.user.UserNo;
import com.data.mvc.model.constant.user.UserType;
import com.utils.ParamsUtils;

/**
 * 通用对话框接口
 *
 * @author unknow
 */
public class MessageUtils {

    /**
     * 显示信息提示框，一个按键
     *
     * @param context 设备上下文
     * @param tips    提示字符串
     */
    public static CommonDialog showCommonDialog(Context context, String tips) {
        CommonDialog commonDialog = getCommonDialog(context, null, tips, context.getString(R.string.common_sure), null, null, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            }
        }, null, null, -1, TimeOutOper.NONE);
        commonDialog.show();
        return commonDialog;
    }

    /**
     * 显示信息提示框，有两个按键
     *
     * @param tips         提示字符串
     * @param listenerSure 确认监听器
     */
    public static CommonDialog showCommonDialog(Context context, String tips, OnClickListener listenerSure) {
        final CommonDialog commonDialog = getCommonDialog(context, null, tips, context.getString(R.string.common_sure), null, context.getString(R.string.common_cancel), listenerSure, null, new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        }, -1, TimeOutOper.NONE);
        commonDialog.show();
        return commonDialog;
    }

    /**
     * 显示信息提示框，有两个按键
     *
     * @param context        上下文
     * @param tips           提示信息
     * @param listenerSure   确认按钮
     * @param listenerCancel 取消按钮
     */
    public static CommonDialog showCommonDialog(Context context, String tips, OnClickListener listenerSure, OnClickListener listenerCancel) {
        final CommonDialog commonDialog = getCommonDialog(context, null, tips, context.getString(R.string.common_sure), null, context.getString(R.string.common_cancel), listenerSure, null, listenerCancel, -1, TimeOutOper.NONE);
        commonDialog.show();
        return commonDialog;
    }

    /**
     * 显示信息提示框，带标题，两个按钮
     *
     * @param context        上下文
     * @param title          标题
     * @param tips           提示信息
     * @param listenerSure   确认监听
     * @param listenerCancel 取消监听
     */
    public static CommonDialog showCommonDialog(Context context, String title, String tips, OnClickListener listenerSure, OnClickListener listenerCancel) {
        final CommonDialog commonDialog = getCommonDialog(context, title, tips, context.getString(R.string.common_sure), null, context.getString(R.string.common_cancel), listenerSure, null, listenerCancel, -1, TimeOutOper.NONE);
        commonDialog.show();
        return commonDialog;
    }

    /**
     * 显示信息提示框，不带标题，两个按钮，并且按钮文本可设置
     *
     * @param context        上下文
     * @param tips           提示信息
     * @param listenerSure   确认监听
     * @param listenerCancel 取消监听
     */
    public static CommonDialog showCommonDialog(Context context, String tips, String sureText, String cancelText, OnClickListener listenerSure, OnClickListener listenerCancel) {
        final CommonDialog commonDialog = getCommonDialog(context, null, tips, sureText, null, cancelText, listenerSure, null, listenerCancel, -1, TimeOutOper.NONE);
        commonDialog.show();
        return commonDialog;
    }

    /**
     * 显示主管和安全密码验证，验证通过弹出提示框
     *
     * @param context             上下文
     * @param tips                提示字符串
     * @param isUseSafePassword   安全密码验证功能
     * @param isUseManagePassword 带主管密码验证功能
     */
    public static CommonDialog showCommonDialog(Context context, String tips, boolean isUseSafePassword, boolean isUseManagePassword, OnClickListener listenerSure) {

        CommonDialog commonDialog = showCommonDialog(context, null, tips, isUseSafePassword, isUseManagePassword, context.getString(R.string.common_sure), null, context.getString(R.string.common_cancel), listenerSure, null, new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }, -1, TimeOutOper.NONE);
        return commonDialog;
    }

    /**
     * 显示主管和安全密码验证，验证通过弹出提示框
     *
     * @param context             上下文
     * @param title               标题
     * @param tips                提示字符串
     * @param isUseSafePassword   安全密码验证功能
     * @param isUseManagePassword 主管密码验证功能
     * @param sureText            确认按钮名称
     * @param middelText          中间按钮名称
     * @param cancelText          取消按钮名称
     * @param listenerSure        确认键监听
     * @param listenerMiddle      中间按钮监听器
     * @param listenerCancel      取消键监听
     * @param timeOut             超时
     */
    public static CommonDialog showCommonDialog(final Context context, String title, String tips, boolean isUseSafePassword, boolean isUseManagePassword, String sureText, String middelText, String cancelText, OnClickListener listenerSure, OnClickListener listenerMiddle, OnClickListener listenerCancel, int timeOut, TimeOutOper timeOutOper) {
        final CommonDialog commonDialog = getCommonDialog(context, title, tips, sureText, middelText, cancelText, listenerSure, listenerMiddle, listenerCancel, timeOut, timeOutOper);
        OperatorPasswordDialog operatorPasswordDialog;
        if (isUseSafePassword) {
            operatorPasswordDialog = new OperatorPasswordDialog(context, R.style.swiping_dialog, 6, R.string.pls_input_safe_password, new InputEventListener() {
                @Override
                public void onConfirm(Dialog dialog, String value) {
                    OperatorPasswordDialog operatorPasswordDialog = (OperatorPasswordDialog) dialog;
                    if (operatorPasswordDialog.getPassword().equals(ParamsUtils.getString(ParamsConst.PARAMS_KEY_SAFE_PASSWORD))) {
                        operatorPasswordDialog.dismiss();
                        commonDialog.show();
                    } else {
                        operatorPasswordDialog.dismiss();
                        ToastUtils.show(context, R.string.error_password);
                    }
                }

                @Override
                public void onCancel() {

                }

            });
            operatorPasswordDialog.show();
        } else if (isUseManagePassword) {

            operatorPasswordDialog = new OperatorPasswordDialog(context, R.style.swiping_dialog, 6, R.string.pls_input_main_password, new InputEventListener() {

                @Override
                public void onConfirm(Dialog dialog, String value) {
                    final OperatorPasswordDialog operatorPasswordDialog = (OperatorPasswordDialog) dialog;

                    new CommonThread(new ThreadCallBack() {
                        boolean result = false;

                        @Override
                        public void onBackGround() {
                            /* 源码：
                            * UserService service = new UserServiceImpl(context);
                            * role = service.checkLogin(UserNo.MANAGER, operatorPasswordDialog.getPassword());
                            */
                            String inputPwd = operatorPasswordDialog.getPassword();
                            UserDao userDao = new UserDaoImpl(context);
                            User managerUser = userDao.findByUserType(UserType.MANAGER).get(0);
                            if (inputPwd.equals(managerUser.getPassword())) {
                                result = true;
                            }
                        }

                        @Override
                        public void onMain() {
                            operatorPasswordDialog.dismiss();
                            if (result) {
                                commonDialog.show();
                            } else {
                                ToastUtils.show(context, R.string.error_password);
                            }
                        }

                    }).start();


                }

                @Override
                public void onCancel() {

                }

            });
            operatorPasswordDialog.show();
        } else {
            commonDialog.show();
        }
        return commonDialog;
    }


    /**
     * 显示信息提示框(默认三个按钮，不需要显示的按钮则将listener传空即可)
     *
     * @param title          提示信息
     * @param tips           确认按钮文件资源id
     * @param listenerSure   确认按钮监听
     * @param listenerMiddle 中间按钮监听
     * @param listenerCancel 取消按钮监听
     */
    private static CommonDialog getCommonDialog(Context context, String title, String tips, String sureText, String middelText, String cancelText, OnClickListener listenerSure, OnClickListener listenerMiddle, OnClickListener listenerCancel, int timeOut, TimeOutOper timeOutOper) {

        final CommonDialog commonDialog = new CommonDialog(context, timeOut, timeOutOper);
        if (title != null) {
            commonDialog.setTitle(title);
        }
        commonDialog.setContent(tips);
        commonDialog.setSureListener(sureText, listenerSure);
        commonDialog.setCancelListener(cancelText, listenerCancel);
        commonDialog.setMiddleListener(middelText, listenerMiddle);
        return commonDialog;
    }


    /**
     * 显示菜单选择对话框
     *
     * @param title              提示框标题
     * @param items              菜单数据源
     * @param defaultSelected    默认选择索引
     * @param itemSelectListener 选项选中监听
     * @param listenerSure       确认监听
     * @param listenerCancel     取消监听
     */
    public static Dialog showMenuSelectDialog(Context context, String title, String[] items, int defaultSelected, AdapterView.OnItemClickListener itemSelectListener, OnListDialogClickListener listenerSure, OnClickListener listenerCancel) {
        CommonListDialog dialog = new CommonListDialog(context, title, items, defaultSelected, itemSelectListener, listenerSure, listenerCancel);
        dialog.show();
        return dialog;
    }

    /**
     * 显示时间对话框(时 分 秒)
     *
     * @param title    对话框标题
     * @param date     初始化时间
     * @param listener 时间选择监听
     */
    @SuppressLint("InflateParams")
    public static Dialog showCommonTimeDialog(Context context, String title, long date, final LongValueChangeListener listener) {

        View view = LayoutInflater.from(context).inflate(R.layout.common_time_dialog, null);

        /*日期控件 */
        final TimeWheelView timeWheelView = view.findViewById(R.id.time_wheel_view);
        /*标题 */
        TextView tvTitle = view.findViewById(R.id.tv_title);
        /* 取消按钮 */
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        /*确定按钮 */
        TextView tvSure = view.findViewById(R.id.tv_sure);

        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(view);

        tvTitle.setText(title);
        timeWheelView.setDate(date);

        tvCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        tvSure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onChange(timeWheelView.getDate());
                }
            }
        });

        return dialog;
    }

    /**
     * 显示日期对话框(年 月 日)
     *
     * @param context  上下文
     * @param title    对话框标题
     * @param listener 日期选择监听器
     */
    public static Dialog showCommonDateDialog(Context context, String title, final LongValueChangeListener listener) {
        return showCommonDateDialog(context, title, System.currentTimeMillis(), 1950, 2050, true, true, true, listener);
    }

    /**
     * 显示日期对话框(年 月 日)
     *
     * @param context     上下文
     * @param title       对话框标题
     * @param date        初始化对话框中日期
     * @param isShowYear  是否显示年
     * @param isShowMouth 是否显示月
     * @param isShowDay   是否显示日
     * @param listener    日期选择监听
     */
    @SuppressLint("InflateParams")
    private static Dialog showCommonDateDialog(Context context, String title, Long date, int minYear, int maxYear, boolean isShowYear, boolean isShowMouth, boolean isShowDay, final LongValueChangeListener listener) {

        View view = LayoutInflater.from(context).inflate(R.layout.common_date_dialog, null);

        /*日期控件 */
        final DateWheelView2 dateWheelView = view.findViewById(R.id.date_wheel_view);
        /*标题 */
        TextView tvTitle = view.findViewById(R.id.tv_title);
        /*取消按钮 */
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        /*确定按钮 */
        TextView tvSure = view.findViewById(R.id.tv_sure);

        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(view);

        tvTitle.setText(title);
        if (date != null) {
            dateWheelView.setDate(date);
        }
        dateWheelView.setIsShowYear(isShowYear);
        dateWheelView.setIsShowMouth(isShowMouth);
        dateWheelView.setIsShowDay(isShowDay);
        dateWheelView.setAvailableYear(minYear, maxYear);

        tvCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        tvSure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onChange(dateWheelView.getDate());
                }
            }
        });

        return dialog;
    }

    /**
     * 显示输入弹出窗
     */
    public static CommonInputDialog showInputDialog(Context context, int resTitle, String content, int inputType, OnClickListener sureListener) {
        CommonInputDialog dialog = new CommonInputDialog(context, inputType);
        dialog.setTitle(resTitle);
        dialog.setContent(content);
        dialog.setSureListener(R.string.common_sure, sureListener);
        dialog.setCancelListener(R.string.common_cancel, new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();
        return dialog;
    }

}
