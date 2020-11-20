package com.convergence.excamera.sdk.wifi.config.base;

import androidx.core.math.MathUtils;

import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

import java.util.Arrays;

/**
 * 参数类Wifi相机参数封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public abstract class WifiParamConfig extends WifiConfig {

    private static final String[] PARAM_TAG_LIST = {
            TAG_PARAM_FOCUS, TAG_PARAM_WHITE_BALANCE, TAG_PARAM_EXPOSURE,
            TAG_PARAM_BRIGHTNESS, TAG_PARAM_CONTRAST, TAG_PARAM_SATURATION,
            TAG_PARAM_HUE, TAG_PARAM_GAMMA, TAG_PARAM_GAIN, TAG_PARAM_SHARPNESS,
            TAG_PARAM_BACKLIGHT_COMPENSATION, TAG_PARAM_POWER_LINE_Frequency, TAG_PARAM_JPEG_QUALITY
    };

    protected WifiParamConfig(NConfigList.ControlsBean controlsBean) {
        super(controlsBean);
    }

    /**
     * 设置参数
     */
    public void setParam(int value) {
        update = clamp(value);
    }

    /**
     * 设置参数百分比
     */
    public void setParamPercent(int percent) {
        update = getValueByPercent(percent);
    }

    /**
     * 设置参数一元二次百分比
     */
    public void setParamPercentQuadratic(int percent) {
        update = getValueByPercentQuadratic(percent);
    }

    /**
     * 获取当前百分比
     */
    public int getCurPercent() {
        return getPercentByValue(cur);
    }

    /**
     * 获取当前一元二次百分比
     */
    public int getCurPercentQuadratic() {
        return getPercentByValueQuadratic(cur);
    }

    /**
     * 获取默认百分比
     */
    public int getDefPercent() {
        return getPercentByValue(def);
    }

    /**
     * 获取默认一元二次百分比
     */
    public int getDefPercentQuadratic() {
        return getPercentByValueQuadratic(def);
    }

    /**
     * 根据百分比获取实际值
     */
    public int getValueByPercent(int percent) {
        float value = (float) (max - min) * percent / 100 + min;
        return (int) MathUtils.clamp(value, min, max);
    }

    /**
     * 更加实际值获取百分比
     */
    public int getPercentByValue(int value) {
        float percent = (float) (value - min) / (max - min) * 100;
        return (int) MathUtils.clamp(percent, 0, 100);
    }

    /**
     * 根据一元二次百分比获取实际值
     */
    public int getValueByPercentQuadratic(int percent) {
        float b = (float) min;
        float k = ((float) max - b) / 10000.f;
        float value = k * percent * percent + b;
        return (int) MathUtils.clamp(value, min, max);
    }

    /**
     * 更加实际值获取一元二次百分比
     */
    public int getPercentByValueQuadratic(int value) {
        float b = (float) min;
        float k = ((float) max - b) / 10000.f;
        float percent = (float) (Math.sqrt((float) value + b) / k);
        return (int) MathUtils.clamp(percent, 0, 100);
    }

    /**
     * 设置参数为最大值
     */
    public void setParamMax() {
        setParam(max);
    }

    /**
     * 设置参数为最小值
     */
    public void setParamMin() {
        setParam(min);
    }

    /**
     * 使参数满足范围
     */
    private int clamp(int value) {
        return MathUtils.clamp(value, min, max);
    }

    /**
     * 根据Tag判断是否参数类配置
     */
    public static boolean isTagSupport(String tag) {
        return Arrays.asList(PARAM_TAG_LIST).contains(tag);
    }
}
