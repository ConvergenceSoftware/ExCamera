package com.convergence.excamera.sdk.common;

import android.os.Handler;

import com.convergence.excamera.sdk.usb.core.UsbCameraController;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfig;
import com.convergence.excamera.sdk.wifi.core.WifiCameraController;
import com.serenegiant.usb.config.base.UVCConfig;

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

    private Handler handler;
    private DelayActionRunnable delayActionRunnable;

    protected TeleFocusHelper() {
        handler = new Handler();
    }

    /**
     * 按下按钮
     *
     * @param isBack 是否向后
     */
    public void startPress(boolean isBack) {
        int value = isBack ? VALUE_FOCUS_BACK_DOWN : VALUE_FOCUS_FRONT_DOWN;
        setFocus(value);
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
        setFocus(value);
        if (delayActionRunnable != null) {
            handler.removeCallbacks(delayActionRunnable);
            delayActionRunnable = null;
        }
    }

    /**
     * 设置调焦
     *
     * @param value 调焦值
     */
    protected abstract void setFocus(int value);

    /**
     * 延时调焦
     */
    private class DelayActionRunnable implements Runnable {

        private boolean isBack;

        public DelayActionRunnable(boolean isBack) {
            this.isBack = isBack;
        }

        @Override
        public void run() {
            int value = isBack ? VALUE_FOCUS_BACK_DELAY : VALUE_FOCUS_FRONT_DELAY;
            setFocus(value);
        }
    }
}
