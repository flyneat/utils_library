package com.newland.payment.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.device.service.helper.system.SystemModule;


import com.newland.payment.R;
import com.newland.payment.ui.adapter.MenuSelectAdapter;
import com.newland.payment.ui.listener.OnListDialogClickListener;
import com.nlutils.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通用列表选择dialog
 *
 * @author CB
 * @date 2015/5/19
 */
public class CommonListDialog extends Dialog {

    /**
     * 列表
     */
    @BindView(R.id.lv_select)
    ListView lv;
    /**
     * 标题
     */
    @BindView(R.id.tv_title)
    TextView tvTitle;
    /**
     * 取消按钮
     */
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    /**
     * 确定按钮
     */
    @BindView(R.id.tv_sure)
    TextView tvSure;

    private View view;
    private OnListDialogClickListener listenerSure;
    private View.OnClickListener listenerCancel;
    private MenuSelectAdapter mAdapter;

    public CommonListDialog(Context context, int title, String[] items, OnListDialogClickListener listenerSure) {
        this(context, context.getString(title), items, 0, null, listenerSure, null);
    }

    public CommonListDialog(Context context, String title, String[] items, int defaultSelected, AdapterView.OnItemClickListener itemSelectListener, OnListDialogClickListener listenerSure, View.OnClickListener listenerCancel) {
        super(context, R.style.common_full_dialog);
        view = View.inflate(context, R.layout.common_select_view, null);
        ButterKnife.bind(this, view);
        if (!StringUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        mAdapter = new MenuSelectAdapter(context, StringUtils.stringsToList(items));
        mAdapter.setSelfOnItemClickListener(itemSelectListener);

        this.listenerSure = listenerSure;
        this.listenerCancel = listenerCancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);

        initData();
        initEvent();
    }


    private void initData() {
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            SystemModule.catchHomeButton(window);
        }
        lv.setAdapter(mAdapter);
    }

    private void initEvent() {

        //屏蔽HOME
        setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dg, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_HOME;

            }
        });
        //确定按钮
        tvSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (listenerSure != null) {
                    listenerSure.onClick(mAdapter.getCheckPosition());
                }
            }
        });
        //取消按钮
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (listenerCancel != null) {
                    listenerCancel.onClick(v);
                }
            }
        });
    }

    /**
     * 更新选中项
     *
     * @param position 选择item位置
     */
    public void setCheck(int position) {
        mAdapter.setCheck(position);
    }
}
