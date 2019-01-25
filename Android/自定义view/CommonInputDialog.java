package com.newland.payment.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.device.service.helper.system.SystemModule;
import com.utils.DisplayUtils;
import com.utils.InputUtils;
import com.newland.payment.R;
import com.nlutils.util.LoggerUtils;

/**
 * 通用输入dialog
 *
 * @author CB
 * @date 2015/5/19
 */
public class CommonInputDialog extends Dialog {

    private View view;
    private LinearLayout llMain;
    private TextView tvTitle;
    private EditText etContent;
    private TextView tvCancel;
    private TextView tvSure;
    private TextView tvMiddle;
    private ImageView ivLine;
    private ImageView ivLineMiddle;

    private Context mContext;

    public CommonInputDialog(Context context, int inputType) {
        super(context, R.style.common_full_dialog);
        this.mContext = context;

        view = View.inflate(context, R.layout.common_input_dialog_view, null);
        llMain = view.findViewById(R.id.ll_main);
        tvTitle = view.findViewById(R.id.tv_title);
        etContent = view.findViewById(R.id.et_content);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvSure = view.findViewById(R.id.tv_sure);
        tvMiddle = view.findViewById(R.id.tv_middle);
        ivLine = view.findViewById(R.id.iv_line);
        ivLineMiddle = view.findViewById(R.id.iv_line_middle);
        etContent.setInputType(inputType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);

        // 关闭软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        setCancelable(false);
        SystemModule.catchHomeButton(getWindow());
        setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dg, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_HOME) {
                    // 屏蔽HOME键操作
                    return true;
                }
                return false;
            }
        });

        setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                // 当Dialog显示后，默认显示系统输入法
                InputUtils.showKeyboard(etContent);
            }
        });

    }

    /**
     * 取消按钮监听器
     *
     * @param resCancelText  取消按钮名称
     * @param listenerCancel 取消监听器
     */
    public void setCancelListener(int resCancelText, final View.OnClickListener listenerCancel) {

        if (listenerCancel == null) {
            ivLine.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(resCancelText);
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                    listenerCancel.onClick(v);
                }
            });
        }
    }

    /**
     * 中间按钮监听器
     *
     * @param resMiddelText  中间按钮名称
     * @param listenerMiddle 中间按钮监听器
     */
    public void setMiddleListener(int resMiddelText, final View.OnClickListener listenerMiddle) {

        if (listenerMiddle == null) {
            ivLineMiddle.setVisibility(View.GONE);
            tvMiddle.setVisibility(View.GONE);
        } else {
            ivLineMiddle.setVisibility(View.VISIBLE);
            tvMiddle.setVisibility(View.VISIBLE);
            tvMiddle.setText(resMiddelText);
            tvMiddle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                    listenerMiddle.onClick(v);
                }
            });
        }
    }

    /**
     * 确定按钮监听器
     *
     * @param resSureText  确认按钮名称
     * @param listenerSure 确认按钮监听器
     */
    public void setSureListener(int resSureText, final View.OnClickListener listenerSure) {

        if (listenerSure == null) {
            ivLine.setVisibility(View.GONE);
            tvSure.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            tvSure.setVisibility(View.VISIBLE);
            tvSure.setText(resSureText);
            tvSure.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                    listenerSure.onClick(v);
                }
            });
        }
    }

    @Override
    public void setTitle(int resTitle) {
        if (resTitle != -1) {
            tvTitle.setText(resTitle);
        }
    }

    public void setTitle(String title) {
        if (title != null) {
            tvTitle.setText(title);
        }
    }

    public void setContent(String content) {
        if (content != null) {
            etContent.setText(content);
        }

    }

    public void setContent(int resContent) {
        if (resContent != -1) {
            etContent.setText(resContent);
        }

    }

    public String getContent() {
        return etContent.getText().toString();
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

    /**
     * 设置最大字符
     *
     * @param maxCharacter 字符值
     */
    public void setMaxCharacterNum(final int maxCharacter) {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                LoggerUtils.i("source:" + source + ",start:" + start + ",end:" + end + ",dest:" + dest + ",dstart:" + dstart + ",dend:" + dend);

                if (getCharacterNum(source.toString() + dest + "") <= maxCharacter) {
                    return null;
                } else {
                    return "";
                }

            }
        };
        etContent.setFilters(new InputFilter[]{filter});
    }

    /**
     * 设置单行
     */
    public void setSingleLine() {
        etContent.setSingleLine();
    }

    /**
     * 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
     *
     * @param content 内容
     */
    private int getCharacterNum(final String content) {
        if (null == content || "".equals(content)) {
            return 0;
        } else {
            return (content.length() + getChineseNum(content));
        }
    }

    /**
     * 返回字符串里中文字或者全角字符的个数
     */
    private int getChineseNum(String s) {
        int num = 0;
        char[] myChar = s.toCharArray();
        for (char c : myChar) {
            if ((char) (byte) c != c) {
                num++;
            }
        }
        return num;
    }

}
