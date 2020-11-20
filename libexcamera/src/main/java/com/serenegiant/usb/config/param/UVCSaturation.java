package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCSaturation extends UVCParamConfig {

    public UVCSaturation(ParamLimit paramLimit) {
        super(TAG_PARAM_SATURATION, paramLimit);
    }
}
