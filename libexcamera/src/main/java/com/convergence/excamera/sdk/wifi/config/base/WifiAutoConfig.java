package com.convergence.excamera.sdk.wifi.config.base;

import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

import java.util.Arrays;

/**
 * 自动类Wifi相机参数封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public abstract class WifiAutoConfig extends WifiConfig {

    private static final String[] AUTO_TAG_LIST = {
            TAG_AUTO_FOCUS_AUTO, TAG_AUTO_WHITE_BALANCE_AUTO, TAG_AUTO_EXPOSURE_AUTO
    };

    //手动对应值
    private int manualValue = 0;
    //自动对应值
    private int autoValue = 1;

    protected WifiAutoConfig(NConfigList.ControlsBean controlsBean) {
        super(controlsBean);
    }

    protected WifiAutoConfig(NConfigList.ControlsBean controlsBean, int valueManual, int autoValue) {
        super(controlsBean);
        this.manualValue = valueManual;
        this.autoValue = autoValue;
    }

    /**
     * 设置是否自动
     */
    public void setAuto(boolean isAuto) {
        update = isAuto ? autoValue : manualValue;
    }

    /**
     * 当前是否自动
     */
    public boolean isAuto() {
        return cur == autoValue;
    }

    /**
     * 判断该值是否自动对应值
     */
    public boolean isValueAuto(int value) {
        return value == autoValue;
    }

    /**
     * 根据Tag判断是否自动类配置
     */
    public static boolean isTagSupport(String tag) {
        return Arrays.asList(AUTO_TAG_LIST).contains(tag);
    }
}
