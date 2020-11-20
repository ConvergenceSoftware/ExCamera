package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCFocus extends UVCParamConfig {

    public UVCFocus(ParamLimit paramLimit) {
        super(TAG_PARAM_FOCUS, paramLimit);
    }
}
