package com.convergence.excamera.sdk.wifi.config.auto;

import com.convergence.excamera.sdk.wifi.config.base.WifiAutoConfig;
import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

/**
 * 自动曝光
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiExposureAuto extends WifiAutoConfig {

    public WifiExposureAuto(NConfigList.ControlsBean controlsBean) {
        super(controlsBean, 1, 3);
    }
}
