package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCRoll extends UVCParamConfig {

    public UVCRoll(ParamLimit paramLimit) {
        super(TAG_PARAM_ROLL, paramLimit);
    }
}
