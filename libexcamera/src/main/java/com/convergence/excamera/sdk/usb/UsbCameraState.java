package com.convergence.excamera.sdk.usb;

/**
 * USB相机状态
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-25
 * @Organization Convergence Ltd.
 */
public enum UsbCameraState {

    /*
    空闲状态，未进行USB连接授权
    Free, USB device is disconnected or permission is denied
     */
    Free,

    /*
    连接状态，已进行USB连接授权，但未打开UVC Camera
    Connected, USB device is connected and permission have been granted, but UVC Camera is not opened
     */
    Connected,

    /*
    开启状态，已打开UVC Camera，但未开启预览
    Opened, UVC Camera is opened, but preview is close
     */
    Opened,

    /*
    预览状态，正在预览中
    Previewing, preview is started and frame is available from USB device
     */
    Previewing
}