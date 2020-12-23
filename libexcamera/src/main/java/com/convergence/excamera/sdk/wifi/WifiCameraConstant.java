package com.convergence.excamera.sdk.wifi;

import com.convergence.excamera.sdk.common.CameraLogger;

/**
 * Wifi相机模块常量
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class WifiCameraConstant {

    public static final String TAG = "CVGC_WIFI_CAMERA";
    public static final boolean DEBUG = true;
    public static final boolean IS_LOG_FRAME_DATA = false;
    public static final boolean IS_LOG_FPS = false;

    public enum PhotographType {
        /*
        直接等待数据流获取下一帧图像实现拍照
         */
        Stream,
        /*
        网络请求snapshot获取一帧图像实现拍照
         */
        NetworkRequest
    }

    /**
     * 当前拍照类型
     */
    public static final PhotographType PHOTOGRAPH_TYPE = PhotographType.Stream;

    /**
     * WIFI相机默认网络请求IP地址
     */
    public static final String DEFAULT_BASE_URL = "http://192.168.8.10:8080/";
    /**
     * WIFI相机获取字节流周期（单位：毫秒）
     */
    public static final long DEFAULT_WIFI_CAMERA_STREAM_PERIOD = 30;
    /**
     * WIFI相机重试次数阈值
     */
    public static final int DEFAULT_WIFI_CAMERA_RETRY_TIMES_THRESHOLD = 120;
    /**
     * WIFI相机重试周期（单位：毫秒)
     */
    public static final long DEFAULT_WIFI_CAMERA_RETRY_PERIOD = 2000;

    /**
     * WIFI相机默认分辨率宽度
     */
    public static final int DEFAULT_RESOLUTION_WIDTH = 1920;
    /**
     * WIFI相机默认分辨率高度
     */
    public static final int DEFAULT_RESOLUTION_HEIGHT = 1080;
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
