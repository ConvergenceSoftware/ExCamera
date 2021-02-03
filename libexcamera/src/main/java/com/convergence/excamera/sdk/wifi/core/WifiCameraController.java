package com.convergence.excamera.sdk.wifi.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.common.ActionState;
import com.convergence.excamera.sdk.common.BaseExCamController;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.common.TeleFocusHelper;
import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.wifi.WifiCameraState;
import com.convergence.excamera.sdk.wifi.config.base.WifiAutoConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiParamConfig;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraParam;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraResolution;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSetting;

/**
 * WiFi相机控制器，在WifiCameraCommand基础上封装了拍照、录制视频等功能
 * 应用中直接操作此类即可完成大部分操作
 *
 * @Author WangZiheng
 * @CreateDate 2021-02-02
 * @Organization Convergence Ltd.
 */
public class WifiCameraController extends BaseExCamController<WifiCameraView> implements WifiCameraCommand.OnCommandListener {

    private WifiCameraCommand wifiCameraCommand;
    private OnControlListener onControlListener;

    public WifiCameraController(Context context, WifiCameraView exCameraView) {
        super(context, exCameraView);
    }

    @NonNull
    @Override
    protected CameraLogger bindLogger() {
        return WifiCameraConstant.GetLogger();
    }

    @NonNull
    @Override
    protected TeleFocusHelper bindTeleFocusHelper() {
        return new WifiTeleFocusHelper(this);
    }

    @Override
    protected void init() {
        wifiCameraCommand = new WifiCameraCommand(context, exCameraView);
        wifiCameraCommand.setOnCommandListener(this);
    }

    /**
     * 开始获取图像数据流
     */
    public void startStream() {
        wifiCameraCommand.startStream();
    }

    /**
     * 停止获取图像数据流
     */
    public void stopStream() {
        wifiCameraCommand.stopStream();
    }

    /**
     * 重新获取图像数据流
     */
    public void retryStream() {
        wifiCameraCommand.retryStream();
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        wifiCameraCommand.startPreview();
        photoSaver.run();
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        wifiCameraCommand.stopPreview();
        photoSaver.release();
    }

    /**
     * 释放资源
     */
    public void release() {
        wifiCameraCommand.release();
    }

    /**
     * 设置控制监听
     */
    public void setOnControlListener(OnControlListener onControlListener) {
        this.onControlListener = onControlListener;
    }

    /**
     * 更新分辨率
     *
     * @param width    分辨率宽
     * @param height   分辨率高
     * @param listener 监听
     */
    public void updateResolution(int width, int height, WifiCameraCommand.OnResolutionUpdateListener listener) {
        if (!isPreviewing() || curActionState != ActionState.Normal) {
            if (listener != null) {
                listener.onFail();
            }
            return;
        }
        wifiCameraCommand.updateResolution(width, height, listener);
    }

    /**
     * 获取wifi相机配置信息（须处于正在视频推流状态）
     *
     * @param isReset 是否需要重置相关UI（一般仅开启推流时需重置）
     */
    public void loadConfig(boolean isReset) {
        wifiCameraCommand.loadConfig(isReset);
    }

    /**
     * 同步网络请求更新对焦
     *
     * @param focus 参数
     */
    public boolean updateFocusExecute(int focus) {
        if (!isPreviewing()) {
            return false;
        }
        return wifiCameraCommand.updateFocusExecute(focus);
    }

    /**
     * 获取当前WiFi相机状态
     */
    public WifiCameraState getCurWifiState() {
        return wifiCameraCommand.getCurState();
    }

    /**
     * 获取自动类调节参数信息
     */
    public WifiAutoConfig getAutoConfig(String tag) {
        return wifiCameraCommand.getAutoConfig(tag);
    }

    /**
     * 获取数值类调节参数信息
     */
    public WifiParamConfig getParamConfig(String tag) {
        return wifiCameraCommand.getParamConfig(tag);
    }

    /**
     * 重置数值参数
     */
    public void resetConfig(String tag) {
        wifiCameraCommand.resetConfig(tag);
    }

