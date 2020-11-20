package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCExposureMode extends UVCParamConfig {

    public UVCExposureMode(ParamLimit paramLimit) {
        super(TAG_PARAM_EXPOSURE_MODE, paramLimit);
    }
}
