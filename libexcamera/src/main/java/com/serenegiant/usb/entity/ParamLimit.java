package com.serenegiant.usb.entity;

import android.annotation.SuppressLint;

public class ParamLimit {

    public static final String TAG_CTRL_SCANNING_MODE = "ScanningMode";
    public static final String TAG_CTRL_EXPOSURE_MODE = "ExposureMode";
    public static final String TAG_CTRL_EXPOSURE_PRIORITY = "ExposurePriority";
    public static final String TAG_CTRL_EXPOSURE_ABS = "ExposureAbs";
    public static final String TAG_CTRL_FOCUS_ABS = "FocusAbs";
    public static final String TAG_CTRL_FOCUS_REL = "FocusRel";
    public static final String TAG_CTRL_IRIS_ABS = "IrisAbs";
    public static final String TAG_CTRL_IRIS_REL = "IrisRel";
    public static final String TAG_CTRL_ZOOM_ABS = "ZoomAbs";
    public static final String TAG_CTRL_ZOOM_REL = "ZoomAbs";
    public static final String TAG_CTRL_PAN_ABS = "PanAbs";
    public static final String TAG_CTRL_PAN_REL = "PanRel";
    public static final String TAG_CTRL_TILT_ABS = "TiltAbs";
    public static final String TAG_CTRL_TILT_REL = "TiltRel";
    public static final String TAG_CTRL_ROLL_ABS = "RollAbs";
    public static final String TAG_CTRL_ROLL_REL = "RollRel";

    public static final String TAG_PROC_BRIGHTNESS = "Brightness";
    public static final String TAG_PROC_CONTRAST = "Contrast";
    public static final String TAG_PROC_HUE = "Hue";
    public static final String TAG_PROC_SATURATION = "Saturation";
    public static final String TAG_PROC_SHARPNESS = "Sharpness";
    public static final String TAG_PROC_GAMMA = "Gamma";
    public static final String TAG_PROC_GAIN = "Gain";
    public static final String TAG_PROC_WHITE_BALANCE = "WhiteBalance";
    public static final String TAG_PROC_WHITE_BALANCE_COMPONENT = "WhiteBalanceComponent";
    public static final String TAG_PROC_BACKLIGHT_COMPENSATION = "BacklightCompensation";
    public static final String TAG_PROC_POWER_LINE_FREQUENCY = "PowerLineFrequency";
    public static final String TAG_PROC_DIGITAL_MULTIPLIER = "DigitalMultiplier";
    public static final String TAG_PROC_DIGITAL_MULTIPLIER_LIMIT = "DigitalMultiplierLimit";
    public static final String TAG_PROC_ANALOG_VIDEO_STANDARD = "AnalogVideoStandard";
    public static final String TAG_PROC_ANALOG_VIDEO_LOCK_STATUS = "AnalogVideoLockStatus";

    public static final String[] TAG_LIST = {
            TAG_CTRL_SCANNING_MODE,
            TAG_CTRL_EXPOSURE_MODE,
            TAG_CTRL_EXPOSURE_PRIORITY,
            TAG_CTRL_EXPOSURE_ABS,
            TAG_CTRL_FOCUS_ABS,
            TAG_CTRL_FOCUS_REL,
            TAG_CTRL_IRIS_ABS,
            TAG_CTRL_IRIS_REL,
            TAG_CTRL_ZOOM_ABS,
            TAG_CTRL_ZOOM_REL,
            TAG_CTRL_PAN_ABS,
            TAG_CTRL_PAN_REL,
            TAG_CTRL_TILT_ABS,
            TAG_CTRL_TILT_REL,
            TAG_CTRL_ROLL_ABS,
            TAG_CTRL_ROLL_REL,
            TAG_PROC_BRIGHTNESS,
            TAG_PROC_CONTRAST,
            TAG_PROC_HUE,
            TAG_PROC_SATURATION,
            TAG_PROC_SHARPNESS,
            TAG_PROC_GAMMA,
            TAG_PROC_GAIN,
            TAG_PROC_WHITE_BALANCE,
            TAG_PROC_WHITE_BALANCE_COMPONENT,
            TAG_PROC_BACKLIGHT_COMPENSATION,
            TAG_PROC_POWER_LINE_FREQUENCY,
            TAG_PROC_DIGITAL_MULTIPLIER,
            TAG_PROC_DIGITAL_MULTIPLIER_LIMIT,
            TAG_PROC_ANALOG_VIDEO_STANDARD,
            TAG_PROC_ANALOG_VIDEO_LOCK_STATUS
    };

    private String tag;
    private int min;
    private int max;
    private int def;

    public ParamLimit(String tag) {
        this.tag = tag;
    }

    public void refreshData(int min, int max, int def) {
        this.min = min;
        this.max = max;
        this.def = def;
    }

    public String getTag() {
        return tag;
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

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%s : min = %d , max = %d , def = %d", tag, min, max, def);
    }
}
