package com.convergence.excamera.sdk.planet;

import com.convergence.excamera.sdk.common.CameraLogger;

/**
 * 云台控制模块常量
 * @Author LiLei
 * @CreateDate 2021-05-21
 * @Organization Convergence Ltd.
 */

public class PlanetConstant {

    public static final String TAG = "CVGC_PLANET";
    public static final boolean DEBUG = true;


    /**
     * WIFI相机默认网络请求IP地址
     */
    public static final String DEFAULT_PLANET_CONTROL_URL = "http://192.168.8.10:8092";

    public static CameraLogger GetLogger() {
        return CameraLogger.create(TAG, DEBUG);
    }
}
