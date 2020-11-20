package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCGamma extends UVCParamConfig {

    public UVCGamma(ParamLimit paramLimit) {
        super(TAG_PARAM_GAMMA, paramLimit);
    }
}
