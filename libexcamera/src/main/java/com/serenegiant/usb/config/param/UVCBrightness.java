package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCBrightness extends UVCParamConfig {

    public UVCBrightness(ParamLimit paramLimit) {
        super(TAG_PARAM_BRIGHTNESS, paramLimit);
    }
}
