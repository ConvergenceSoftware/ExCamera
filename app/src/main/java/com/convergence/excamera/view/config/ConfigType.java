package com.convergence.excamera.view.config;

/**
 * 调整参数类型枚举
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public enum ConfigType {

    /*
    未设定
     */
    Unknown("Unknown"),
    /*
    对焦
     */
    Focus("Focus"),
    /*
    白平衡
     */
    WhiteBalance("WhiteBalance"),
    /*
    曝光
     */
    Exposure("Exposure"),
    /*
    亮度
     */
    Brightness("Brightness"),
    /*
    对比度
     */
    Contrast("Contrast"),
    /*
    色调
     */
    Hue("Hue"),
    /*
    饱和度
     */
    Saturation("Saturation"),
    /*
    锐度
     */
    Sharpness("Sharpness"),
    /*
    伽马
     */
    Gamma("Gamma"),
    /*
    增益
     */
    Gain("Gain"),

    /*
    可变光圈
     */
    Iris("Iris"),
    /*
    缩放
     */
    Zoom("Zoom"),
    /*
    滚动（镜像翻转）
     */
    Roll("Roll"),
    /*
    平移
     */
    Pan("Pan"),
    /*
    倾斜
     */
    Tilt("Tilt"),

    /*
    图片质量
     */
    Quality("Quality");

    private String name;

    ConfigType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

