package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCGain extends UVCParamConfig {

    public UVCGain(ParamLimit paramLimit) {
        super(TAG_PARAM_GAIN,paramLimit);
    }
}
