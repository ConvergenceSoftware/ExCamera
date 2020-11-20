package com.convergence.excamera.sdk.wifi.config.base;

import com.convergence.excamera.sdk.wifi.config.auto.WifiExposureAuto;
import com.convergence.excamera.sdk.wifi.config.auto.WifiFocusAuto;
import com.convergence.excamera.sdk.wifi.config.auto.WifiWhiteBalanceAuto;
import com.convergence.excamera.sdk.wifi.config.param.WifiBacklightCompensation;
import com.convergence.excamera.sdk.wifi.config.param.WifiBrightness;
import com.convergence.excamera.sdk.wifi.config.param.WifiContrast;
import com.convergence.excamera.sdk.wifi.config.param.WifiExposure;
import com.convergence.excamera.sdk.wifi.config.param.WifiFocus;
import com.convergence.excamera.sdk.wifi.config.param.WifiGain;
import com.convergence.excamera.sdk.wifi.config.param.WifiGamma;
import com.convergence.excamera.sdk.wifi.config.param.WifiHue;
import com.convergence.excamera.sdk.wifi.config.param.WifiJpegQuality;
import com.convergence.excamera.sdk.wifi.config.param.WifiPowerLineFrequency;
import com.convergence.excamera.sdk.wifi.config.param.WifiSaturation;
import com.convergence.excamera.sdk.wifi.config.param.WifiSharpness;
import com.convergence.excamera.sdk.wifi.config.param.WifiWhiteBalance;
import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

/**
 * Wifi相机参数封装类创建工具
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiConfigCreator {

    private WifiConfigCreator() {
    }

    public static WifiConfig create(NConfigList.ControlsBean controlsBean) {
        String tag = WifiConfig.getIdTagMap().get(controlsBean.getId());
        if (tag == null || tag.isEmpty()) return null;
        switch (tag) {
            case WifiConfig.TAG_AUTO_FOCUS_AUTO:
                return new WifiFocusAuto(controlsBean);
            case WifiConfig.TAG_AUTO_WHITE_BALANCE_AUTO:
                return new WifiWhiteBalanceAuto(controlsBean);
            case WifiConfig.TAG_AUTO_EXPOSURE_AUTO:
                return new WifiExposureAuto(controlsBean);
            case WifiConfig.TAG_PARAM_FOCUS:
                return new WifiFocus(controlsBean);
            case WifiConfig.TAG_PARAM_WHITE_BALANCE:
                return new WifiWhiteBalance(controlsBean);
            case WifiConfig.TAG_PARAM_EXPOSURE:
                return new WifiExposure(controlsBean);
            case WifiConfig.TAG_PARAM_BRIGHTNESS:
                return new WifiBrightness(controlsBean);
            case WifiConfig.TAG_PARAM_CONTRAST:
                return new WifiContrast(controlsBean);
            case WifiConfig.TAG_PARAM_SATURATION:
                return new WifiSaturation(controlsBean);
            case WifiConfig.TAG_PARAM_HUE:
                return new WifiHue(controlsBean);
            case WifiConfig.TAG_PARAM_GAMMA:
                return new WifiGamma(controlsBean);
            case WifiConfig.TAG_PARAM_GAIN:
                return new WifiGain(controlsBean);
            case WifiConfig.TAG_PARAM_SHARPNESS:
                return new WifiSharpness(controlsBean);
            case WifiConfig.TAG_PARAM_BACKLIGHT_COMPENSATION:
                return new WifiBacklightCompensation(controlsBean);
            case WifiConfig.TAG_PARAM_POWER_LINE_Frequency:
                return new WifiPowerLineFrequency(controlsBean);
            case WifiConfig.TAG_PARAM_JPEG_QUALITY:
                return new WifiJpegQuality(controlsBean);
            default:
                return null;
        }
    }
}
