package com.convergence.excamera.view.config;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.convergence.excamera.R;
import com.convergence.excamera.view.config.component.ConfigComLayout;
import com.convergence.excamera.view.config.component.ConfigMixLayout;
import com.convergence.excamera.view.config.component.MirrorFlipLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 显微相机模块-USB连接参数布局
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbMicroConfigLayout extends LinearLayout {

    @BindView(R.id.item_flip_layout_usb_micro_config)
    MirrorFlipLayout itemFlipLayoutUsbMicroConfig;
    @BindView(R.id.item_focus_layout_usb_micro_config)
    ConfigMixLayout itemFocusLayoutUsbMicroConfig;
    @BindView(R.id.item_white_balance_layout_usb_micro_config)
    ConfigMixLayout itemWhiteBalanceLayoutUsbMicroConfig;
    @BindView(R.id.item_exposure_layout_usb_micro_config)
    ConfigMixLayout itemExposureLayoutUsbMicroConfig;
    @BindView(R.id.item_brightness_layout_usb_micro_config)
    ConfigComLayout itemBrightnessLayoutUsbMicroConfig;
    @BindView(R.id.item_contrast_layout_usb_micro_config)
    ConfigComLayout itemContrastLayoutUsbMicroConfig;
    @BindView(R.id.item_hue_layout_usb_micro_config)
    ConfigComLayout itemHueLayoutUsbMicroConfig;
    @BindView(R.id.item_saturation_layout_usb_micro_config)
    ConfigComLayout itemSaturationLayoutUsbMicroConfig;
    @BindView(R.id.item_sharpness_layout_usb_micro_config)
    ConfigComLayout itemSharpnessLayoutUsbMicroConfig;
    @BindView(R.id.item_gamma_layout_usb_micro_config)
    ConfigComLayout itemGammaLayoutUsbMicroConfig;
    @BindView(R.id.item_gain_layout_usb_micro_config)
    ConfigComLayout itemGainLayoutUsbMicroConfig;

    private Context context;

    public UsbMicroConfigLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public UsbMicroConfigLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public UsbMicroConfigLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_usb_micro_config, this, true);
        ButterKnife.bind(this, view);
    }

    public void setOnMirrorFlipListener(MirrorFlipLayout.OnMirrorFlipListener listener) {
        itemFlipLayoutUsbMicroConfig.setOnMirrorFlipListener(listener);
    }

    public void setOnMixConfigListener(ConfigMixLayout.OnMixConfigListener listener) {
        itemFocusLayoutUsbMicroConfig.setOnMixConfigListener(listener);
        itemWhiteBalanceLayoutUsbMicroConfig.setOnMixConfigListener(listener);
        itemExposureLayoutUsbMicroConfig.setOnMixConfigListener(listener);
    }

    public void setOnComConfigListener(ConfigComLayout.OnComConfigListener listener) {
        itemBrightnessLayoutUsbMicroConfig.setOnComConfigListener(listener);
        itemContrastLayoutUsbMicroConfig.setOnComConfigListener(listener);
        itemHueLayoutUsbMicroConfig.setOnComConfigListener(listener);
        itemSaturationLayoutUsbMicroConfig.setOnComConfigListener(listener);
        itemSharpnessLayoutUsbMicroConfig.setOnComConfigListener(listener);
        itemGammaLayoutUsbMicroConfig.setOnComConfigListener(listener);
        itemGainLayoutUsbMicroConfig.setOnComConfigListener(listener);
    }

    public MirrorFlipLayout getItemFlip() {
        return itemFlipLayoutUsbMicroConfig;
    }

    public ConfigMixLayout getItemFocus() {
        return itemFocusLayoutUsbMicroConfig;
    }

    public ConfigMixLayout getItemWhiteBalance() {
        return itemWhiteBalanceLayoutUsbMicroConfig;
    }

    public ConfigMixLayout getItemExposure() {
        return itemExposureLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemBrightness() {
        return itemBrightnessLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemContrast() {
        return itemContrastLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemHue() {
        return itemHueLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemSaturation() {
        return itemSaturationLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemSharpness() {
        return itemSharpnessLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemGamma() {
        return itemGammaLayoutUsbMicroConfig;
    }

    public ConfigComLayout getItemGain() {
        return itemGainLayoutUsbMicroConfig;
    }
}
