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
 * 显微相机模块-WiFi连接参数布局
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class WifiMicroConfigLayout extends LinearLayout {

    @BindView(R.id.item_flip_layout_wifi_micro_config)
    MirrorFlipLayout itemFlipLayoutWifiMicroConfig;
    @BindView(R.id.item_focus_layout_wifi_micro_config)
    ConfigMixLayout itemFocusLayoutWifiMicroConfig;
    @BindView(R.id.item_white_balance_layout_wifi_micro_config)
    ConfigMixLayout itemWhiteBalanceLayoutWifiMicroConfig;
    @BindView(R.id.item_exposure_layout_wifi_micro_config)
    ConfigMixLayout itemExposureLayoutWifiMicroConfig;
    @BindView(R.id.item_brightness_layout_wifi_micro_config)
    ConfigComLayout itemBrightnessLayoutWifiMicroConfig;
    @BindView(R.id.item_contrast_layout_wifi_micro_config)
    ConfigComLayout itemContrastLayoutWifiMicroConfig;
    @BindView(R.id.item_hue_layout_wifi_micro_config)
    ConfigComLayout itemHueLayoutWifiMicroConfig;
    @BindView(R.id.item_saturation_layout_wifi_micro_config)
    ConfigComLayout itemSaturationLayoutWifiMicroConfig;
    @BindView(R.id.item_sharpness_layout_wifi_micro_config)
    ConfigComLayout itemSharpnessLayoutWifiMicroConfig;
    @BindView(R.id.item_gamma_layout_wifi_micro_config)
    ConfigComLayout itemGammaLayoutWifiMicroConfig;
    @BindView(R.id.item_gain_layout_wifi_micro_config)
    ConfigComLayout itemGainLayoutWifiMicroConfig;
    @BindView(R.id.item_quality_layout_wifi_micro_config)
    ConfigComLayout itemQualityLayoutWifiMicroConfig;

    private Context context;

    public WifiMicroConfigLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WifiMicroConfigLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public WifiMicroConfigLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_wifi_micro_config, this, true);
        ButterKnife.bind(this, view);
    }

    public void setOnMirrorFlipListener(MirrorFlipLayout.OnMirrorFlipListener listener) {
        itemFlipLayoutWifiMicroConfig.setOnMirrorFlipListener(listener);
    }

    public void setOnMixConfigListener(ConfigMixLayout.OnMixConfigListener listener) {
        itemFocusLayoutWifiMicroConfig.setOnMixConfigListener(listener);
        itemWhiteBalanceLayoutWifiMicroConfig.setOnMixConfigListener(listener);
        itemExposureLayoutWifiMicroConfig.setOnMixConfigListener(listener);
    }

    public void setOnComConfigListener(ConfigComLayout.OnComConfigListener listener) {
        itemBrightnessLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemContrastLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemHueLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemSaturationLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemSharpnessLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemGammaLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemGainLayoutWifiMicroConfig.setOnComConfigListener(listener);
        itemQualityLayoutWifiMicroConfig.setOnComConfigListener(listener);
    }

    public MirrorFlipLayout getItemFlip() {
        return itemFlipLayoutWifiMicroConfig;
    }

    public ConfigMixLayout getItemFocus() {
        return itemFocusLayoutWifiMicroConfig;
    }

    public ConfigMixLayout getItemWhiteBalance() {
        return itemWhiteBalanceLayoutWifiMicroConfig;
    }

    public ConfigMixLayout getItemExposure() {
        return itemExposureLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemBrightness() {
        return itemBrightnessLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemContrast() {
        return itemContrastLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemHue() {
        return itemHueLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemSaturation() {
        return itemSaturationLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemSharpness() {
        return itemSharpnessLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemGamma() {
        return itemGammaLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemGain() {
        return itemGainLayoutWifiMicroConfig;
    }

    public ConfigComLayout getItemQuality() {
        return itemQualityLayoutWifiMicroConfig;
    }
}
