package com.convergence.excamera.sdk.wifi.core;

import com.convergence.excamera.sdk.common.TeleFocusHelper;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfig;

/**
 * 望远相机WiFi连接调焦助手
 *
 * @Author WangZiheng
 * @CreateDate 2020-12-07
 * @Organization Convergence Ltd.
 */
public class WifiTeleFocusHelper extends TeleFocusHelper {

    private WifiCameraController wifiCameraController;

    public WifiTeleFocusHelper(WifiCameraController wifiCameraController) {
        super();
        this.wifiCameraController = wifiCameraController;
    }

    @Override
    protected void setFocus(int value) {
        if (wifiCameraController != null && wifiCameraController.isPreviewing()) {
            wifiCameraController.setParam(WifiConfig.TAG_PARAM_FOCUS, value);
        }
    }
}
