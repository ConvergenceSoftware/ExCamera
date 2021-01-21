package com.convergence.excamera.sdk.wifi.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Size;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.common.ActionState;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.common.FrameRateObserver;
import com.convergence.excamera.sdk.common.MediaScanner;
import com.convergence.excamera.sdk.common.OutputUtil;
import com.convergence.excamera.sdk.common.PhotoSaver;
import com.convergence.excamera.sdk.common.callback.OnCameraPhotographListener;
import com.convergence.excamera.sdk.common.callback.OnCameraRecordListener;
import com.convergence.excamera.sdk.common.callback.OnTeleAFListener;
import com.convergence.excamera.sdk.common.video.ExCameraRecorder;
import com.convergence.excamera.sdk.common.video.VideoCreator;
import com.convergence.excamera.sdk.tele.TeleFocusHelper;
import com.convergence.excamera.sdk.tele.WifiTeleFocusHelper;
import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.wifi.WifiCameraState;
import com.convergence.excamera.sdk.wifi.config.base.WifiAutoConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiParamConfig;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraParam;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraResolution;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSP;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSetting;

/**
 * USB相机控制器，在UsbCameraCommand基础上封装了拍照、录制视频等功能
 * 应用中直接操作此类即可完成大部分操作
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraController implements Handler.Callback, WifiCameraCommand.OnCommandListener,
        ExCameraRecorder.OnRecordListener, PhotoSaver.OnPhotoSaverListener,
        VideoCreator.DataProvider, TeleFocusHelper.TeleFocusCallback, FrameRateObserver.OnFrameRateListener {

    private CameraLogger cameraLogger = WifiCameraConstant.GetLogger();

    private Context context;
    private WifiCameraView wifiCameraView;
    private WifiCameraCommand wifiCameraCommand;
    private WifiCameraRecorder wifiCameraRecorder;
    private PhotoSaver photoSaver;
    private TeleFocusHelper teleFocusHelper;
    private Handler handler;
    private MediaScanner mediaScanner;
    private FrameRateObserver frameRateObserver;
    private ActionState curActionState = ActionState.Normal;

    private OnControlListener onControlListener;
    private OnCameraPhotographListener onCameraPhotographListener;
    private OnCameraRecordListener onCameraRecordListener;
    private OnTeleAFListener onTeleAFListener;

    public WifiCameraController(Context context, WifiCameraView wifiCameraView) {
        this.context = context;
        this.wifiCameraView = wifiCameraView;
        wifiCameraCommand = new WifiCameraCommand(context, wifiCameraView);
        wifiCameraRecorder = new WifiCameraRecorder(context, this, this);
        photoSaver = new PhotoSaver(this);
        teleFocusHelper = new WifiTeleFocusHelper(this);
        handler = new Handler(this);
        mediaScanner = new MediaScanner(context);
        frameRateObserver = new FrameRateObserver(this);
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
     * 设置拍照监听
     */
    public void setOnCameraPhotographListener(OnCameraPhotographListener onCameraPhotographListener) {
        this.onCameraPhotographListener = onCameraPhotographListener;
    }

    /**
     * 设置录像监听
     */
    public void setOnCameraRecordListener(OnCameraRecordListener onCameraRecordListener) {
        this.onCameraRecordListener = onCameraRecordListener;
    }

    /**
     * 设置望远自动调焦监听
     */
    public void setOnTeleAFListener(OnTeleAFListener onTeleAFListener) {
        this.onTeleAFListener = onTeleAFListener;
    }

    /**
     * 更新分辨率
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    public void updateResolution(int width, int height) {
        updateResolution(width, height, null);
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
     * 获取wifi相机配置信息（须处于正在视频推流状态）
     *
     * @param isReset 是否需要重置相关UI（一般仅开启推流时需重置）
     */
    public void loadConfig(boolean isReset) {
        wifiCameraCommand.loadConfig(isReset);
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        String path = OutputUtil.getRandomPicPath(WifiCameraSP.getEditor(context).getCameraOutputRootPath());
        takePhoto(path);
    }

    /**
     * 拍照
     */
    public void takePhoto(String path) {
        if (!isPreviewing()) {
            if (onCameraPhotographListener != null) {
                onCameraPhotographListener.onTakePhotoFail();
            }
            return;
        }
        switch (curActionState) {
            case Normal:
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
                break;
            case Photographing:
            default:
                break;
            case Recording:
                if (onCameraPhotographListener != null) {
                    onCameraPhotographListener.onTakePhotoFail();
                }
                break;
        }
    }

    /**
     * 开始录像
     */
    public void startRecord() {
        String path = OutputUtil.getRandomVideoPath(WifiCameraSP.getEditor(context).getCameraOutputRootPath());
        startRecord(path);
    }

    /**
     * 开始录像（自定义路径）
     */
    public void startRecord(String path) {
        if (!isPreviewing()) {
            if (onCameraRecordListener != null) {
                onCameraRecordListener.onRecordStartFail();
            }
            return;
        }
        switch (curActionState) {
            case Normal:
                WifiCameraSetting wifiCameraSetting = WifiCameraSetting.getInstance();
                if (!wifiCameraSetting.isAvailable()) {
                    if (onCameraRecordListener != null) {
                        onCameraRecordListener.onRecordStartFail();
                    }
                    break;
                }
                WifiCameraResolution.Resolution resolution = wifiCameraSetting.getWifiCameraParam().getWifiCameraResolution().getCurResolution();
                Size videoSize = new Size(resolution.getWidth(), resolution.getHeight());
                wifiCameraRecorder.setup(path, videoSize);
                break;
            case Photographing:
                if (onCameraRecordListener != null) {
                    onCameraRecordListener.onRecordStartFail();
                }
                break;
            case Recording:
            default:
                break;
        }
    }

    /**
     * 停止录像
     */
    public void stopRecord() {
        wifiCameraRecorder.stop();
    }

    /**
     * 开始望远自动对焦
     */
    public void startTeleAF() {
        teleFocusHelper.startAutoFocus();
    }

    /**
     * 停止望远自动对焦
     */
    public void stopTeleAF() {
        teleFocusHelper.stopAutoFocus();
    }

    /**
     * 是否正在预览
     */
    public boolean isPreviewing() {
        return wifiCameraCommand.isPreviewing();
    }

    /**
     * 是否正在望远自动对焦
     */
    public boolean isTeleAFRunning() {
        return teleFocusHelper.isAFRunning();
    }

    /**
     * 获取当前WiFi相机状态
     */
    public WifiCameraState getCurUsbState() {
        return wifiCameraCommand.getCurState();
    }

    /**
     * 获取当前操作状态
     */
    public ActionState getCurActionState() {
        return curActionState;
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
     * 开始望远相机调焦
     *
     * @param isBack 是否向后调焦
     */
    public void startTeleFocus(boolean isBack) {
        teleFocusHelper.startPress(isBack);
    }

    /**
     * 结束望远相机调焦
     *
     * @param isBack 是否向后调焦
     */
    public void stopTeleFocus(boolean isBack) {
        teleFocusHelper.stopPress(isBack);
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

    /**
     * 更新当前功能状态
     */
    private void updateActionState(ActionState state) {
        if (curActionState == state) {
            return;
        }
        cameraLogger.LogD("Action State Update : " + curActionState + " ==> " + state);
        curActionState = state;
        if (onControlListener != null) {
            onControlListener.onActionStateUpdate(state);
        }
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

    @Override
    public void onSavePhotoSuccess(String path) {
        mediaScanner.scanFile(path, null);
        if (onCameraPhotographListener != null) {
            onCameraPhotographListener.onTakePhotoDone();
            onCameraPhotographListener.onTakePhotoSuccess(path);
        }
    }

    @Override
    public void onSavePhotoFail() {
        if (onCameraPhotographListener != null) {
            onCameraPhotographListener.onTakePhotoDone();
            onCameraPhotographListener.onTakePhotoFail();
        }
    }

    @Override
    public void onSetupRecordSuccess() {
        wifiCameraRecorder.start();
    }

    @Override
    public void onSetupRecordError() {
        if (onCameraRecordListener != null) {
            onCameraRecordListener.onRecordStartFail();
        }
    }

    @Override
    public void onStartRecordSuccess() {
        updateActionState(ActionState.Recording);
        if (onCameraRecordListener != null) {
            onCameraRecordListener.onRecordStartSuccess();
        }
    }

    @Override
    public void onStartRecordError() {
        if (onCameraRecordListener != null) {
            onCameraRecordListener.onRecordStartFail();
        }
    }

    @Override
    public void onRecordProgress(int recordTime) {
        if (onCameraRecordListener != null) {
            onCameraRecordListener.onRecordProgress(recordTime);
        }
    }

    @Override
    public void onRecordSuccess(String videoPath) {
        updateActionState(ActionState.Normal);
        if (onCameraRecordListener != null) {
            onCameraRecordListener.onRecordSuccess(videoPath);
        }
    }

    @Override
    public void onRecordError() {
        updateActionState(ActionState.Normal);
        if (onCameraRecordListener != null) {
            onCameraRecordListener.onRecordFail();
        }
    }

    @Override
    public Bitmap provideBitmap() {
        return wifiCameraCommand.getLatestBitmap();
    }

    @Override
    public Bitmap provideAFBitmap() {
        return wifiCameraCommand.getLatestBitmap();
    }

    @Override
    public void onAFStart(boolean isRunningReset) {
        cameraLogger.LogD("Start Tele AF");
        if (onTeleAFListener != null) {
            onTeleAFListener.onStartTeleAF(isRunningReset);
        }
    }

    @Override
    public void onAFStop() {
        cameraLogger.LogD("Stop Tele AF");
        if (onTeleAFListener != null) {
            onTeleAFListener.onStopTeleAF();
        }
    }

    @Override
    public void onObserveStart() {

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
    public void onObserveStop() {

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
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
