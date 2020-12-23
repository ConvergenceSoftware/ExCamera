package com.convergence.excamera.view.config.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


import com.convergence.excamera.R;
import com.convergence.excamera.view.config.ConfigType;

import butterknife.ButterKnife;

/**
 * 参数布局基类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public abstract class ConfigBaseLayout extends LinearLayout {

    private static final int TYPE_CONFIG_UNKNOWN = 0;
    private static final int TYPE_CONFIG_FOCUS = 1;
    private static final int TYPE_CONFIG_WHITE_BALANCE = 2;
    private static final int TYPE_CONFIG_EXPOSURE = 3;
    private static final int TYPE_CONFIG_BRIGHTNESS = 4;
    private static final int TYPE_CONFIG_CONTRAST = 5;
    private static final int TYPE_CONFIG_HUE = 6;
    private static final int TYPE_CONFIG_SATURATION = 7;
    private static final int TYPE_CONFIG_SHARPNESS = 8;
    private static final int TYPE_CONFIG_GAMMA = 9;
    private static final int TYPE_CONFIG_GAIN = 10;
    private static final int TYPE_CONFIG_IRIS = 11;
    private static final int TYPE_CONFIG_ZOOM = 12;
    private static final int TYPE_CONFIG_ROLL = 13;
    private static final int TYPE_CONFIG_PAN = 14;
    private static final int TYPE_CONFIG_TILT = 15;
    private static final int TYPE_CONFIG_QUALITY = 16;

    protected Context context;
    protected ConfigType configType = ConfigType.Unknown;

    protected ConfigBaseLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    protected ConfigBaseLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    protected ConfigBaseLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConfigBaseLayout);
        int typeRes = typedArray.getInt(R.styleable.ConfigBaseLayout_configType, TYPE_CONFIG_UNKNOWN);
        switch (typeRes) {
            case TYPE_CONFIG_UNKNOWN:
                configType = ConfigType.Unknown;
                break;
            case TYPE_CONFIG_FOCUS:
                configType = ConfigType.Focus;
                break;
            case TYPE_CONFIG_WHITE_BALANCE:
                configType = ConfigType.WhiteBalance;
                break;
            case TYPE_CONFIG_EXPOSURE:
                configType = ConfigType.Exposure;
                break;
            case TYPE_CONFIG_BRIGHTNESS:
                configType = ConfigType.Brightness;
                break;
            case TYPE_CONFIG_CONTRAST:
                configType = ConfigType.Contrast;
                break;
            case TYPE_CONFIG_HUE:
                configType = ConfigType.Hue;
                break;
            case TYPE_CONFIG_SATURATION:
                configType = ConfigType.Saturation;
                break;
            case TYPE_CONFIG_SHARPNESS:
                configType = ConfigType.Sharpness;
                break;
            case TYPE_CONFIG_GAMMA:
                configType = ConfigType.Gamma;
                break;
            case TYPE_CONFIG_GAIN:
                configType = ConfigType.Gain;
                break;
            case TYPE_CONFIG_IRIS:
                configType = ConfigType.Iris;
                break;
            case TYPE_CONFIG_ZOOM:
                configType = ConfigType.Zoom;
                break;
            case TYPE_CONFIG_ROLL:
                configType = ConfigType.Roll;
                break;
            case TYPE_CONFIG_PAN:
                configType = ConfigType.Pan;
                break;
            case TYPE_CONFIG_TILT:
                configType = ConfigType.Tilt;
                break;
            case TYPE_CONFIG_QUALITY:
                configType = ConfigType.Quality;
                break;
            default:
                break;
        }
        typedArray.recycle();
    }

    protected void init() {
        View view = LayoutInflater.from(context).inflate(bindLayoutId(), this, true);
        ButterKnife.bind(this, view);
    }

    /**
     * 设置布局资源文件
     *
     * @return layout ID
     */
    protected abstract int bindLayoutId();

    /**
     * 获取参数类型
     *
     * @return 参数类型枚举
     */
    public ConfigType getConfigType() {
        return configType;
    }
}
