package com.serenegiant.usb.config.param;

import com.serenegiant.usb.config.base.UVCParamConfig;
import com.serenegiant.usb.entity.ParamLimit;

public class UVCWhiteBalanceComponent extends UVCParamConfig {

    public UVCWhiteBalanceComponent(ParamLimit paramLimit) {
        super(TAG_PARAM_WHITE_BALANCE_COMPONENT, paramLimit);
    }
}
