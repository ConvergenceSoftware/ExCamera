package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCHue extends UVCParamConfig {

    public UVCHue(ParamLimit paramLimit) {
        super(TAG_PARAM_HUE, paramLimit);
    }
}
