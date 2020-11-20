package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCExposure extends UVCParamConfig {

    public UVCExposure(ParamLimit paramLimit) {
        super(TAG_PARAM_EXPOSURE, paramLimit);
    }
}
