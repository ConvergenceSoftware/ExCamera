package com.convergence.excamera.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.convergence.excamera.IApp;
import com.convergence.excamera.sdk.common.ActionState;
import com.convergence.excamera.sdk.common.OutputUtil;
import com.convergence.excamera.sdk.common.callback.OnCameraPhotographListener;
import com.convergence.excamera.sdk.common.callback.OnCameraRecordListener;
import com.convergence.excamera.sdk.common.callback.OnCameraStackAvgListener;
import com.convergence.excamera.sdk.common.callback.OnCameraTLRecordListener;
import com.convergence.excamera.sdk.usb.UsbCameraState;
import com.convergence.excamera.sdk.usb.core.UsbCameraController;
import com.convergence.excamera.sdk.usb.core.UsbCameraView;
import com.convergence.excamera.sdk.usb.entity.UsbCameraResolution;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSP;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSetting;
import com.convergence.excamera.view.config.UsbMicroConfigLayout;
import com.convergence.excamera.view.config.component.ConfigComLayout;
import com.convergence.excamera.view.config.component.ConfigMixLayout;
import com.convergence.excamera.view.config.component.MirrorFlipLayout;
import com.convergence.excamera.view.resolution.ResolutionDialog;
import com.convergence.excamera.view.resolution.ResolutionOption;
import com.serenegiant.usb.config.base.UVCConfig;
import com.serenegiant.usb.config.base.UVCParamConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 显微相机模块-USB连接功能封装管理类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbMicroCamManager implements CamManager, MirrorFlipLayout.OnMirrorFlipListener,
        ConfigMixLayout.OnMixConfigListener, ConfigComLayout.OnComConfigListener,
        UsbCameraController.OnControlListener, OnCameraPhotographListener,
        OnCameraRecordListener, OnCameraTLRecordListener, OnCameraStackAvgListener {

    private static final int EXPOSURE_MODE_AUTO = 8;
    private static final int EXPOSURE_MODE_MANUAL = 1;

    private Context context;
    private UsbCameraView usbCameraView;
    private UsbMicroConfigLayout configLayout;
    private TextView recordTimeText;
    private TextView fpsText;

    private UsbCameraController usbCameraController;

    private UsbMicroCamManager(Builder builder) {
        this.context = builder.context;
        this.usbCameraView = builder.usbCameraView;
        this.configLayout = builder.configLayout;
        this.recordTimeText = builder.recordTimeText;
        this.fpsText = builder.fpsText;
        init();
    }

    private void init() {
        usbCameraController = new UsbCameraController(context, usbCameraView);
        usbCameraController.setOnControlListener(this);
        usbCameraController.setOnCameraPhotographListener(this);
        usbCameraController.setOnCameraRecordListener(this);
        usbCameraController.setOnCameraTLRecordListener(this);
        usbCameraController.setOnCameraStackAvgListener(this);
        if (configLayout != null) {
            configLayout.setOnMirrorFlipListener(this);
            configLayout.setOnMixConfigListener(this);
            configLayout.setOnComConfigListener(this);
        }
        if (recordTimeText != null) {
            recordTimeText.setText(OutputUtil.getRecordTimeText(0));
            recordTimeText.setVisibility(View.GONE);
        }
        if (fpsText != null) {
            fpsText.setText(OutputUtil.getAverageFPSText(0));
            fpsText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        usbCameraController.registerUsb();
    }

    @Override
    public void onResume() {
        usbCameraController.startPreview();
    }

    @Override
    public void onPause() {
        usbCameraController.stopPreview();
    }

    @Override
    public void onStop() {
        usbCameraController.unregisterUsb();
    }

    @Override
    public void onDestroy() {
        usbCameraController.release();
    }

    @Override
    public void takePhoto() {
        usbCameraController.takePhoto();
    }

    @Override
    public void startRecord() {
        usbCameraController.startRecord();
    }

    @Override
    public void stopRecord() {
        usbCameraController.stopRecord();
    }

    @Override
    public void startTLRecord(int timeLapseRate) {
        usbCameraController.startTLRecord(timeLapseRate);
    }

    @Override
    public void stopTLRecord() {
        usbCameraController.stopTLRecord();
    }

    @Override
    public void startStackAvg() {
        usbCameraController.startStackAvg();
    }

    @Override
    public void cancelStackAvg() {
        usbCameraController.cancelStackAvg();
    }

    @Override
    public void showResolutionSelection() {
        UsbCameraSetting usbCameraSetting = UsbCameraSetting.getInstance();
        if (!isPreviewing() || !usbCameraSetting.isAvailable()) {
            return;
        }
        UsbCameraResolution usbCameraResolution = usbCameraSetting.getUsbCameraResolution();
        List<UsbCameraResolution.Resolution> resolutionList = usbCameraResolution.getResolutionList();
        List<ResolutionOption> optionList = new ArrayList<>();
        UsbCameraResolution.Resolution curResolution = usbCameraResolution.getCurResolution();
        Size curSize = new Size(curResolution.getWidth(), curResolution.getHeight());
        for (UsbCameraResolution.Resolution resolution : resolutionList) {
            ResolutionOption option = new ResolutionOption(resolution.getWidth(), resolution.getHeight());
            option.setSelect(option.equals(curSize.getWidth(), curSize.getHeight()));
            option.setDefault(resolution.isDefault());
            optionList.add(option);
        }
        ResolutionDialog dialog = new ResolutionDialog(context, optionList, curSize,
                resultSize -> updateResolution(resultSize.getWidth(), resultSize.getHeight()));
        dialog.show();
    }

    @Override
    public void updateResolution(int width, int height) {
        usbCameraController.updateResolution(width, height);
    }

    @Override
    public boolean isPreviewing() {
        return usbCameraController.isPreviewing();
    }

    @Override
    public boolean isRecording() {
        return usbCameraController.getCurActionState() == ActionState.Recording;
    }

    @Override
    public boolean isTLRecording() {
        return usbCameraController.getCurActionState() == ActionState.TLRecording;
    }

    /**
     * 重置所有参数布局
     */
    private void resetConfigLayout() {
        if (configLayout == null) {
            return;
        }
        UsbCameraSP.Editor editor = UsbCameraSP.getEditor(context);
        MirrorFlipLayout itemFlip = configLayout.getItemFlip();
        itemFlip.initSwitch(editor.isFlipHorizontal(), editor.isFlipVertical());
        if (!isPreviewing()) {
            return;
        }
        resetFocusLayout();
        resetWhiteBalanceLayout();
        resetExposureLayout();
        resetConfigComLayout(configLayout.getItemBrightness(), UVCConfig.TAG_PARAM_BRIGHTNESS);
        resetConfigComLayout(configLayout.getItemContrast(), UVCConfig.TAG_PARAM_CONTRAST);
        resetConfigComLayout(configLayout.getItemHue(), UVCConfig.TAG_PARAM_HUE);
        resetConfigComLayout(configLayout.getItemSaturation(), UVCConfig.TAG_PARAM_SATURATION);
        resetConfigComLayout(configLayout.getItemSharpness(), UVCConfig.TAG_PARAM_SHARPNESS);
        resetConfigComLayout(configLayout.getItemGamma(), UVCConfig.TAG_PARAM_GAMMA);
        resetConfigComLayout(configLayout.getItemGain(), UVCConfig.TAG_PARAM_GAIN);
    }

    /**
     * 重置对焦参数布局
     */
    private void resetFocusLayout() {
        ConfigMixLayout itemFocus = configLayout.getItemFocus();
        if (usbCameraController.checkConfigEnable(UVCConfig.TAG_AUTO_FOCUS_AUTO) &&
                usbCameraController.checkConfigEnable(UVCConfig.TAG_PARAM_FOCUS)) {
            itemFocus.setVisibility(View.VISIBLE);
            setConfigAuto(UVCConfig.TAG_AUTO_FOCUS_AUTO, true);
            UVCParamConfig focus = usbCameraController.getParamConfig(UVCConfig.TAG_PARAM_FOCUS);
            if (focus != null) {
                itemFocus.resetData(true, focus.getMin(), focus.getMax(), focus.getCur());
            }
        } else {
            itemFocus.setVisibility(View.GONE);
        }
    }

    /**
     * 重置白平衡参数布局
     */
    private void resetWhiteBalanceLayout() {
        ConfigMixLayout itemWhiteBalance = configLayout.getItemWhiteBalance();
        if (usbCameraController.checkConfigEnable(UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO) &&
                usbCameraController.checkConfigEnable(UVCConfig.TAG_PARAM_WHITE_BALANCE)) {
            itemWhiteBalance.setVisibility(View.VISIBLE);
            setConfigAuto(UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO, true);
            UVCParamConfig whiteBalance = usbCameraController.getParamConfig(UVCConfig.TAG_PARAM_WHITE_BALANCE);
            if (whiteBalance != null) {
                itemWhiteBalance.resetData(true, whiteBalance.getMin(), whiteBalance.getMax(), whiteBalance.getCur());
            }
        } else {
            itemWhiteBalance.setVisibility(View.GONE);
        }
    }

    /**
     * 重置曝光参数布局
     */
    private void resetExposureLayout() {
        ConfigMixLayout itemExposure = configLayout.getItemExposure();
        if (usbCameraController.checkConfigEnable(UVCConfig.TAG_PARAM_EXPOSURE_MODE) &&
                usbCameraController.checkConfigEnable(UVCConfig.TAG_PARAM_EXPOSURE)) {
            itemExposure.setVisibility(View.VISIBLE);
            setConfigParam(UVCConfig.TAG_PARAM_EXPOSURE_MODE, EXPOSURE_MODE_AUTO);
            UVCParamConfig exposure = usbCameraController.getParamConfig(UVCConfig.TAG_PARAM_EXPOSURE);
            if (exposure != null) {
                itemExposure.resetData(true, exposure.getMin(), exposure.getMax(), exposure.getCur());
            }
        } else {
            itemExposure.setVisibility(View.GONE);
        }
    }

    /**
     * 重置通用参数布局
     *
     * @param layout 通用参数布局
     * @param tag    对应UVCConfig标识
     */
    private void resetConfigComLayout(ConfigComLayout layout, String tag) {
        if (usbCameraController.checkConfigEnable(tag)) {
            layout.setVisibility(View.VISIBLE);
            UVCParamConfig config = usbCameraController.getParamConfig(tag);
            if (config != null) {
                layout.resetData(config.getMin(), config.getMax(), config.getCur());
            }
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置参数是否自动
     *
     * @param tag    对应UVCConfig标识
     * @param isAuto 是否自动
     */
    private void setConfigAuto(String tag, boolean isAuto) {
        if (isPreviewing() && usbCameraController.checkConfigEnable(tag)) {
            usbCameraController.setAuto(tag, isAuto);
        }
    }

    /**
     * 设置参数值
     *
     * @param tag   对应UVCConfig标识
     * @param value 参数值
     */
    private void setConfigParam(String tag, int value) {
        if (isPreviewing() && usbCameraController.checkConfigEnable(tag)) {
            usbCameraController.setParam(tag, value);
        }
    }

    /**
     * 重置参数值
     *
     * @param tag      对应UVCConfig标识
     * @param listener 重置监听
     */
    private void resetConfigParam(String tag, @Nullable OnConfigResetListener listener) {
        if (isPreviewing() && usbCameraController.checkConfigEnable(tag)) {
            usbCameraController.resetParam(tag);
            int value = usbCameraController.getParam(tag);
            if (listener != null) {
                listener.onResetDone(value);
            }
        }
    }

    @Override
    public void onUsbConnect() {

    }

    @Override
    public void onUsbDisConnect() {

    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraClose() {

    }

    @Override
    public void onPreviewStart() {
        if (fpsText != null) {
            fpsText.setText(OutputUtil.getAverageFPSText(0));
            fpsText.setVisibility(View.VISIBLE);
        }
        resetConfigLayout();
    }

    @Override
    public void onPreviewStop() {
        if (fpsText != null) {
            fpsText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUsbStateUpdate(UsbCameraState state) {

    }

    @Override
    public void onActionStateUpdate(ActionState state) {

    }

    @Override
    public void onLoadFrame(Bitmap bitmap) {

    }

    @Override
    public void onLoadFPS(int instantFPS, float averageFPS) {
        if (fpsText != null) {
            fpsText.setText(OutputUtil.getAverageFPSText(averageFPS));
        }
    }

    @Override
    public void onFlipCheckedChange(boolean isFlipHorizontal, boolean isFlipVertical) {
        UsbCameraSP.Editor editor = UsbCameraSP.getEditor(context);
        editor.setIsFlipHorizontal(isFlipHorizontal);
        editor.setIsFlipVertical(isFlipVertical);
        usbCameraController.updateFlip();
    }

    @Override
    public void onMixConfigAutoChange(ConfigMixLayout view, boolean isAuto) {
        switch (view.getConfigType()) {
            case Focus:
                setConfigAuto(UVCConfig.TAG_AUTO_FOCUS_AUTO, isAuto);
                break;
            case WhiteBalance:
                setConfigAuto(UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO, isAuto);
                break;
            case Exposure:
                setConfigParam(UVCConfig.TAG_PARAM_EXPOSURE_MODE, isAuto ? EXPOSURE_MODE_AUTO : EXPOSURE_MODE_MANUAL);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMixConfigProgressChange(ConfigMixLayout view, int value, int percent) {
        switch (view.getConfigType()) {
            case Focus:
                setConfigParam(UVCConfig.TAG_PARAM_FOCUS, value);
                break;
            case WhiteBalance:
                setConfigParam(UVCConfig.TAG_PARAM_WHITE_BALANCE, value);
                break;
            case Exposure:
                setConfigParam(UVCConfig.TAG_PARAM_EXPOSURE, value);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMixConfigReset(ConfigMixLayout view) {
        switch (view.getConfigType()) {
            case Focus:
                resetConfigParam(UVCConfig.TAG_PARAM_FOCUS, view::setSeekBarValue);
                break;
            case WhiteBalance:
                resetConfigParam(UVCConfig.TAG_PARAM_WHITE_BALANCE, view::setSeekBarValue);
                break;
            case Exposure:
                resetConfigParam(UVCConfig.TAG_PARAM_EXPOSURE, view::setSeekBarValue);
                break;
            default:
                break;
        }
    }

    @Override
    public void onComConfigProgressChange(ConfigComLayout view, int value, int percent) {
        switch (view.getConfigType()) {
            case Brightness:
                setConfigParam(UVCConfig.TAG_PARAM_BRIGHTNESS, value);
                break;
            case Contrast:
                setConfigParam(UVCConfig.TAG_PARAM_CONTRAST, value);
                break;
            case Hue:
                setConfigParam(UVCConfig.TAG_PARAM_HUE, value);
                break;
            case Saturation:
                setConfigParam(UVCConfig.TAG_PARAM_SATURATION, value);
                break;
            case Sharpness:
                setConfigParam(UVCConfig.TAG_PARAM_SHARPNESS, value);
                break;
            case Gamma:
                setConfigParam(UVCConfig.TAG_PARAM_GAMMA, value);
                break;
            case Gain:
                setConfigParam(UVCConfig.TAG_PARAM_GAIN, value);
                break;
            default:
                break;
        }
    }

    @Override
    public void onComConfigReset(ConfigComLayout view) {
        switch (view.getConfigType()) {
            case Brightness:
                resetConfigParam(UVCConfig.TAG_PARAM_BRIGHTNESS, view::setSeekBarValue);
                break;
            case Contrast:
                resetConfigParam(UVCConfig.TAG_PARAM_CONTRAST, view::setSeekBarValue);
                break;
            case Hue:
                resetConfigParam(UVCConfig.TAG_PARAM_HUE, view::setSeekBarValue);
                break;
            case Saturation:
                resetConfigParam(UVCConfig.TAG_PARAM_SATURATION, view::setSeekBarValue);
                break;
            case Sharpness:
                resetConfigParam(UVCConfig.TAG_PARAM_SHARPNESS, view::setSeekBarValue);
                break;
            case Gamma:
                resetConfigParam(UVCConfig.TAG_PARAM_GAMMA, view::setSeekBarValue);
                break;
            case Gain:
                resetConfigParam(UVCConfig.TAG_PARAM_GAIN, view::setSeekBarValue);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTakePhotoStart() {

    }

    @Override
    public void onTakePhotoDone() {

    }

    @Override
    public void onTakePhotoSuccess(String path) {
        IApp.showToast("Done : " + path);
    }

    @Override
    public void onTakePhotoFail() {
        IApp.showToast("Fail");
    }

    @Override
    public void onRecordStartSuccess() {
        IApp.showToast("Start");
        if (recordTimeText != null) {
            recordTimeText.setText(OutputUtil.getRecordTimeText(0));
            recordTimeText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecordStartFail() {
        IApp.showToast("Fail");
    }

    @Override
    public void onRecordProgress(int recordSeconds) {
        if (recordTimeText != null) {
            recordTimeText.setText(OutputUtil.getRecordTimeText(recordSeconds));
        }
    }

    @Override
    public void onRecordSuccess(String path) {
        IApp.showToast("Done : " + path);
        if (recordTimeText != null) {
            recordTimeText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecordFail() {
        IApp.showToast("Fail");
        if (recordTimeText != null) {
            recordTimeText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTLRecordStartSuccess() {
        IApp.showToast("Start");
        if (recordTimeText != null) {
            recordTimeText.setText(OutputUtil.getLongRecordTimeText(0));
            recordTimeText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTLRecordStartFail() {
        IApp.showToast("Fail");
    }

    @Override
    public void onTLRecordProgress(int recordSeconds) {
        if (recordTimeText != null) {
            recordTimeText.setText(OutputUtil.getLongRecordTimeText(recordSeconds));
        }
    }

    @Override
    public void onTLRecordSuccess(String path) {
        IApp.showToast("Done : " + path);
        if (recordTimeText != null) {
            recordTimeText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTLRecordFail() {
        IApp.showToast("Fail");
        if (recordTimeText != null) {
            recordTimeText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStackAvgStart() {
        IApp.showToast("Stack Avg Start");
    }

    @Override
    public void onStackAvgCancel() {
        IApp.showToast("Stack Avg Cancel");
    }

    @Override
    public void onStackAvgSuccess(Bitmap bitmap, String path) {
        IApp.showToast("Stack Avg Success : " + path);
    }

    @Override
    public void onStackAvgError(String error) {
        IApp.showToast("Stack Avg Fail");
    }

    public static class Builder {

        private Context context;
        private UsbCameraView usbCameraView;
        private UsbMicroConfigLayout configLayout;
        private TextView recordTimeText;
        private TextView fpsText;

        public Builder(Context context, UsbCameraView usbCameraView) {
            this.context = context;
            this.usbCameraView = usbCameraView;
        }

        public Builder bindConfigLayout(UsbMicroConfigLayout configLayout) {
            this.configLayout = configLayout;
            return this;
        }

        public Builder bindRecordTimeText(TextView recordTimeText) {
            this.recordTimeText = recordTimeText;
            return this;
        }

        public Builder bindFpsText(TextView fpsText) {
            this.fpsText = fpsText;
            return this;
        }

        public UsbMicroCamManager build() {
            return new UsbMicroCamManager(this);
        }
    }
}
