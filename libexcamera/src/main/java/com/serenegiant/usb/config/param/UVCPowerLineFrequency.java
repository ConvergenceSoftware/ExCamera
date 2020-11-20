package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCPowerLineFrequency extends UVCParamConfig {

    public UVCPowerLineFrequency(ParamLimit paramLimit) {
        super(TAG_PARAM_POWER_LINE_FREQUENCY, paramLimit);
    }
}
