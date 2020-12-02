package com.convergence.excamera.sdk.usb.core;

import android.hardware.usb.UsbDevice;

import com.convergence.excamera.sdk.usb.UsbCameraState;
import com.serenegiant.usb.USBMonitor;

/**
 * USB设备连接监听的具体实现封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbDevConnection implements USBMonitor.OnDeviceConnectListener {

    private UsbCameraCommand usbCameraCommand;
    private ConnectionInfo connectionInfo;
    private UsbCameraCommand.OnConnectListener onConnectListener;
    private boolean isPermissionRequest = false;
    private boolean isConnected = false;

    public UsbDevConnection(UsbCameraCommand usbCameraCommand) {
        this.usbCameraCommand = usbCameraCommand;
    }

    public void setOnConnectListener(UsbCameraCommand.OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public boolean isConnected() {
        return isConnected && connectionInfo != null;
    }

    /**
     * called by checking usb device
     * do request device permission
     *
     * @param device USB device
     */
    @Override
    public void onAttach(UsbDevice device) {
        if (!isPermissionRequest) {
            isPermissionRequest = true;
            usbCameraCommand.requestUsbPermission(device);
        }
    }

    /**
     * called by taking out usb device
     * do close camera
     *
     * @param device USB device
     */
    @Override
    public void onDettach(UsbDevice device) {
        if (isPermissionRequest) {
            isPermissionRequest = false;
            usbCameraCommand.closeCamera();
        }
    }

    /**
     * called by connect to usb camera
     * do open camera,start previewing
     *
     * @param device    USB device
     * @param ctrlBlock control class
     * @param createNew is create New
     */
    @Override
    public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
        connectionInfo = new ConnectionInfo(device, ctrlBlock);
        isConnected = true;
        usbCameraCommand.updateState(UsbCameraState.Connected);
        if (onConnectListener != null) {
            onConnectListener.onUsbConnect();
        }
        usbCameraCommand.openCamera();
    }

    /**
     * called by disconnect to usb camera
     *
     * @param device    USB device
     * @param ctrlBlock control class
     */
    @Override
    public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
        usbCameraCommand.updateState(UsbCameraState.Free);
        if (onConnectListener != null) {
            onConnectListener.onUsbDisConnect();
        }
        connectionInfo = null;
        isConnected = false;
    }

    @Override
    public void onCancel(UsbDevice device) {

    }

    /**
     * USB连接并成功授权后获取的设备信息
     */
    public static class ConnectionInfo {

        private UsbDevice usbDevice;
        private USBMonitor.UsbControlBlock ctrlBlock;

        public ConnectionInfo(UsbDevice usbDevice, USBMonitor.UsbControlBlock ctrlBlock) {
            this.usbDevice = usbDevice;
            this.ctrlBlock = ctrlBlock;
        }

        public UsbDevice getUsbDevice() {
            return usbDevice;
        }

        public USBMonitor.UsbControlBlock getCtrlBlock() {
            return ctrlBlock;
        }
    }
}
