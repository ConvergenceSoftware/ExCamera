package com.convergence.excamera.sdk.usb.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Size;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.common.ActionState;
import com.convergence.excamera.sdk.common.BaseExCamController;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.common.TeleFocusHelper;
import com.convergence.excamera.sdk.usb.UsbCameraConstant;
import com.convergence.excamera.sdk.usb.UsbCameraState;
import com.convergence.excamera.sdk.usb.entity.UsbCameraResolution;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSetting;
import com.serenegiant.usb.config.base.UVCAutoConfig;
import com.serenegiant.usb.config.base.UVCParamConfig;

/**
 * USB相机控制器，在UsbCameraCommand基础上封装了拍照、录制视频等功能
 * 应用中直接操作此类即可完成大部分操作
 *
 * @Author WangZiheng
 * @CreateDate 2021-02-02
 * @Organization Convergence Ltd.
 */
public class UsbCameraController extends BaseExCamController<UsbCameraView> implements Handler.Callback,
        UsbCameraCommand.OnConnectListener, UsbCameraCommand.OnCommandListener {

    private static final int MSG_PREVIEW_START = 100;
    private static final int MSG_PREVIEW_STOP = 101;

    private UsbCameraCommand usbCameraCommand;
    private Handler handler;
    private OnControlListener onControlListener;

    public UsbCameraController(Context context, UsbCameraView exCameraView) {
        super(context, exCameraView);
    }

    @NonNull
    @Override
    protected CameraLogger bindLogger() {
        return UsbCameraConstant.GetLogger();
    }

    @NonNull
    @Override
    protected TeleFocusHelper bindTeleFocusHelper() {
        return new UsbTeleFocusHelper(this);
    }

    @Override
    protected void init() {
        usbCameraCommand = new UsbCameraCommand(context, exCameraView);
        handler = new Handler(this);
        usbCameraCommand.setOnCommandListener(this);
        usbCameraCommand.setOnConnectListener(this);
    }

    /**
     * 注册USB广播监听（建议在生命周期OnStart中调用）
     */
    public void registerUsb() {
        usbCameraCommand.registerUsb();
    }

    /**
     * 注销USB广播监听（建议在生命周期OnStop中调用）
     */
    public void unregisterUsb() {
        usbCameraCommand.unregisterUsb();
    }

    /**
     * 开启预览（建议在生命周期OnResume中调用）
     */
    public void startPreview() {
        usbCameraCommand.startPreview();
        photoSaver.run();
    }

    /**
     * 关闭预览（建议在生命周期OnPause中调用）
     */
    public void stopPreview() {
        usbCameraCommand.stopPreview();
        photoSaver.release();
    }

    /**
     * 释放资源（建议在生命周期OnDestroy中调用）
     */
    public void release() {
        usbCameraCommand.closeCamera();
    }

    /**
     * 设置控制监听
     */
    public void setOnControlListener(OnControlListener onControlListener) {
        this.onControlListener = onControlListener;
    }

    /**
     * 更新镜像翻转参数
     */
    public void updateFlip() {
        usbCameraCommand.updateFlip();
    }

    /**
     * 获取当前USB相机状态
     */
    public UsbCameraState getCurUsbState() {
        return usbCameraCommand.getCurState();
    }

    /**
     * 获取自动类调节参数信息
     */
    public UVCAutoConfig getAutoConfig(String tag) {
        return usbCameraCommand.getAutoConfig(tag);
    }

    /**
     * 获取数值类调节参数信息
     */
    public UVCParamConfig getParamConfig(String tag) {
        return usbCameraCommand.getParamConfig(tag);
    }

    /**
     * 判断参数是否可进行调节
     */
    public boolean checkConfigEnable(String tag) {
        return usbCameraCommand.checkConfigEnable(tag);
    }

    /**
     * 获取自动参数是否自动
     */
    public boolean getAuto(String tag) {
        return usbCameraCommand.getAuto(tag);
    }

    /**
     * 设置自动参数为自动或手动
     */
    public void setAuto(String tag, boolean value) {
        usbCameraCommand.setAuto(tag, value);
    }

    /**
     * 重置自动参数
     */
    public void resetAuto(String tag) {
        usbCameraCommand.resetAuto(tag);
    }

    /**
     * 获取数值参数当前值
     */
    public int getParam(String tag) {
        return usbCameraCommand.getParam(tag);
    }

    /**
     * 设置数值参数当前值
     */
    public void setParam(String tag, int param) {
        usbCameraCommand.setParam(tag, param);
    }

    /**
     * 重置数值参数
     */
    public void resetParam(String tag) {
        usbCameraCommand.resetParam(tag);
    }

    /**
     * 获取数值参数当前值百分比
     */
    public int getParamPercent(String tag) {
        UVCParamConfig uvcParamConfig = getParamConfig(tag);
        return uvcParamConfig != null ? uvcParamConfig.getPercentByValue(getParam(tag)) : 0;
    }

    /**
     * 获取数值参数当前值一元二次百分比
     */
    public int getParamPercentQuadratic(String tag) {
        UVCParamConfig uvcParamConfig = getParamConfig(tag);
        return uvcParamConfig != null ? uvcParamConfig.getPercentByValueQuadratic(getParam(tag)) : 0;
    }

    /**
     * 按百分比设置数值参数当前值
     */
    public void setParamPercent(String tag, int percent) {
        UVCParamConfig uvcParamConfig = getParamConfig(tag);
        if (uvcParamConfig == null) {
            return;
        }
        setParam(tag, uvcParamConfig.getValueByPercent(percent));
    }

    /**
     * 按一元二次百分比设置数值参数当前值
     */
    public void setParamPercentQuadratic(String tag, int percent) {
        UVCParamConfig uvcParamConfig = getParamConfig(tag);
        if (uvcParamConfig == null) {
            return;
        }
        setParam(tag, uvcParamConfig.getValueByPercentQuadratic(percent));
    }

    @Override
    protected void onUpdateActionState(ActionState state) {
        if (onControlListener != null) {
            onControlListener.onActionStateUpdate(state);
        }
    }

    @Override
    protected void onUpdateResolution(int width, int height) {
        usbCameraCommand.updateResolution(width, height);
    }

    @Override
    protected void onTakePhoto(String path) {
        updateActionState(ActionState.Photographing);
        photoSaver.addTask(path);
        if (onCameraPhotographListener != null) {
            onCameraPhotographListener.onTakePhotoStart();
        }
    }

    @Override
    protected void onSetupRecord(String path) {
        UsbCameraSetting usbCameraSetting = UsbCameraSetting.getInstance();
        if (!usbCameraSetting.isAvailable()) {
            if (onCameraRecordListener != null) {
                onCameraRecordListener.onRecordStartFail();
            }
            return;
        }
        UsbCameraResolution.Resolution resolution = usbCameraSetting.getUsbCameraResolution().getCurResolution();
        Size videoSize = new Size(resolution.getWidth(), resolution.getHeight());
        exCameraRecorder.setup(path, videoSize);
    }

    @Override
    protected void onStopRecord() {
        exCameraRecorder.stop();
    }

    @Override
    protected void onSetupTLRecord(String path, int timeLapseRate) {
        UsbCameraSetting usbCameraSetting = UsbCameraSetting.getInstance();
        if (!usbCameraSetting.isAvailable()) {
            if (onCameraRecordListener != null) {
                onCameraRecordListener.onRecordStartFail();
            }
            return;
        }
        UsbCameraResolution.Resolution resolution = usbCameraSetting.getUsbCameraResolution().getCurResolution();
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
        return usbCameraCommand.isPreviewing();
    }

    @Override
    public void onObserveFPS(int instantFPS, float averageFPS) {
        if (UsbCameraConstant.IS_LOG_FPS) {
            cameraLogger.LogD("FPS : instant = " + instantFPS + " , average = " + averageFPS);
        }
        if (onControlListener != null) {
            onControlListener.onLoadFPS(instantFPS, averageFPS);
        }
    }

    @Override
    public Bitmap provideBitmap() {
        return usbCameraCommand.getLatestBitmap();
    }

    @Override
    public void onStateUpdate(UsbCameraState state) {
        if (onControlListener != null) {
            onControlListener.onUsbStateUpdate(state);
        }
    }

    @Override
    public void onLoadFrame(Bitmap bitmap) {
        frameRateObserver.mark();
        if (onControlListener != null) {
            onControlListener.onLoadFrame(bitmap);
        }
        if (curActionState == ActionState.Photographing) {
            photoSaver.provideFrame(bitmap);
            updateActionState(ActionState.Normal);
        }
    }

    @Override
    public void onUsbConnect() {
        if (onControlListener != null) {
            onControlListener.onUsbConnect();
        }
    }

    @Override
    public void onUsbDisConnect() {
        if (curActionState == ActionState.Recording && exCameraRecorder.isRecording()) {
            stopRecord();
        }
        if (curActionState == ActionState.TLRecording && exCameraTLRecorder.isRecording()) {
            stopTLRecord();
        }
        if (onControlListener != null) {
            onControlListener.onUsbDisConnect();
        }
    }

    @Override
    public void onCameraOpen() {
        if (onControlListener != null) {
            onControlListener.onCameraOpen();
        }
    }

    @Override
    public void onCameraClose() {
        if (onControlListener != null) {
            onControlListener.onCameraClose();
        }
    }

    @Override
    public void onPreviewStart() {
        cameraLogger.LogD(usbCameraCommand.getAllConfigDes());
        frameRateObserver.startObserve();
        handler.sendEmptyMessage(MSG_PREVIEW_START);

    }

    @Override
    public void onPreviewStop() {
        frameRateObserver.stopObserve();
        handler.sendEmptyMessage(MSG_PREVIEW_STOP);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_PREVIEW_START:
                if (onControlListener != null) {
                    onControlListener.onPreviewStart();
                }
                break;
            case MSG_PREVIEW_STOP:
                if (onControlListener != null) {
                    onControlListener.onPreviewStop();
                }
                break;
            default:
                break;
        }
        return false;
    }

    public interface OnControlListener {

        /**
         * USB连接成功
         */
        void onUsbConnect();

        /**
         * USB连接断开
         */
        void onUsbDisConnect();

        /**
         * UVC Camera开启
         */
        void onCameraOpen();

        /**
         * UVC Camera关闭
         */
        void onCameraClose();

        /**
         * 预览开始
         */
        void onPreviewStart();

        /**
         * 预览结束
         */
        void onPreviewStop();

        /**
         * USB连接状态更新
         *
         * @param state 当前USB连接状态
         */
        void onUsbStateUpdate(UsbCameraState state);

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
    }
}
