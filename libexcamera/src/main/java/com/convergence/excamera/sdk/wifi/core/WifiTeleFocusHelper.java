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

    private final WifiCameraController wifiCameraController;

    public WifiTeleFocusHelper(WifiCameraController wifiCameraController) {
        super();
        this.wifiCameraController = wifiCameraController;
        bindImgProvider(wifiCameraController);
        bindAFCallback(wifiCameraController);
    }

    @Override
    protected void setFocus(boolean isAF, int value) {
        if (wifiCameraController != null && wifiCameraController.isPreviewing()) {
            if (isAF) {
                wifiCameraController.updateFocusExecute(value);
            } else {
                wifiCameraController.setParam(WifiConfig.TAG_PARAM_FOCUS, value);
            }
        }
    }
}