    /**
     * 获取自动参数是否自动
     */
    public boolean getAuto(String tag) {
        return wifiCameraCommand.getAuto(tag);
    }

    /**
     * 设置自动参数为自动或手动
     */
    public void setAuto(String tag, boolean value) {
        wifiCameraCommand.setAuto(tag, value);
    }

    /**
     * 获取数值参数当前值
     */
    public int getParam(String tag) {
        return wifiCameraCommand.getParam(tag);
    }

    /**
     * 设置数值参数当前值
     */
    public void setParam(String tag, int param) {
        wifiCameraCommand.setParam(tag, param);
    }

    /**
     * 获取数值参数当前值百分比
     */
    public int getParamPercent(String tag) {
        WifiParamConfig wifiParamConfig = getParamConfig(tag);
        return wifiParamConfig != null ? wifiParamConfig.getPercentByValue(getParam(tag)) : 0;
    }

    /**
     * 获取数值参数当前值一元二次百分比
     */
    public int getParamPercentQuadratic(String tag) {
        WifiParamConfig wifiParamConfig = getParamConfig(tag);
        return wifiParamConfig != null ? wifiParamConfig.getPercentByValueQuadratic(getParam(tag)) : 0;
    }

    /**
     * 按百分比设置数值参数当前值
     */
    public void setParamPercent(String tag, int percent) {
        WifiParamConfig wifiParamConfig = getParamConfig(tag);
        if (wifiParamConfig == null) {
            return;
        }
        setParam(tag, wifiParamConfig.getValueByPercent(percent));
    }

    /**
     * 按一元二次百分比设置数值参数当前值
     */
    public void setParamPercentQuadratic(String tag, int percent) {
        WifiParamConfig wifiParamConfig = getParamConfig(tag);
        if (wifiParamConfig == null) {
            return;
        }
        setParam(tag, wifiParamConfig.getValueByPercentQuadratic(percent));
    }

    /**
     * 网络请求snapshot实现拍照
     */
    private void networkPhotograph(String path) {
        wifiCameraCommand.loadOneFrame(new WifiCameraCommand.OnLoadOneFrameListener() {
            @Override
            public void onStart() {
                if (onCameraPhotographListener != null) {
                    onCameraPhotographListener.onTakePhotoStart();
                }
            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                photoSaver.addTask(path);
                photoSaver.provideFrame(bitmap);
            }

            @Override
            public void onError(String error) {
                if (onCameraPhotographListener != null) {
                    onCameraPhotographListener.onTakePhotoDone();
                    onCameraPhotographListener.onTakePhotoFail();
                }
            }
        });
    }

    @Override
    protected void onUpdateActionState(ActionState state) {
        if (onControlListener != null) {
            onControlListener.onActionStateUpdate(state);
        }
    }

    @Override
    protected void onUpdateResolution(int width, int height) {
        updateResolution(width, height, null);
    }

