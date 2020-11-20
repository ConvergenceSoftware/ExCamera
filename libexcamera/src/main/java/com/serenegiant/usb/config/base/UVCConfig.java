package com.serenegiant.usb.config.base;

import com.serenegiant.usb.UVCCamera;

import java.util.HashMap;
import java.util.Map;

public abstract class UVCConfig {

    /**
     * 显微相机参数
     * UVC Camera Config :
     * Config <FocusAuto> : flag = 131072 , isEnable = true , isAuto = false
     * Config <Privacy> : flag = 262144 , isEnable = false , isAuto = false
     * Config <WhiteBalanceAuto> : flag = -2147479552 , isEnable = true , isAuto = false
     * Config <WhiteBalanceComponentAuto> : flag = -2147475456 , isEnable = false , isAuto = false
     * <p>
     * Config <ScanningMode> : flag = 1 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <ExposureMode> : flag = 2 , isEnable = true ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <ExposurePriority> : flag = 4 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Exposure> : flag = 8 , isEnable = true ,
     * min = 3 , max = 2047 , def = 166 , cur = 166
     * Config <Focus> : flag = 32 , isEnable = true ,
     * min = 0 , max = 255 , def = 0 , cur = 0
     * Config <FocusRel> : flag = 64 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Iris> : flag = 128 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <IrisRel> : flag = 256 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Zoom> : flag = 512 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <ZoomRel> : flag = 1024 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Pan> : flag = 2048 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <PanRel> : flag = 4096 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Tilt> : flag = 2048 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <TiltRel> : flag = 4096 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Roll> : flag = 8192 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <RollRel> : flag = 16384 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <Brightness> : flag = -2147483647 , isEnable = true ,
     * min = -64 , max = 64 , def = -9 , cur = 0
     * Config <Contrast> : flag = -2147483646 , isEnable = true ,
     * min = 0 , max = 95 , def = 0 , cur = 0
     * Config <Hue> : flag = -2147483644 , isEnable = true ,
     * min = -2000 , max = 2000 , def = 185 , cur = 0
     * Config <Saturation> : flag = -2147483640 , isEnable = true ,
     * min = 0 , max = 100 , def = 68 , cur = 0
     * Config <Sharpness> : flag = -2147483632 , isEnable = true ,
     * min = 1 , max = 8 , def = 6 , cur = 6
     * Config <Gamma> : flag = -2147483616 , isEnable = true ,
     * min = 100 , max = 300 , def = 140 , cur = 0
     * Config <Gain> : flag = -2147483136 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <WhiteBalance> : flag = -2147483584 , isEnable = true ,
     * min = 2800 , max = 6500 , def = 4600 , cur = 0
     * Config <WhiteBalanceComponent> : flag = -2147483520 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <BacklightCompensation> : flag = -2147483392 , isEnable = true ,
     * min = 0 , max = 1 , def = 1 , cur = 0
     * Config <PowerLineFrequency> : flag = -2147482624 , isEnable = true ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <DigitalMultiplier> : flag = -2147467264 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <DigitalMultiplierLimit> : flag = -2147450880 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <AnalogVideoStandard> : flag = -2147418112 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     * Config <AnalogVideoLockStatus> : flag = -2147352576 , isEnable = false ,
     * min = 0 , max = 0 , def = 0 , cur = 0
     */

    public static final String TAG_AUTO_FOCUS_AUTO = "FocusAuto";
    public static final String TAG_AUTO_PRIVACY = "Privacy";
    public static final String TAG_AUTO_WHITE_BALANCE_AUTO = "WhiteBalanceAuto";
    public static final String TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO = "WhiteBalanceComponentAuto";

    public static final String TAG_PARAM_SCANNING_MODE = "ScanningMode";
    public static final String TAG_PARAM_EXPOSURE_MODE = "ExposureMode";
    public static final String TAG_PARAM_EXPOSURE_PRIORITY = "ExposurePriority";
    public static final String TAG_PARAM_EXPOSURE = "Exposure";
    public static final String TAG_PARAM_FOCUS = "Focus";
    public static final String TAG_PARAM_FOCUS_REL = "FocusRel";
    public static final String TAG_PARAM_IRIS = "Iris";
    public static final String TAG_PARAM_IRIS_REL = "IrisRel";
    public static final String TAG_PARAM_ZOOM = "Zoom";
    public static final String TAG_PARAM_ZOOM_REL = "ZoomRel";
    public static final String TAG_PARAM_PAN = "Pan";
    public static final String TAG_PARAM_PAN_REL = "PanRel";
    public static final String TAG_PARAM_TILT = "Tilt";
    public static final String TAG_PARAM_TILT_REL = "TiltRel";
    public static final String TAG_PARAM_ROLL = "Roll";
    public static final String TAG_PARAM_ROLL_REL = "RollRel";
    public static final String TAG_PARAM_BRIGHTNESS = "Brightness";
    public static final String TAG_PARAM_CONTRAST = "Contrast";
    public static final String TAG_PARAM_HUE = "Hue";
    public static final String TAG_PARAM_SATURATION = "Saturation";
    public static final String TAG_PARAM_SHARPNESS = "Sharpness";
    public static final String TAG_PARAM_GAMMA = "Gamma";
    public static final String TAG_PARAM_GAIN = "Gain";
    public static final String TAG_PARAM_WHITE_BALANCE = "WhiteBalance";
    public static final String TAG_PARAM_WHITE_BALANCE_COMPONENT = "WhiteBalanceComponent";
    public static final String TAG_PARAM_BACKLIGHT_COMPENSATION = "BacklightCompensation";
    public static final String TAG_PARAM_POWER_LINE_FREQUENCY = "PowerLineFrequency";
    public static final String TAG_PARAM_DIGITAL_MULTIPLIER = "DigitalMultiplier";
    public static final String TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT = "DigitalMultiplierLimit";
    public static final String TAG_PARAM_ANALOG_VIDEO_STANDARD = "AnalogVideoStandard";
    public static final String TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS = "AnalogVideoLockStatus";

