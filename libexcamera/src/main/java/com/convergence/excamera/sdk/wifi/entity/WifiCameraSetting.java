package com.convergence.excamera.sdk.wifi.entity;

import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

/**
 * WiFi相机设置封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraSetting {

    private WifiCameraParam wifiCameraParam;

    private WifiCameraSetting() {
        wifiCameraParam = new WifiCameraParam();
    }

    public static WifiCameraSetting getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final WifiCameraSetting INSTANCE = new WifiCameraSetting();
    }

    public void updateParam(NConfigList data) {
        wifiCameraParam.updateData(data);
    }

    public WifiCameraParam getWifiCameraParam() {
        return wifiCameraParam;
    }

    public boolean isAvailable() {
        return wifiCameraParam.isAvailable();
    }
}
