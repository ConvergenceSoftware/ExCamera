package com.convergence.excamera.sdk.tele;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 望远相机调焦助手
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-25
 * @Organization Convergence Ltd.
 */
public abstract class TeleFocusHelper {

    /**
     * 延时调焦时间
     */
    private static final long ACTION_DELAY_TIME = 1000L;

    /**
     * 向后调焦按下
     */
    private static final int VALUE_FOCUS_BACK_DOWN = 10;
    /**
     * 向后调焦抬起
     */
    private static final int VALUE_FOCUS_BACK_UP = 20;
    /**
     * 向后调焦延时
     */
    private static final int VALUE_FOCUS_BACK_DELAY = 15;

    /**
     * 向前调焦按下
     */
    private static final int VALUE_FOCUS_FRONT_DOWN = 90;
    /**
     * 向前调焦抬起
     */
    private static final int VALUE_FOCUS_FRONT_UP = 80;
    /**
     * 向前调焦延时
     */
    private static final int VALUE_FOCUS_FRONT_DELAY = 85;

    private final Object syncAF = new Object();
    private final Handler handler;
    private final TeleFocusCallback callback;
    protected final ThreadPoolExecutor executor;
    private DelayActionRunnable delayActionRunnable;
    private boolean isAFBack = false;

    protected TeleFocusHelper(TeleFocusCallback callback) {
        this.callback = callback;
        handler = new Handler();
        executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), r -> new Thread(r, "TeleAutoFocus"));
    }

    /**
     * 按下按钮
     *
     * @param isBack 是否向后
     */
    public void startPress(boolean isBack) {
        int value = isBack ? VALUE_FOCUS_BACK_DOWN : VALUE_FOCUS_FRONT_DOWN;
        setFocus(false, value);
        delayActionRunnable = new DelayActionRunnable(isBack);
        handler.postDelayed(delayActionRunnable, ACTION_DELAY_TIME);
    }

    /**
     * 停止按压
     *
     * @param isBack 是否向后
     */
    public void stopPress(boolean isBack) {
        int value = isBack ? VALUE_FOCUS_BACK_UP : VALUE_FOCUS_FRONT_UP;
        setFocus(false, value);
        if (delayActionRunnable != null) {
            handler.removeCallbacks(delayActionRunnable);
            delayActionRunnable = null;
        }
    }

    /**
     * 开始自动对焦
     */
    public void startAutoFocus() {
        boolean isRunningReset = isAFRunning();
        int flag = TeleNativeLib.startAF(isAFBack);
        if (flag >= 0) {
            callback.onAFStart(isRunningReset);
        }
        processAFFlag(flag);
    }

    /**
     * 停止自动对焦
     */
    public void stopAutoFocus() {
        int flag = TeleNativeLib.stopAF();
        if (flag < 0) {
            callback.onAFStop();
            isAFBack = !isAFBack;
        }
    }

    /**
     * 判断自动对焦是否正在运行中
     *
     * @return 是否正在自动对焦
     */
    public boolean isAFRunning() {
        return TeleNativeLib.isAFRunning();
    }

    /**
     * 根据自动对焦标识进行相应的操作
     *
     * @param flag 自动对焦标识
     */
    private void processAFFlag(int flag) {
        switch (flag) {
            case TeleNativeLib.AF_FLAG_FREE:
            default:
                stopAutoFocus();
                break;
            case TeleNativeLib.AF_FLAG_BACK:
                executor.execute(new AutoFocusRunnable(true));
                break;
            case TeleNativeLib.AF_FLAG_FRONT:
                executor.execute(new AutoFocusRunnable(false));
                break;
        }
    }

    /**
     * 设置调焦
     *
     * @param isAF  是否自动对焦（Wifi BoxV2需要使用同步网络请求）
     * @param value 调焦值
     */
    protected abstract void setFocus(boolean isAF, int value);

    /**
     * 延时调焦
     */
    private class DelayActionRunnable implements Runnable {

        private final boolean isBack;

        public DelayActionRunnable(boolean isBack) {
            this.isBack = isBack;
        }

        @Override
        public void run() {
            int value = isBack ? VALUE_FOCUS_BACK_DELAY : VALUE_FOCUS_FRONT_DELAY;
            setFocus(false, value);
        }
    }

    /**
     * 自动对焦
     */
    private class AutoFocusRunnable implements Runnable {

        private final boolean isBack;

        public AutoFocusRunnable(boolean isBack) {
            this.isBack = isBack;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            setFocus(true, isBack ? VALUE_FOCUS_BACK_DOWN : VALUE_FOCUS_FRONT_DOWN);
            synchronized (syncAF) {
                Bitmap src = callback.provideAFBitmap();
                if (src == null) {
                    return;
                }
                int flag = TeleNativeLib.processBitmapAF(src);
                processAFFlag(flag);
            }
            setFocus(true, isBack ? VALUE_FOCUS_BACK_UP : VALUE_FOCUS_FRONT_UP);
            long costTime = System.currentTimeMillis() - startTime;
            Log.d("TeleAutoFocus", "jni processAf cost " + costTime + " ms");
        }
    }

    /**
     * 望远相机对焦回调
     */
    public interface TeleFocusCallback {

        /**
         * 提供图像Bitmap
         *
         * @return 图像Bitmap
         */
        Bitmap provideAFBitmap();

        /**
         * 自动对焦开始
         *
         * @param isRunningReset 是否正在运行自动对焦并恢复
         */
        void onAFStart(boolean isRunningReset);

        /**
         * 自动对焦结束
         */
        void onAFStop();
    }
}
