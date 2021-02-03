package com.convergence.excamera;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * @Author WangZiheng
 * @CreateDate 2021-01-06
 * @Organization Convergence Ltd.
 */
public class IApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}
