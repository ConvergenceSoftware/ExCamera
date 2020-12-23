package com.convergence.excamera.sdk.usb;

import com.convergence.excamera.sdk.common.CameraLogger;

/**
 * USB相机常量
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraConstant {

    public static final boolean DEBUG = true;
    public static final String TAG = "CVGC_USB_CAMERA";
    public static final boolean IS_LOG_FPS = false;

    public enum PreviewType {
        /*
        直接通过Native方法将画面传入控件
         */
        Surface,
        /*
        获取逐帧Bitmap后在控件中实时绘制
         */
        Draw
    }

    /**
     * 当前预览方式
     */
    public static final PreviewType PREVIEW_TYPE = PreviewType.Surface;

    /**
     * USB相机默认分辨率宽度
     */
    public static final int DEFAULT_RESOLUTION_WIDTH = 1280;
    /**
     * USB相机默认分辨率高度
     */
    public static final int DEFAULT_RESOLUTION_HEIGHT = 720;
    /**
     * 是否默认水平翻转开启
     */
    public static final boolean DEFAULT_IS_FLIP_HORIZONTAL = false;
    /**
     * 是否默认垂直翻转开启
     */
    public static final boolean DEFAULT_IS_FLIP_VERTICAL = false;

    public static CameraLogger GetLogger() {
        return CameraLogger.create(TAG, DEBUG);
    }
}
