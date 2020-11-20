package com.convergence.excamera.sdk.common;

import android.util.Log;

/**
 * 日志打印工具类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class CameraLogger {

    public static final String TAG_DEFAULT = "CVGC_CAMERA";

    private String tag = TAG_DEFAULT;
    private boolean isDebug = true;

    private CameraLogger(String tag, boolean isDebug) {
        this.tag = tag;
        this.isDebug = isDebug;
    }

    public static CameraLogger create() {
        return create(TAG_DEFAULT);
    }

    public static CameraLogger create(String tag) {
        return create(tag, true);
    }

    public static CameraLogger create(String tag, boolean isDebug) {
        return new CameraLogger(tag, isDebug);
    }
    public void LogV(String msg) {
        if (isDebug){
            LogV(tag, msg);
        }
    }
    public void LogD(String msg) {
        if (isDebug){
            LogD(tag, msg);
        }
    }
    public void LogI(String msg) {
        if (isDebug){
            LogI(tag, msg);
        }
    }
    public void LogW(String msg) {
        if (isDebug){
            LogW(tag, msg);
        }
    }
    public void LogE(String msg) {
        if (isDebug){
            LogE(tag, msg);
        }
    }

    public static void LogV(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void LogD(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void LogI(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void LogW(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void LogE(String tag, String msg) {
        Log.e(tag, msg);
    }
}
