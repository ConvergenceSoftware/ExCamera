package com.convergence.excamera.view.config;

/**
 * 调整参数类型枚举
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public enum ConfigType {

    Unknown("Unknown"),//未设定
    Focus("Focus"),//对焦
    WhiteBalance("WhiteBalance"),//白平衡
    Exposure("Exposure"),//曝光
    Brightness("Brightness"),//亮度
    Contrast("Contrast"),//对比度
    Hue("Hue"),//色调
    Saturation("Saturation"),//饱和度
    Sharpness("Sharpness"),//锐度
    Gamma("Gamma"),//伽马
    Gain("Gain"),//增益

    Iris("Iris"),//可变光圈
    Zoom("Zoom"),//缩放
    Roll("Roll"),//滚动（镜像翻转）
    Pan("Pan"),//平移
    Tilt("Tilt"),//倾斜

    Quality("Quality");//图片质量

    private String name;

    ConfigType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

