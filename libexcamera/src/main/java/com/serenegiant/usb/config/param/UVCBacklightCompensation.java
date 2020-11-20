package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCBacklightCompensation extends UVCParamConfig {

    public UVCBacklightCompensation(ParamLimit paramLimit) {
        super(TAG_PARAM_BACKLIGHT_COMPENSATION, paramLimit);
    }
}
