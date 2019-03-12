package com.newland.vanke_acquire.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.newland.vanke_acquire.R;
import com.newland.vanke_acquire.base.App;
import com.newland.vanke_acquire.ui.activity.MainActivity;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: ZX
 * date: 2017/10/16
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    @NonNull
    private static CrashHandler INSTANCE = new CrashHandler();

    // 用于格式化日期,作为日志文件名的一部分
    @NonNull
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    private CrashHandler() {
    }

    @NonNull
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, @NonNull Throwable t) {

        Logger.e(t, t.getMessage());
        saveCrashInfo2File(App.getInstance(), t);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(App.getInstance(), R.string.crash_toast_text, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restartApp();
    }

    /**
     * 重启应用
     */
    private void restartApp() {
        Intent intent = new Intent(App.getInstance(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        App.getInstance().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());//再此之前可以做些退出等操作
    }

    /**
     * 保存错误信息到文件中
     */
    private String saveCrashInfo2File(@NonNull Context context, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            String time = formatter.format(new Date());
//            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            String packageNames = info.packageName;
            String fileName = "/crash-" + time + ".log";
            File folder = new File(context.getFilesDir(), "CrashLog");
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    Log.e(TAG, "崩溃日志文件夹创建失败！");
                }
            }

            if (folder.exists()) {
                FileOutputStream fos = new FileOutputStream(new File(folder, fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
            }

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occurred while writing file...", e);
        }

        return null;
    }
}
