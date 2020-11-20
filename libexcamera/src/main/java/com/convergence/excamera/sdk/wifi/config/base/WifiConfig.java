package com.convergence.excamera.sdk.wifi.config.base;

import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;

import java.util.HashMap;
import java.util.Map;

/**
 * Wifi相机参数封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public abstract class WifiConfig {

    public static final String TAG_AUTO_FOCUS_AUTO = "FocusAuto";
    public static final String TAG_AUTO_WHITE_BALANCE_AUTO = "WhiteBalanceAuto";
    public static final String TAG_AUTO_EXPOSURE_AUTO = "ExposureAuto";
    public static final String TAG_PARAM_FOCUS = "Focus";
    public static final String TAG_PARAM_WHITE_BALANCE = "WhiteBalance";
    public static final String TAG_PARAM_EXPOSURE = "Exposure";
    public static final String TAG_PARAM_BRIGHTNESS = "Brightness";
    public static final String TAG_PARAM_CONTRAST = "Contrast";
    public static final String TAG_PARAM_SATURATION = "Saturation";
    public static final String TAG_PARAM_HUE = "Hue";
    public static final String TAG_PARAM_GAMMA = "Gamma";
    public static final String TAG_PARAM_GAIN = "Gain";
    public static final String TAG_PARAM_SHARPNESS = "Sharpness";
    public static final String TAG_PARAM_BACKLIGHT_COMPENSATION = "BacklightCompensation";
    public static final String TAG_PARAM_POWER_LINE_Frequency = "PowerLineFrequency";
    public static final String TAG_PARAM_JPEG_QUALITY = "JpegQuality";

    public static final String ID_FOCUS_AUTO = "10094860";
    public static final String ID_WHITE_BALANCE_AUTO = "9963788";
    public static final String ID_EXPOSURE_AUTO = "10094849";
    public static final String ID_FOCUS = "10094858";
    public static final String ID_WHITE_BALANCE = "9963802";
    public static final String ID_EXPOSURE = "10094850";
    public static final String ID_BRIGHTNESS = "9963776";
    public static final String ID_CONTRAST = "9963777";
    public static final String ID_SATURATION = "9963778";
    public static final String ID_HUE = "9963779";
    public static final String ID_GAMMA = "9963792";
    public static final String ID_GAIN = "9963795";
    public static final String ID_SHARPNESS = "9963803";
    public static final String ID_BACKLIGHT_COMPENSATION = "9963804";
    public static final String ID_POWER_LINE_Frequency = "9963800";
    public static final String ID_JPEG_QUALITY = "1";

    //ID——Tag对应表
    private static Map<String, String> idTagMap;

    //参数类型标识Tag
    protected String tag;
    //参数id（网络数据提供）
    protected String id;
    //参数名称（网络数据提供）
    protected String name;
    //最小值（网络数据提供）
    protected int min;
    //最大值（网络数据提供）
    protected int max;
    //默认值（网络数据提供）
    protected int def;
    //当前值（网络数据提供）
    protected int cur;
    //类别（网络数据提供）
    protected int type;
    //参数调整步长（网络数据提供）
    protected int step;
    //dest（网络数据提供）
    protected int dest;
    //flag（网络数据提供）
    protected int flag;
    //group（网络数据提供）
    protected int group;
    //更新值
    protected int update;

    static {
        idTagMap = new HashMap<>();
        idTagMap.put(ID_FOCUS_AUTO, TAG_AUTO_FOCUS_AUTO);
        idTagMap.put(ID_WHITE_BALANCE_AUTO, TAG_AUTO_WHITE_BALANCE_AUTO);
        idTagMap.put(ID_EXPOSURE_AUTO, TAG_AUTO_EXPOSURE_AUTO);
        idTagMap.put(ID_FOCUS, TAG_PARAM_FOCUS);
        idTagMap.put(ID_WHITE_BALANCE, TAG_PARAM_WHITE_BALANCE);
        idTagMap.put(ID_EXPOSURE, TAG_PARAM_EXPOSURE);
        idTagMap.put(ID_BRIGHTNESS, TAG_PARAM_BRIGHTNESS);
        idTagMap.put(ID_CONTRAST, TAG_PARAM_CONTRAST);
        idTagMap.put(ID_SATURATION, TAG_PARAM_SATURATION);
        idTagMap.put(ID_HUE, TAG_PARAM_HUE);
        idTagMap.put(ID_GAMMA, TAG_PARAM_GAMMA);
        idTagMap.put(ID_GAIN, TAG_PARAM_GAIN);
        idTagMap.put(ID_SHARPNESS, TAG_PARAM_SHARPNESS);
        idTagMap.put(ID_BACKLIGHT_COMPENSATION, TAG_PARAM_BACKLIGHT_COMPENSATION);
        idTagMap.put(ID_POWER_LINE_Frequency, TAG_PARAM_POWER_LINE_Frequency);
        idTagMap.put(ID_JPEG_QUALITY, TAG_PARAM_JPEG_QUALITY);
    }

    protected WifiConfig(NConfigList.ControlsBean controlsBean) {
        id = controlsBean.getId();
        name = controlsBean.getName();
        min = controlsBean.getMin();
        max = controlsBean.getMax();
        def = controlsBean.getDefaultX();
        cur = controlsBean.getValue();
        type = controlsBean.getType();
        step = controlsBean.getStep();
        dest = controlsBean.getDest();
        flag = controlsBean.getFlags();
        group = controlsBean.getGroup();
        tag = idTagMap.get(id);
        update = cur;
    }

    public static Map<String, String> getIdTagMap() {
        return idTagMap;
    }

    public String getTag() {
        return tag;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public int getType() {
        return type;
    }

    public int getStep() {
        return step;
    }

    public int getDest() {
        return dest;
    }

    public int getFlag() {
        return flag;
    }

    public int getGroup() {
        return group;
    }

    public int getUpdate() {
        return update;
    }

    /**
     * 恢复默认值
     */
    public void reset() {
        update = def;
    }

    /**
     * 指令成功后将更新值赋予当前值
     */
    public void refresh() {
        cur = update;
    }

    @Override
    public String toString() {
        return "Config <" + name + "> : id = " + id + " , type = " + type + " , flag = " + flag
                + " , group = " + group + " , dest = " + dest + " , step = " + step
                + " , min = " + min + " , max = " + max + " , def = " + def + " , cur = " + cur;
    }
}
