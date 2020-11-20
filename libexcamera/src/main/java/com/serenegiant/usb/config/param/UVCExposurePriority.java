package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCExposurePriority extends UVCParamConfig {

    public UVCExposurePriority(ParamLimit paramLimit) {
        super(TAG_PARAM_EXPOSURE_PRIORITY, paramLimit);
    }
}
