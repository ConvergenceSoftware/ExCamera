package com.serenegiant.usb.config.base;

import androidx.core.math.MathUtils;

import com.serenegiant.usb.entity.ParamLimit;

public abstract class UVCParamConfig extends UVCConfig {

    protected ParamLimit paramLimit;
    protected int min;
    protected int max;
    protected int def;
    protected int cur;
    private boolean isInit = false;

    protected UVCParamConfig(String tag, ParamLimit paramLimit) {
        super(tag);
        this.paramLimit = paramLimit;
    }

    public void refreshLimit() {
        if (paramLimit == null) return;
        min = paramLimit.getMin();
        max = paramLimit.getMax();
        def = paramLimit.getDef();
        if (!isInit) {
            cur = def;
            isInit = true;
        }
    }

    public void setCur(int cur) {
        this.cur = cur;
    }

    public ParamLimit getParamLimit() {
        return paramLimit;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getDef() {
        return def;
    }

    public int getCur() {
        return cur;
    }

    public int clamp(int value) {
        return MathUtils.clamp(value, min, max);
    }

    public int getCurPercent() {
        return getPercentByValue(clamp(cur));
    }

    public int getCurPercentQuadratic() {
        return getPercentByValueQuadratic(clamp(cur));
    }

    public int getValueByPercent(int percent) {
        float value = (float) (max - min) * percent / 100 + min;
        return (int) MathUtils.clamp(value, min, max);
    }

    public int getPercentByValue(int value) {
        float percent = (float) (value - min) / (max - min) * 100;
        return (int) MathUtils.clamp(percent, 0, 100);
    }

    public int getValueByPercentQuadratic(int percent) {
        float b = (float) min;
        float k = ((float) max - b) / 10000.f;
        float value = k * percent * percent + b;
        return (int) MathUtils.clamp(value, min, max);
    }

    public int getPercentByValueQuadratic(int value) {
        float b = (float) min;
        float k = ((float) max - b) / 10000.f;
        float percent = (float) (Math.sqrt((float) value + b) / k);
        return (int) MathUtils.clamp(percent, 0, 100);
    }

    @Override
    public String toString() {
        return "Config <" + tag + "> : flag = " + flag + " , isEnable = " + isEnable
                + " , min = " + min + " , max = " + max + " , def = " + def + " , cur = " + cur;
    }
}
