package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCScanningMode extends UVCParamConfig {

    public UVCScanningMode(ParamLimit paramLimit) {
        super(TAG_PARAM_SCANNING_MODE, paramLimit);
    }
}
