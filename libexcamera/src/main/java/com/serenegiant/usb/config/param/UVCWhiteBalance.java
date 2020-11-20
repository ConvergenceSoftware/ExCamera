package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCWhiteBalance extends UVCParamConfig {

    public UVCWhiteBalance(ParamLimit paramLimit) {
        super(TAG_PARAM_WHITE_BALANCE, paramLimit);
    }
}
