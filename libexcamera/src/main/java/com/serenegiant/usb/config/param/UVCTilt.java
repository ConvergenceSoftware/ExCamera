package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCTilt extends UVCParamConfig {

    public UVCTilt(ParamLimit paramLimit) {
        super(TAG_PARAM_TILT, paramLimit);
    }
}