    @Override
    protected void onTakePhoto(String path) {
        switch (WifiCameraConstant.PHOTOGRAPH_TYPE) {
            case Stream:
                updateActionState(ActionState.Photographing);
                photoSaver.addTask(path);
                if (onCameraPhotographListener != null) {
                    onCameraPhotographListener.onTakePhotoStart();
                }
                break;
            case NetworkRequest:
                networkPhotograph(path);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSetupRecord(String path) {
        WifiCameraSetting wifiCameraSetting = WifiCameraSetting.getInstance();
        if (!wifiCameraSetting.isAvailable()) {
            if (onCameraRecordListener != null) {
                onCameraRecordListener.onRecordStartFail();
            }
            return;
        }
        WifiCameraResolution.Resolution resolution = wifiCameraSetting.getWifiCameraParam().getWifiCameraResolution().getCurResolution();
        Size videoSize = new Size(resolution.getWidth(), resolution.getHeight());
        exCameraRecorder.setup(path, videoSize);
    }

    @Override
    protected void onStopRecord() {
        exCameraRecorder.stop();
    }

    @Override
    protected void onSetupTLRecord(String path, int timeLapseRate) {
        WifiCameraSetting wifiCameraSetting = WifiCameraSetting.getInstance();
        if (!wifiCameraSetting.isAvailable()) {
            if (onCameraRecordListener != null) {
                onCameraRecordListener.onRecordStartFail();
            }
            return;
        }
        WifiCameraResolution.Resolution resolution = wifiCameraSetting.getWifiCameraParam().getWifiCameraResolution().getCurResolution();
        Size videoSize = new Size(resolution.getWidth(), resolution.getHeight());
        exCameraTLRecorder.setup(path, videoSize, timeLapseRate);
    }

    @Override
    protected void onStopTLRecord() {
        exCameraTLRecorder.stop();
    }

    @Override
    protected void onStartStackAvg(String path) {
        stackAvgOperator.start(path);
    }

    @Override
    protected void onCancelStackAvg() {
        stackAvgOperator.cancel();
    }

    @Override
    public boolean isPreviewing() {
        return wifiCameraCommand.isPreviewing();
    }

    @Override
    public void onObserveFPS(int instantFPS, float averageFPS) {
        if (WifiCameraConstant.IS_LOG_FPS) {
            cameraLogger.LogD("FPS : instant = " + instantFPS + " , average = " + averageFPS);
        }
        if (onControlListener != null) {
            onControlListener.onLoadFPS(instantFPS, averageFPS);
        }
    }

    @Override
    public Bitmap provideBitmap() {
        return wifiCameraCommand.getLatestBitmap();
    }

    @Override
    public void onStreamStart(boolean isRetry) {
        frameRateObserver.startObserve();
        if (onControlListener != null) {
            onControlListener.onStreamStart(isRetry);
        }
    }

    @Override
    public void onStreamStop(boolean isRetry) {
        frameRateObserver.stopObserve();
        if (curActionState == ActionState.Recording) {
            stopRecord();
        }
        if (curActionState == ActionState.TLRecording) {
            stopTLRecord();
        }
        if (onControlListener != null) {
            onControlListener.onStreamStop(isRetry);
        }
    }

    @Override
    public void onStateUpdate(WifiCameraState state) {
        if (onControlListener != null) {
            onControlListener.onWifiStateUpdate(state);
        }
    }

    @Override
    public void onLoadFrame(Bitmap bitmap) {
        frameRateObserver.mark();
        if (onControlListener != null) {
            onControlListener.onLoadFrame(bitmap);
        }
        if (WifiCameraConstant.PHOTOGRAPH_TYPE == WifiCameraConstant.PhotographType.Stream
                && curActionState == ActionState.Photographing) {
            photoSaver.provideFrame(bitmap);
            updateActionState(ActionState.Normal);
        }
    }

    @Override
    public void onParamUpdate(WifiCameraParam param, boolean isReset) {
        if (onControlListener != null) {
            onControlListener.onParamUpdate(param, isReset);
        }
    }

    public interface OnControlListener {

        /**
         * 获取图像数据流开始
         *
         * @param isRetry 是否重试
         */
        void onStreamStart(boolean isRetry);

        /**
         * 获取图像数据流停止
         *
         * @param isRetry 是否重试
         */
        void onStreamStop(boolean isRetry);

        /**
         * WiFi相机状态更新
         *
         * @param state 当前WiFi相机连接状态
         */
        void onWifiStateUpdate(WifiCameraState state);

        /**
         * 功能状态更新
         *
         * @param state 当前功能状态
         */
        void onActionStateUpdate(ActionState state);

        /**
         * 获取画面帧
         *
         * @param bitmap 画面帧Bitmap
         */
        void onLoadFrame(Bitmap bitmap);

        /**
         * 获取帧率
         *
         * @param instantFPS 即时帧率
         * @param averageFPS 平均帧率
         */
        void onLoadFPS(int instantFPS, float averageFPS);

        /**
         * WiFi相机参数更新
         *
         * @param param   当前参数配置
         * @param isReset 是否需要重置UI控件
         */
        void onParamUpdate(WifiCameraParam param, boolean isReset);
    }
}