    private static Map<String, Integer> tagFlagMap;

    static {
        tagFlagMap = new HashMap<>();
        tagFlagMap.put(TAG_AUTO_FOCUS_AUTO, UVCCamera.CTRL_FOCUS_AUTO);
        tagFlagMap.put(TAG_AUTO_PRIVACY, UVCCamera.CTRL_PRIVACY);
        tagFlagMap.put(TAG_AUTO_WHITE_BALANCE_AUTO, UVCCamera.PU_WB_TEMP_AUTO);
        tagFlagMap.put(TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO, UVCCamera.PU_WB_COMPO_AUTO);

        tagFlagMap.put(TAG_PARAM_SCANNING_MODE, UVCCamera.CTRL_SCANNING);
        tagFlagMap.put(TAG_PARAM_EXPOSURE_MODE, UVCCamera.CTRL_AE);
        tagFlagMap.put(TAG_PARAM_EXPOSURE_PRIORITY, UVCCamera.CTRL_AE_PRIORITY);
        tagFlagMap.put(TAG_PARAM_EXPOSURE, UVCCamera.CTRL_AE_ABS);
        tagFlagMap.put(TAG_PARAM_FOCUS, UVCCamera.CTRL_FOCUS_ABS);
        tagFlagMap.put(TAG_PARAM_FOCUS_REL, UVCCamera.CTRL_FOCUS_REL);
        tagFlagMap.put(TAG_PARAM_IRIS, UVCCamera.CTRL_IRIS_ABS);
        tagFlagMap.put(TAG_PARAM_IRIS_REL, UVCCamera.CTRL_IRIS_REL);
        tagFlagMap.put(TAG_PARAM_ZOOM, UVCCamera.CTRL_ZOOM_ABS);
        tagFlagMap.put(TAG_PARAM_ZOOM_REL, UVCCamera.CTRL_ZOOM_REL);
        tagFlagMap.put(TAG_PARAM_PAN, UVCCamera.CTRL_PANTILT_ABS);
        tagFlagMap.put(TAG_PARAM_PAN_REL, UVCCamera.CTRL_PANTILT_REL);
        tagFlagMap.put(TAG_PARAM_TILT, UVCCamera.CTRL_PANTILT_ABS);
        tagFlagMap.put(TAG_PARAM_TILT_REL, UVCCamera.CTRL_PANTILT_REL);
        tagFlagMap.put(TAG_PARAM_ROLL, UVCCamera.CTRL_ROLL_ABS);
        tagFlagMap.put(TAG_PARAM_ROLL_REL, UVCCamera.CTRL_ROLL_REL);
        tagFlagMap.put(TAG_PARAM_BRIGHTNESS, UVCCamera.PU_BRIGHTNESS);
        tagFlagMap.put(TAG_PARAM_CONTRAST, UVCCamera.PU_CONTRAST);
        tagFlagMap.put(TAG_PARAM_HUE, UVCCamera.PU_HUE);
        tagFlagMap.put(TAG_PARAM_SATURATION, UVCCamera.PU_SATURATION);
        tagFlagMap.put(TAG_PARAM_SHARPNESS, UVCCamera.PU_SHARPNESS);
        tagFlagMap.put(TAG_PARAM_GAMMA, UVCCamera.PU_GAMMA);
        tagFlagMap.put(TAG_PARAM_GAIN, UVCCamera.PU_GAIN);
        tagFlagMap.put(TAG_PARAM_WHITE_BALANCE, UVCCamera.PU_WB_TEMP);
        tagFlagMap.put(TAG_PARAM_WHITE_BALANCE_COMPONENT, UVCCamera.PU_WB_COMPO);
        tagFlagMap.put(TAG_PARAM_BACKLIGHT_COMPENSATION, UVCCamera.PU_BACKLIGHT);
        tagFlagMap.put(TAG_PARAM_POWER_LINE_FREQUENCY, UVCCamera.PU_POWER_LF);
        tagFlagMap.put(TAG_PARAM_DIGITAL_MULTIPLIER, UVCCamera.PU_DIGITAL_MULT);
        tagFlagMap.put(TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT, UVCCamera.PU_DIGITAL_LIMIT);
        tagFlagMap.put(TAG_PARAM_ANALOG_VIDEO_STANDARD, UVCCamera.PU_AVIDEO_STD);
        tagFlagMap.put(TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS, UVCCamera.PU_AVIDEO_LOCK);
    }

    protected String tag;
    protected int flag;
    protected boolean isEnable = false;

    protected UVCConfig(String tag) {
        this.tag = tag;
        Integer value = tagFlagMap.get(tag);
        flag = (value == null ? 0 : value);
    }

    public void refreshEnable(boolean enable) {
        isEnable = enable;
    }

    public String getTag() {
        return tag;
    }

    public long getFlag() {
        return flag;
    }

    public boolean isEnable() {
        return isEnable;
    }
}
