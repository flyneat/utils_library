package com.utils.thread;

import com.nlutils.util.LoggerUtils;

/**
 * 线程锁处理
 *
 * @author jianshengd
 * @date 2018/3/26
 */
public class ThreadLock<T> {
    private T value;
    private boolean isWake;
    private final Object waitObj = new Object();

    public void setValue(T value) {
        this.value = value;
    }


    public T getValue() {
        return value;
    }

    /**
     * 锁住当前线程
     *
     * @author jianshengd
     */
    public void threadWait() {
        synchronized (waitObj) {
            try {
                //已经被唤醒的锁不能再加锁，避免死锁
                if (!isWake) {
                    LoggerUtils.d("ThreadLock wait");
                    waitObj.wait();
                } else {
                    LoggerUtils.e("ThreadLock have bean notify");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 唤醒线程
     *
     * @author jianshengd
     */
    public void threadAwake() {
        synchronized (waitObj) {
            isWake = true;
            LoggerUtils.d("ThreadLock notify");
            waitObj.notify();
        }
    }
}
