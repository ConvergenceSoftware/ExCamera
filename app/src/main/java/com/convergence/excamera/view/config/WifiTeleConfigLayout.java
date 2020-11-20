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
import com.convergence.excamera.view.config.component.TeleFocusLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 望远相机模块-WiFi连接参数布局
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class WifiTeleConfigLayout extends LinearLayout {

    @BindView(R.id.item_flip_layout_wifi_tele_config)
    MirrorFlipLayout itemFlipLayoutWifiTeleConfig;
    @BindView(R.id.item_focus_layout_wifi_tele_config)
    TeleFocusLayout itemFocusLayoutWifiTeleConfig;
    @BindView(R.id.item_white_balance_layout_wifi_tele_config)
    ConfigMixLayout itemWhiteBalanceLayoutWifiTeleConfig;
    @BindView(R.id.item_exposure_layout_wifi_tele_config)
    ConfigMixLayout itemExposureLayoutWifiTeleConfig;
    @BindView(R.id.item_brightness_layout_wifi_tele_config)
    ConfigComLayout itemBrightnessLayoutWifiTeleConfig;
    @BindView(R.id.item_contrast_layout_wifi_tele_config)
    ConfigComLayout itemContrastLayoutWifiTeleConfig;
    @BindView(R.id.item_hue_layout_wifi_tele_config)
    ConfigComLayout itemHueLayoutWifiTeleConfig;
    @BindView(R.id.item_saturation_layout_wifi_tele_config)
    ConfigComLayout itemSaturationLayoutWifiTeleConfig;
    @BindView(R.id.item_sharpness_layout_wifi_tele_config)
    ConfigComLayout itemSharpnessLayoutWifiTeleConfig;
    @BindView(R.id.item_gamma_layout_wifi_tele_config)
    ConfigComLayout itemGammaLayoutWifiTeleConfig;
    @BindView(R.id.item_gain_layout_wifi_tele_config)
    ConfigComLayout itemGainLayoutWifiTeleConfig;
    @BindView(R.id.item_quality_layout_wifi_tele_config)
    ConfigComLayout itemQualityLayoutWifiTeleConfig;

    private Context context;

    public WifiTeleConfigLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WifiTeleConfigLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public WifiTeleConfigLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_wifi_tele_config, this, true);
        ButterKnife.bind(this, view);
    }

    public void setOnMirrorFlipListener(MirrorFlipLayout.OnMirrorFlipListener listener) {
        itemFlipLayoutWifiTeleConfig.setOnMirrorFlipListener(listener);
    }

    public void setOnTeleFocusListener(TeleFocusLayout.OnTeleFocusListener listener) {
        itemFocusLayoutWifiTeleConfig.setOnTeleFocusListener(listener);
    }

    public void setOnMixConfigListener(ConfigMixLayout.OnMixConfigListener listener) {
        itemWhiteBalanceLayoutWifiTeleConfig.setOnMixConfigListener(listener);
        itemExposureLayoutWifiTeleConfig.setOnMixConfigListener(listener);
    }

    public void setOnComConfigListener(ConfigComLayout.OnComConfigListener listener) {
        itemBrightnessLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemContrastLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemHueLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemSaturationLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemSharpnessLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemGammaLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemGainLayoutWifiTeleConfig.setOnComConfigListener(listener);
        itemQualityLayoutWifiTeleConfig.setOnComConfigListener(listener);
    }

    public MirrorFlipLayout getItemFlip() {
        return itemFlipLayoutWifiTeleConfig;
    }

    public TeleFocusLayout getItemFocus() {
        return itemFocusLayoutWifiTeleConfig;
    }

    public ConfigMixLayout getItemWhiteBalance() {
        return itemWhiteBalanceLayoutWifiTeleConfig;
    }

    public ConfigMixLayout getItemExposure() {
        return itemExposureLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemBrightness() {
        return itemBrightnessLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemContrast() {
        return itemContrastLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemHue() {
        return itemHueLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemSaturation() {
        return itemSaturationLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemSharpness() {
        return itemSharpnessLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemGamma() {
        return itemGammaLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemGain() {
        return itemGainLayoutWifiTeleConfig;
    }

    public ConfigComLayout getItemQuality() {
        return itemQualityLayoutWifiTeleConfig;
    }
}
