package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCAnalogVideoLockStatus extends UVCParamConfig {

    public UVCAnalogVideoLockStatus(ParamLimit paramLimit) {
        super(TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS, paramLimit);
    }
}
