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
 * 望远相机模块-USB连接参数布局
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbTeleConfigLayout extends LinearLayout {

    @BindView(R.id.item_flip_layout_usb_tele_config)
    MirrorFlipLayout itemFlipLayoutUsbTeleConfig;
    @BindView(R.id.item_focus_layout_usb_tele_config)
    TeleFocusLayout itemFocusLayoutUsbTeleConfig;
    @BindView(R.id.item_white_balance_layout_usb_tele_config)
    ConfigMixLayout itemWhiteBalanceLayoutUsbTeleConfig;
    @BindView(R.id.item_exposure_layout_usb_tele_config)
    ConfigMixLayout itemExposureLayoutUsbTeleConfig;
    @BindView(R.id.item_brightness_layout_usb_tele_config)
    ConfigComLayout itemBrightnessLayoutUsbTeleConfig;
    @BindView(R.id.item_contrast_layout_usb_tele_config)
    ConfigComLayout itemContrastLayoutUsbTeleConfig;
    @BindView(R.id.item_hue_layout_usb_tele_config)
    ConfigComLayout itemHueLayoutUsbTeleConfig;
    @BindView(R.id.item_saturation_layout_usb_tele_config)
    ConfigComLayout itemSaturationLayoutUsbTeleConfig;
    @BindView(R.id.item_sharpness_layout_usb_tele_config)
    ConfigComLayout itemSharpnessLayoutUsbTeleConfig;
    @BindView(R.id.item_gamma_layout_usb_tele_config)
    ConfigComLayout itemGammaLayoutUsbTeleConfig;
    @BindView(R.id.item_gain_layout_usb_tele_config)
    ConfigComLayout itemGainLayoutUsbTeleConfig;

    private Context context;

    public UsbTeleConfigLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public UsbTeleConfigLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public UsbTeleConfigLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_usb_tele_config, this, true);
        ButterKnife.bind(this, view);
    }

    public void setOnMirrorFlipListener(MirrorFlipLayout.OnMirrorFlipListener listener) {
        itemFlipLayoutUsbTeleConfig.setOnMirrorFlipListener(listener);
    }

    public void setOnTeleFocusListener(TeleFocusLayout.OnTeleFocusListener listener) {
        itemFocusLayoutUsbTeleConfig.setOnTeleFocusListener(listener);
    }

    public void setOnMixConfigListener(ConfigMixLayout.OnMixConfigListener listener) {
        itemWhiteBalanceLayoutUsbTeleConfig.setOnMixConfigListener(listener);
        itemExposureLayoutUsbTeleConfig.setOnMixConfigListener(listener);
    }

    public void setOnComConfigListener(ConfigComLayout.OnComConfigListener listener) {
        itemBrightnessLayoutUsbTeleConfig.setOnComConfigListener(listener);
        itemContrastLayoutUsbTeleConfig.setOnComConfigListener(listener);
        itemHueLayoutUsbTeleConfig.setOnComConfigListener(listener);
        itemSaturationLayoutUsbTeleConfig.setOnComConfigListener(listener);
        itemSharpnessLayoutUsbTeleConfig.setOnComConfigListener(listener);
        itemGammaLayoutUsbTeleConfig.setOnComConfigListener(listener);
        itemGainLayoutUsbTeleConfig.setOnComConfigListener(listener);
    }

    public MirrorFlipLayout getItemFlip() {
        return itemFlipLayoutUsbTeleConfig;
    }

    public TeleFocusLayout getItemFocus() {
        return itemFocusLayoutUsbTeleConfig;
    }

    public ConfigMixLayout getItemWhiteBalance() {
        return itemWhiteBalanceLayoutUsbTeleConfig;
    }

    public ConfigMixLayout getItemExposure() {
        return itemExposureLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemBrightness() {
        return itemBrightnessLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemContrast() {
        return itemContrastLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemHue() {
        return itemHueLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemSaturation() {
        return itemSaturationLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemSharpness() {
        return itemSharpnessLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemGamma() {
        return itemGammaLayoutUsbTeleConfig;
    }

    public ConfigComLayout getItemGain() {
        return itemGainLayoutUsbTeleConfig;
    }
}
