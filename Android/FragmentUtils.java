package com.newland.base;

import android.support.v4.app.Fragment;

import com.newland.payment.ui.activity.BaseActivity;
import com.newland.payment.ui.fragment.BaseFragment;
import com.newland.payment.ui.fragment.MainFragment;

import java.util.List;

/**
 * Frament工具类
 *
 * @author jianshengd
 * @date 2018/3/29
 */

public class FragmentUtils {
    /**
     * 获取指定acivity中fragment栈顶的fragment
     *
     * @param acitivy 指定acivity
     * @return 顶层fragment
     */
    public static Fragment getTopFragment(BaseActivity acitivy) {
        List<Fragment> fragments = acitivy.getSupportFragmentManager().getFragments();
        if (fragments == null || fragments.isEmpty()) {
            return null;
        }
        int size = fragments.size();
        BaseFragment fragment = null;
        for (int i = size - 1; i >= 0; i--) {
            fragment = (BaseFragment) fragments.get(i);
            if (fragment != null) {
                break;
            }
        }
        return fragment;
    }

    /**
     * 获取指定activity的fragment堆栈中的所有fragment
     *
     * @param fragmentActivity 容器activity
     * @return 堆栈中的所有fragment
     */
    public static List<Fragment> getFragments(BaseActivity fragmentActivity) {
        return fragmentActivity.getSupportFragmentManager().getFragments();
    }

    /**
     * 获取堆栈中的MainFragment对象数量
     *
     * @param fragmentActivity 容器activity
     * @return 主界面
     */
    public static int getMainFragmentsCount(BaseActivity fragmentActivity) {

        int count = 0;
        List<Fragment> fragments = fragmentActivity.getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            int size = fragments.size();
            BaseFragment fragment;
            for (int i = size - 1; i >= 0; i--) {
                fragment = (BaseFragment) fragments.get(i);
                if (fragment instanceof MainFragment) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取堆栈中第一个MainFragment离栈顶距离
     *
     * @param fragmentActivity 容器activity
     * @return 主界面
     */
    public static int getFirstMainFragmentPosition(BaseActivity fragmentActivity) {

        int count = 0;
        List<Fragment> fragments = fragmentActivity.getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            int size = fragments.size();
            BaseFragment fragment;
            for (int i = size - 1; i >= 0; i--) {
                fragment = (BaseFragment) fragments.get(i);
                if (fragment instanceof MainFragment) {
                    count = size - (i + 1);
                }
            }
        }
        return count;
    }

}
