package com.convergence.excamera.sdk.wifi.entity;


import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfigCreator;
import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WiFi相机参数封装类，由网络请求结果转化
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraParam {

    private CameraLogger cameraLogger = WifiCameraConstant.GetLogger();
    private NConfigList data;
    private Map<String, WifiConfig> configMap;
    private WifiCameraResolution wifiCameraResolution;
    private boolean isAvailable = false;

    public WifiCameraParam() {
        configMap = new HashMap<>();
    }

    public void updateData(NConfigList data) {
        if (data == null) {
            return;
        }
        this.data = data;
        configMap.clear();
        List<NConfigList.ControlsBean> controlList = data.getControls();
        for (int i = 0; i < controlList.size(); i++) {
            NConfigList.ControlsBean controlsBean = controlList.get(i);
            WifiConfig wifiConfig = WifiConfigCreator.create(controlsBean);
            if (wifiConfig == null) {
                continue;
            }
            configMap.put(wifiConfig.getTag(), wifiConfig);
        }
        List<NConfigList.FormatsBean> formatsBeanList = data.getFormats();
        for (int i = 0; i < formatsBeanList.size(); i++) {
            NConfigList.FormatsBean formatsBean = formatsBeanList.get(i);
            if ("0".equals(formatsBean.getId())) {
                wifiCameraResolution = new WifiCameraResolution(formatsBean);
                break;
            }
        }
        isAvailable = true;
        cameraLogger.LogD("WifiCameraParam : isAvailable = " + isAvailable() + "\n" + wifiCameraResolution.toString()
                + "\n" + getAllConfigDes());
    }

    public void clear() {
        configMap.clear();
        data = null;
        wifiCameraResolution = null;
        isAvailable = false;
    }

    public NConfigList getData() {
        return data;
    }

    public Map<String, WifiConfig> getConfigMap() {
        return configMap;
    }

    public WifiCameraResolution getWifiCameraResolution() {
        return wifiCameraResolution;
    }

    public String getAllConfigDes() {
        StringBuilder stringBuilder = new StringBuilder("Wifi Camera Config : ");
        for (Map.Entry<String, WifiConfig> entry : configMap.entrySet()) {
            stringBuilder.append("\n").append(entry.getValue().toString());
        }
        return stringBuilder.toString();
    }

    public WifiConfig getConfig(String tag) {
        return configMap.get(tag);
    }

    public boolean isAvailable() {
        return isAvailable && wifiCameraResolution != null && wifiCameraResolution.isAvailable();
    }

    @Override
    public String toString() {
        return "WifiCameraConfig : isAvailable = " + isAvailable + "\n" + wifiCameraResolution.toString()
                + "\n" + getAllConfigDes();
    }
}
