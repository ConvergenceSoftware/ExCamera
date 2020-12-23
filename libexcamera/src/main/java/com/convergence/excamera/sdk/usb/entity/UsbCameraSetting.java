package com.convergence.excamera.sdk.usb.entity;

import com.convergence.excamera.sdk.usb.core.UsbDevConnection;
import com.serenegiant.usb.Size;

import java.util.List;

/**
 * USB相机设置封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraSetting {

    private UsbDevConnection.ConnectionInfo connectionInfo;
    private UsbCameraResolution usbCameraResolution;

    private UsbCameraSetting() {
        usbCameraResolution = new UsbCameraResolution();
    }

    public static UsbCameraSetting getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final UsbCameraSetting INSTANCE = new UsbCameraSetting();
    }

    public void initConnection(UsbDevConnection.ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public void initResolution(List<Size> supportedSizes) {
        usbCameraResolution.initData(supportedSizes);
    }

    public boolean isAvailable() {
        return connectionInfo != null && usbCameraResolution.isAvailable();
    }

    public UsbDevConnection.ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public UsbCameraResolution getUsbCameraResolution() {
        return usbCameraResolution;
    }

    public void refreshCurResolution(int width, int height) {
        if (!isAvailable()) {
            return;
        }
        List<UsbCameraResolution.Resolution> resolutionList = usbCameraResolution.getResolutionList();
        for (UsbCameraResolution.Resolution resolution : resolutionList) {
            if (resolution.getWidth() == width && resolution.getHeight() == height) {
                usbCameraResolution.setCurResolution(resolution);
            }
        }
    }
}
