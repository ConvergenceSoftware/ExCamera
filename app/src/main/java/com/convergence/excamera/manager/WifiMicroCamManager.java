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
import com.convergence.excamera.sdk.wifi.WifiCameraState;
import com.convergence.excamera.sdk.wifi.config.base.WifiAutoConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiParamConfig;
import com.convergence.excamera.sdk.wifi.core.WifiCameraCommand;
import com.convergence.excamera.sdk.wifi.core.WifiCameraController;
import com.convergence.excamera.sdk.wifi.core.WifiCameraView;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraParam;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraResolution;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSP;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSetting;
import com.convergence.excamera.view.config.WifiMicroConfigLayout;
import com.convergence.excamera.view.config.component.ConfigComLayout;
import com.convergence.excamera.view.config.component.ConfigMixLayout;
import com.convergence.excamera.view.config.component.MirrorFlipLayout;
import com.convergence.excamera.view.resolution.ResolutionDialog;
import com.convergence.excamera.view.resolution.ResolutionOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 显微相机模块-WiFi连接功能封装管理类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-10
 * @Organization Convergence Ltd.
 */
public class WifiMicroCamManager implements CamManager, MirrorFlipLayout.OnMirrorFlipListener,
        ConfigMixLayout.OnMixConfigListener, ConfigComLayout.OnComConfigListener, WifiCameraController.OnControlListener,
        OnCameraPhotographListener, OnCameraRecordListener, OnCameraTLRecordListener, OnCameraStackAvgListener {

    private Context context;
    private WifiCameraView wifiCameraView;
    private WifiMicroConfigLayout configLayout;
    private TextView recordTimeText;
    private TextView fpsText;

    private WifiCameraController wifiCameraController;

    private WifiMicroCamManager(Builder builder) {
        this.context = builder.context;
        this.wifiCameraView = builder.wifiCameraView;
        this.configLayout = builder.configLayout;
        this.recordTimeText = builder.recordTimeText;
        this.fpsText = builder.fpsText;
        init();
    }

    private void init() {
        wifiCameraController = new WifiCameraController(context, wifiCameraView);
        wifiCameraController.setOnControlListener(this);
        wifiCameraController.setOnCameraPhotographListener(this);
        wifiCameraController.setOnCameraRecordListener(this);
        wifiCameraController.setOnCameraTLRecordListener(this);
        wifiCameraController.setOnCameraStackAvgListener(this);
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
        wifiCameraController.startStream();
    }

    @Override
    public void onResume() {
        wifiCameraController.startPreview();
    }

    @Override
    public void onPause() {
        wifiCameraController.stopPreview();
    }

    @Override
    public void onStop() {
        wifiCameraController.stopStream();
    }

    @Override
    public void onDestroy() {
        wifiCameraController.release();
    }

    @Override
    public void takePhoto() {
        wifiCameraController.takePhoto();
    }

    @Override
    public void startRecord() {
        wifiCameraController.startRecord();
    }

    @Override
    public void stopRecord() {
        wifiCameraController.stopRecord();
    }

    @Override
    public void startTLRecord(int timeLapseRate) {
        wifiCameraController.startTLRecord(timeLapseRate);
    }

    @Override
    public void stopTLRecord() {
        wifiCameraController.stopTLRecord();
    }

    @Override
    public void startStackAvg() {
        wifiCameraController.startStackAvg();
    }

    @Override
    public void cancelStackAvg() {
        wifiCameraController.cancelStackAvg();
    }

    @Override
    public void showResolutionSelection() {
        WifiCameraSetting wifiCameraSetting = WifiCameraSetting.getInstance();
        if (!isPreviewing() || !wifiCameraSetting.isAvailable()) {
            return;
        }
        WifiCameraResolution wifiCameraResolution = wifiCameraSetting.getWifiCameraParam().getWifiCameraResolution();
        List<WifiCameraResolution.Resolution> resolutionList = wifiCameraResolution.getResolutionList();
        List<ResolutionOption> optionList = new ArrayList<>();
        WifiCameraResolution.Resolution curResolution = wifiCameraResolution.getCurResolution();
        Size curSize = new Size(curResolution.getWidth(), curResolution.getHeight());
        for (WifiCameraResolution.Resolution resolution : resolutionList) {
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
        wifiCameraController.updateResolution(width, height, new WifiCameraCommand.OnResolutionUpdateListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess() {
                IApp.showToast("Done : " + width + " * " + height);
            }

            @Override
            public void onFail() {
                IApp.showToast("Fail");
            }
        });
    }

    @Override
    public boolean isPreviewing() {
        return wifiCameraController.isPreviewing();
    }

    @Override
    public boolean isRecording() {
        return wifiCameraController.getCurActionState() == ActionState.Recording;
    }

    @Override
    public boolean isTLRecording() {
        return wifiCameraController.getCurActionState() == ActionState.TLRecording;
    }

    /**
     * 重置所有参数布局
     */
    private void resetConfigLayout() {
        if (configLayout == null) {
            return;
        }
        WifiCameraSP.Editor editor = WifiCameraSP.getEditor(context);
        MirrorFlipLayout itemFlip = configLayout.getItemFlip();
        itemFlip.initSwitch(editor.isFlipHorizontal(), editor.isFlipVertical());
        if (!WifiCameraSetting.getInstance().isAvailable()) {
            return;
        }
        resetFocusLayout();
        resetWhiteBalanceLayout();
        resetExposureLayout();
        resetConfigComLayout(configLayout.getItemBrightness(), WifiConfig.TAG_PARAM_BRIGHTNESS);
        resetConfigComLayout(configLayout.getItemContrast(), WifiConfig.TAG_PARAM_CONTRAST);
        resetConfigComLayout(configLayout.getItemHue(), WifiConfig.TAG_PARAM_HUE);
        resetConfigComLayout(configLayout.getItemSaturation(), WifiConfig.TAG_PARAM_SATURATION);
        resetConfigComLayout(configLayout.getItemSharpness(), WifiConfig.TAG_PARAM_SHARPNESS);
        resetConfigComLayout(configLayout.getItemGamma(), WifiConfig.TAG_PARAM_GAMMA);
        resetConfigComLayout(configLayout.getItemGain(), WifiConfig.TAG_PARAM_GAIN);
        resetConfigComLayout(configLayout.getItemQuality(), WifiConfig.TAG_PARAM_JPEG_QUALITY);
    }

    /**
     * 重置对焦参数布局
     */
    private void resetFocusLayout() {
        WifiAutoConfig focusAuto = wifiCameraController.getAutoConfig(WifiConfig.TAG_AUTO_FOCUS_AUTO);
        WifiParamConfig focus = wifiCameraController.getParamConfig(WifiConfig.TAG_PARAM_FOCUS);
        ConfigMixLayout itemFocus = configLayout.getItemFocus();
        if (focusAuto != null && focus != null) {
            itemFocus.setVisibility(View.VISIBLE);
            itemFocus.resetData(focusAuto.isAuto(), focus.getMin(), focus.getMax(), focus.getCur());
        } else {
            itemFocus.setVisibility(View.GONE);
        }
    }

    /**
     * 重置白平衡参数布局
     */
    private void resetWhiteBalanceLayout() {
        WifiAutoConfig whiteBalanceAuto = wifiCameraController.getAutoConfig(WifiConfig.TAG_AUTO_WHITE_BALANCE_AUTO);
        WifiParamConfig whiteBalance = wifiCameraController.getParamConfig(WifiConfig.TAG_PARAM_WHITE_BALANCE);
        ConfigMixLayout itemWhiteBalance = configLayout.getItemWhiteBalance();
        if (whiteBalanceAuto != null && whiteBalance != null) {
            itemWhiteBalance.setVisibility(View.VISIBLE);
            itemWhiteBalance.resetData(whiteBalanceAuto.isAuto(), whiteBalance.getMin(), whiteBalance.getMax(), whiteBalance.getCur());
        } else {
            itemWhiteBalance.setVisibility(View.GONE);
        }
    }

    /**
     * 重置曝光参数布局
     */
    private void resetExposureLayout() {
        WifiAutoConfig exposureAuto = wifiCameraController.getAutoConfig(WifiConfig.TAG_AUTO_EXPOSURE_AUTO);
        WifiParamConfig exposure = wifiCameraController.getParamConfig(WifiConfig.TAG_PARAM_EXPOSURE);
        ConfigMixLayout itemExposure = configLayout.getItemExposure();
        if (exposureAuto != null && exposure != null) {
            itemExposure.setVisibility(View.VISIBLE);
            itemExposure.resetData(exposureAuto.isAuto(), exposure.getMin(), exposure.getMax(), exposure.getCur());
        } else {
            itemExposure.setVisibility(View.GONE);
        }
    }

    /**
     * 重置通用参数布局
     *
     * @param layout 通用参数布局
     * @param tag    对应WifiConfig标识
     */
    private void resetConfigComLayout(ConfigComLayout layout, String tag) {
        WifiParamConfig config = wifiCameraController.getParamConfig(tag);
        if (config != null) {
            layout.setVisibility(View.VISIBLE);
            layout.resetData(config.getMin(), config.getMax(), config.getCur());
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
        if (WifiCameraSetting.getInstance().isAvailable()) {
            wifiCameraController.setAuto(tag, isAuto);
        }
    }

    /**
     * 设置参数值
     *
     * @param tag   对应UVCConfig标识
     * @param value 参数值
     */
    private void setConfigParam(String tag, int value) {
        if (WifiCameraSetting.getInstance().isAvailable()) {
            wifiCameraController.setParam(tag, value);
        }
    }

    /**
     * 重置参数值
     *
     * @param tag      对应UVCConfig标识
     * @param listener 重置监听
     */
    private void resetConfigParam(String tag, @Nullable OnConfigResetListener listener) {
        if (WifiCameraSetting.getInstance().isAvailable()) {
            wifiCameraController.resetConfig(tag);
            int value = wifiCameraController.getParam(tag);
            if (listener != null) {
                listener.onResetDone(value);
            }
        }
    }

    @Override
    public void onFlipCheckedChange(boolean isFlipHorizontal, boolean isFlipVertical) {
        WifiCameraSP.Editor editor = WifiCameraSP.getEditor(context);
        editor.setIsFlipHorizontal(isFlipHorizontal);
        editor.setIsFlipVertical(isFlipVertical);
    }

    @Override
    public void onMixConfigAutoChange(ConfigMixLayout view, boolean isAuto) {
        switch (view.getConfigType()) {
            case Focus:
                setConfigAuto(WifiConfig.TAG_AUTO_FOCUS_AUTO, isAuto);
                break;
            case WhiteBalance:
                setConfigAuto(WifiConfig.TAG_AUTO_WHITE_BALANCE_AUTO, isAuto);
                break;
            case Exposure:
                setConfigAuto(WifiConfig.TAG_AUTO_EXPOSURE_AUTO, isAuto);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMixConfigProgressChange(ConfigMixLayout view, int value, int percent) {
        switch (view.getConfigType()) {
            case Focus:
                setConfigParam(WifiConfig.TAG_PARAM_FOCUS, value);
                break;
            case WhiteBalance:
                setConfigParam(WifiConfig.TAG_PARAM_WHITE_BALANCE, value);
                break;
            case Exposure:
                setConfigParam(WifiConfig.TAG_PARAM_EXPOSURE, value);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMixConfigReset(ConfigMixLayout view) {
        switch (view.getConfigType()) {
            case Focus:
                resetConfigParam(WifiConfig.TAG_PARAM_FOCUS, view::setSeekBarValue);
                break;
            case WhiteBalance:
                resetConfigParam(WifiConfig.TAG_PARAM_WHITE_BALANCE, view::setSeekBarValue);
                break;
            case Exposure:
                resetConfigParam(WifiConfig.TAG_PARAM_EXPOSURE, view::setSeekBarValue);
                break;
            default:
                break;
        }
    }

    @Override
    public void onComConfigProgressChange(ConfigComLayout view, int value, int percent) {
        switch (view.getConfigType()) {
            case Brightness:
                setConfigParam(WifiConfig.TAG_PARAM_BRIGHTNESS, value);
                break;
            case Contrast:
                setConfigParam(WifiConfig.TAG_PARAM_CONTRAST, value);
                break;
            case Hue:
                setConfigParam(WifiConfig.TAG_PARAM_HUE, value);
                break;
            case Saturation:
                setConfigParam(WifiConfig.TAG_PARAM_SATURATION, value);
                break;
            case Sharpness:
                setConfigParam(WifiConfig.TAG_PARAM_SHARPNESS, value);
                break;
            case Gamma:
                setConfigParam(WifiConfig.TAG_PARAM_GAMMA, value);
                break;
            case Gain:
                setConfigParam(WifiConfig.TAG_PARAM_GAIN, value);
                break;
            case Quality:
                setConfigParam(WifiConfig.TAG_PARAM_JPEG_QUALITY, value);
                break;
            default:
                break;
        }
    }

    @Override
    public void onComConfigReset(ConfigComLayout view) {
        switch (view.getConfigType()) {
            case Brightness:
                resetConfigParam(WifiConfig.TAG_PARAM_BRIGHTNESS, view::setSeekBarValue);
                break;
            case Contrast:
                resetConfigParam(WifiConfig.TAG_PARAM_CONTRAST, view::setSeekBarValue);
                break;
            case Hue:
                resetConfigParam(WifiConfig.TAG_PARAM_HUE, view::setSeekBarValue);
                break;
            case Saturation:
                resetConfigParam(WifiConfig.TAG_PARAM_SATURATION, view::setSeekBarValue);
                break;
            case Sharpness:
                resetConfigParam(WifiConfig.TAG_PARAM_SHARPNESS, view::setSeekBarValue);
                break;
            case Gamma:
                resetConfigParam(WifiConfig.TAG_PARAM_GAMMA, view::setSeekBarValue);
                break;
            case Gain:
                resetConfigParam(WifiConfig.TAG_PARAM_GAIN, view::setSeekBarValue);
                break;
            case Quality:
                resetConfigParam(WifiConfig.TAG_PARAM_JPEG_QUALITY, view::setSeekBarValue);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStreamStart(boolean isRetry) {
        if (fpsText != null) {
            fpsText.setText(OutputUtil.getAverageFPSText(0));
            fpsText.setVisibility(View.VISIBLE);
        }
        IApp.showToast(isRetry ? "Restore" : "Connect");
    }

    @Override
    public void onStreamStop(boolean isRetry) {
        if (fpsText != null) {
            fpsText.setVisibility(View.GONE);
        }
        IApp.showToast(isRetry ? "Retry" : "DisConnect");
    }

    @Override
    public void onWifiStateUpdate(WifiCameraState state) {

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
    public void onParamUpdate(WifiCameraParam param, boolean isReset) {
        if (isReset) {
            resetConfigLayout();
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
        private WifiCameraView wifiCameraView;
        private WifiMicroConfigLayout configLayout;
        private TextView recordTimeText;
        private TextView fpsText;

        public Builder(Context context, WifiCameraView wifiCameraView) {
            this.context = context;
            this.wifiCameraView = wifiCameraView;
        }

        public Builder bindConfigLayout(WifiMicroConfigLayout configLayout) {
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

        public WifiMicroCamManager build() {
            return new WifiMicroCamManager(this);
        }
    }
}
