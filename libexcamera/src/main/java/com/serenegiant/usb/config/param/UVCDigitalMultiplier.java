package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCDigitalMultiplier extends UVCParamConfig {

    public UVCDigitalMultiplier(ParamLimit paramLimit) {
        super(TAG_PARAM_DIGITAL_MULTIPLIER, paramLimit);
    }
}
