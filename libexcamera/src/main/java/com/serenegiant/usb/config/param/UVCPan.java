package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCPan extends UVCParamConfig {

    public UVCPan(ParamLimit paramLimit) {
        super(TAG_PARAM_PAN, paramLimit);
    }
}
