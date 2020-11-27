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
public class TeleFocusHelper {

    private static final long ACTION_DELAY_TIME = 1000L;        //延时调焦时间

    private static final int VALUE_FOCUS_BACK_DOWN = 10;        //向后调焦按下
    private static final int VALUE_FOCUS_BACK_UP = 20;          //向后调焦抬起
    private static final int VALUE_FOCUS_BACK_DELAY = 15;       //向后调焦延时

    private static final int VALUE_FOCUS_FRONT_DOWN = 90;       //向前调焦按下
    private static final int VALUE_FOCUS_FRONT_UP = 80;         //向前调焦抬起
    private static final int VALUE_FOCUS_FRONT_DELAY = 85;      //向前调焦延时

    private enum Type {
        USB, WIFI
    }

    private Type type;
    private Handler handler;
    private UsbCameraController usbCameraController;
    private WifiCameraController wifiCameraController;
    private DelayActionRunnable delayActionRunnable;

    public TeleFocusHelper(UsbCameraController usbCameraController) {
        this.usbCameraController = usbCameraController;
        type = Type.USB;
        handler = new Handler();
    }

    public TeleFocusHelper(WifiCameraController wifiCameraController) {
        this.wifiCameraController = wifiCameraController;
        type = Type.WIFI;
        handler = new Handler();
    }

    /**
     * 按下按钮
     *
     * @param isBack 是否向后
     */
    public void startPress(boolean isBack) {
        int value = isBack ? VALUE_FOCUS_BACK_DOWN : VALUE_FOCUS_FRONT_DOWN;
        switch (type) {
            case USB:
                setUsbFocus(value);
                break;
            case WIFI:
                setWifiFocus(value);
                break;
        }
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
        switch (type) {
            case USB:
                setUsbFocus(value);
                break;
            case WIFI:
                setWifiFocus(value);
                break;
        }
        if (delayActionRunnable != null) {
            handler.removeCallbacks(delayActionRunnable);
            delayActionRunnable = null;
        }
    }

    /**
     * 设置USB调焦
     *
     * @param value 调焦值
     */
    private void setUsbFocus(int value) {
        if (usbCameraController != null && usbCameraController.isPreviewing()) {
            usbCameraController.setParam(UVCConfig.TAG_PARAM_FOCUS, value);
        }
    }

    /**
     * 设置WiFi调焦
     *
     * @param value 调焦值
     */
    private void setWifiFocus(int value) {
        if (wifiCameraController != null && wifiCameraController.isPreviewing()) {
            wifiCameraController.setParam(WifiConfig.TAG_PARAM_FOCUS, value);
        }
    }

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
            switch (type) {
                case USB:
                    setUsbFocus(value);
                    break;
                case WIFI:
                    setWifiFocus(value);
                    break;
            }
        }
    }
}
