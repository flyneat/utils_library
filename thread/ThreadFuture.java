package com.utils.thread;


import com.nlutils.util.LoggerUtils;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 自定义线程池中线程
 *
 * @author jianshengd
 * @date 2018/4/25
 */
public class ThreadFuture {
    private Future<?> mFuture;
    private Runnable mRunnable;
    private ThreadPoolExecutor mThreadPoolExecutor;

    ThreadFuture(ThreadPoolExecutor threadPoolExecutor, Runnable runnable) {
        this.mRunnable = runnable;
        this.mThreadPoolExecutor = threadPoolExecutor;
    }

    /**
     * 执行线程
     */
    public void start() {
        if (mFuture != null) {
            LoggerUtils.e("forbid start the Thread that has been started");
            return;
        }
        if (mRunnable != null) {
            LoggerUtils.d("active thread count:" + mThreadPoolExecutor.getActiveCount());
            mFuture = mThreadPoolExecutor.submit(mRunnable);

        }
    }

    /**
     * 取消线程执行
     *
     * @param mayInterruptIfRunning 如果线程已经执行，是否允许中断
     * @return true 成功，false 失败
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mFuture != null) {
            return true;
        }
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * 现象是否已经取消
     *
     * @return true 是，false 否
     */
    public boolean isCancelled() {
        if (mFuture == null) {
            return true;
        }
        return mFuture.isCancelled();
    }

    /**
     * 现象是否已经执行完成
     *
     * @return true 是，false 否
     */
    public boolean isDone() {
        if (mFuture == null) {
            return true;
        }
        return mFuture.isDone();
    }

    public Runnable getRunnable() {
        return mRunnable;
    }
}
