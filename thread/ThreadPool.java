package com.utils.thread;


import com.nlutils.util.LoggerUtils;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具
 * 线程资源必须通过线程池提供，不允许在应用中自行显式创建线程。
 * 使用线程池的好处是减少在创建和销毁线程上所花的时间以及系统资源的开销，解决资源不足的问题。
 * 如果不使用线程池，有可能造成系统创建大量同类线程而导致消耗完内存或者“过度切换”的问题。
 *
 * @author jianshengd
 * @date 2018/3/7
 */
public class ThreadPool {
    private static ThreadPoolExecutor threadPoolExecutor;

    /**
     * 建立线程池
     */
    public static void createPool() {
        if (threadPoolExecutor != null) {
            LoggerUtils.e("存在未关闭的线程池---->自动关闭");
            release();
        }
//		Executors.newFixedThreadPool(10);
//        threadPoolExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, Thread.currentThread().getName());
//            }
//        });
        /*
         *  SynchronousQueue 同步队列，可无限创建线程，立即使用；
         *  LinkedBlockingQueue 链表阻塞队列，线程总数 > 在用的核心线程数时，等待核心线程退出后才执行新线程
         */
        threadPoolExecutor = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                String name = "Thread-" + System.currentTimeMillis() + " of Pool";
                return new Thread(r, name);
            }
        });
    }

    /**
     * 创建新线程
     *
     * @param runnable 线程
     * @return 线程
     */
    public static ThreadFuture newThread(Runnable runnable) {
        if (threadPoolExecutor == null) {
            LoggerUtils.e("ThreadPool isn't exist---->auto create ThreadPool");
            createPool();
        }
        return new ThreadFuture(threadPoolExecutor, runnable);
    }

    /**
     * 销毁线程池
     *
     * @return 被关闭的线程集合
     */
    public static List<Runnable> release() {
        if (threadPoolExecutor == null) {
            LoggerUtils.e("ThreadPool isn't exist");
            return null;
        }
        List<Runnable> runnables = threadPoolExecutor.shutdownNow();
        LoggerUtils.e("clear ThreadPool:" + runnables.size());
        threadPoolExecutor = null;
        return runnables;
    }


}
