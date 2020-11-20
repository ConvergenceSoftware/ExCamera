package com.convergence.excamera.sdk.usb.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Size;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.UsbCameraConstant;
import com.convergence.excamera.sdk.common.BitmapUtil;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.common.FrameRateObserver;
import com.convergence.excamera.sdk.common.MediaScanner;
import com.convergence.excamera.sdk.common.OutputUtil;
import com.convergence.excamera.sdk.common.VideoCreator;
import com.convergence.excamera.sdk.usb.entity.UsbCameraResolution;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSP;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSetting;
import com.serenegiant.usb.config.base.UVCAutoConfig;
import com.serenegiant.usb.config.base.UVCParamConfig;

/**
 * USB相机控制器，在UsbCameraCommand基础上封装了拍照、录制视频等功能
 * 应用中直接操作此类即可完成大部分操作
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraController implements Handler.Callback, UsbCameraCommand.OnConnectListener,
        UsbCameraCommand.OnCommandListener, UsbCameraRecorder.OnRecordListener,
        VideoCreator.DataProvider, FrameRateObserver.OnFrameRateListener {

    private static final int MSG_PREVIEW_START = 100;
    private static final int MSG_PREVIEW_STOP = 101;
    private static final int MSG_TAKE_PHOTO_SUCCESS = 102;
    private static final int MSG_TAKE_PHOTO_FAIL = 103;

    public enum ActionState {
        Normal, Photographing, Recording
    }

    private CameraLogger cameraLogger = UsbCameraConstant.GetLogger();

    private Context context;
    private UsbCameraView usbCameraView;
    private UsbCameraCommand usbCameraCommand;
    private UsbCameraRecorder usbCameraRecorder;
    private Handler handler;
    private MediaScanner mediaScanner;
    private FrameRateObserver frameRateObserver;
    private ActionState curActionState = ActionState.Normal;

    private OnControlListener onControlListener;
    private OnTakePhotoListener onTakePhotoListener;
    private OnRecordListener onRecordListener;

    public UsbCameraController(Context context, UsbCameraView usbCameraView) {
        this.context = context;
        this.usbCameraView = usbCameraView;
        usbCameraCommand = new UsbCameraCommand(context, usbCameraView);
        usbCameraRecorder = new UsbCameraRecorder(context, this, this);
        handler = new Handler(this);
        mediaScanner = new MediaScanner(context);
        frameRateObserver = new FrameRateObserver(this);
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
    }

    /**
     * 关闭预览（建议在生命周期OnPause中调用）
     */
    public void stopPreview() {
        usbCameraCommand.stopPreview();
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
     * 设置拍照监听
     */
    public void setOnTakePhotoListener(OnTakePhotoListener onTakePhotoListener) {
        this.onTakePhotoListener = onTakePhotoListener;
    }

    /**
     * 设置录像监听
     */
    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    /**
     * 更新分辨率
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    public void updateResolution(int width, int height) {
        usbCameraCommand.updateResolution(width, height);
    }

    /**
     * 更新镜像翻转参数
     */
    public void updateFlip() {
        usbCameraCommand.updateFlip();
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        if (!isPreviewing()) {
            if (onTakePhotoListener != null) {
                onTakePhotoListener.onTakePhotoFail();
            }
            return;
        }
        switch (curActionState) {
            case Normal:
                updateActionState(ActionState.Photographing);
                if (onTakePhotoListener != null) {
                    onTakePhotoListener.onTakePhotoStart();
                }
                break;
            case Photographing:
                break;
            case Recording:
                if (onTakePhotoListener != null) {
                    onTakePhotoListener.onTakePhotoFail();
                }
                break;
        }
    }

    /**
     * 开始录像
     */
    public void startRecord() {
        if (!isPreviewing()) {
            if (onRecordListener != null) {
                onRecordListener.onRecordStartFail();
            }
            return;
        }
        switch (curActionState) {
            case Normal:
                UsbCameraSetting usbCameraSetting = UsbCameraSetting.getInstance();
                if (!usbCameraSetting.isAvailable()) {
                    if (onRecordListener != null) {
                        onRecordListener.onRecordStartFail();
                    }
                    break;
                }
                String path = OutputUtil.getRandomVideoPath(UsbCameraSP.getEditor(context).getCameraOutputRootPath());
                UsbCameraResolution.Resolution resolution = usbCameraSetting.getUsbCameraResolution().getCurResolution();
                Size videoSize = new Size(resolution.getWidth(), resolution.getHeight());
                usbCameraRecorder.setup(path, videoSize);
                break;
            case Photographing:
                if (onRecordListener != null) {
                    onRecordListener.onRecordStartFail();
                }
                break;
            case Recording:
                break;
        }
    }

    /**
     * 停止录像
     */
    public void stopRecord() {
        usbCameraRecorder.stop();
    }

    /**
     * 是否正在预览
     */
    public boolean isPreviewing() {
        return usbCameraCommand.isPreviewing();
    }

    /**
     * 获取当前USB相机状态
     */
    public UsbCameraCommand.State getCurUsbState() {
        return usbCameraCommand.getCurState();
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
        if (uvcParamConfig == null) return;
        setParam(tag, uvcParamConfig.getValueByPercent(percent));
    }

    /**
     * 按一元二次百分比设置数值参数当前值
     */
    public void setParamPercentQuadratic(String tag, int percent) {
        UVCParamConfig uvcParamConfig = getParamConfig(tag);
        if (uvcParamConfig == null) return;
        setParam(tag, uvcParamConfig.getValueByPercentQuadratic(percent));
    }

    /**
     * 更新当前功能状态
     */
    private void updateActionState(ActionState state) {
        if (curActionState == state) return;
        cameraLogger.LogD("Action State Update : " + curActionState + " ==> " + state);
        curActionState = state;
        if (onControlListener != null) {
            onControlListener.onActionStateUpdate(state);
        }
    }

    @Override
    public void onStateUpdate(UsbCameraCommand.State state) {
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
            new Thread(() -> {
                UsbCameraSetting usbCameraSetting = UsbCameraSetting.getInstance();
                if (!usbCameraSetting.isAvailable()) {
                    handler.sendEmptyMessage(MSG_TAKE_PHOTO_FAIL);
                }
                String path = OutputUtil.getRandomPicPath(UsbCameraSP.getEditor(context).getCameraOutputRootPath());
                boolean result = BitmapUtil.saveBitmap(bitmap, path);
                if (result) {
                    Message message = new Message();
                    message.what = MSG_TAKE_PHOTO_SUCCESS;
                    message.obj = path;
                    handler.sendMessage(message);
                    mediaScanner.scanFile(path, null);
                } else {
                    handler.sendEmptyMessage(MSG_TAKE_PHOTO_FAIL);
                }
            }).start();
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
        if (curActionState == ActionState.Recording && usbCameraRecorder.isRecording()) {
            stopRecord();
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
    public void onSetupRecordSuccess() {
        usbCameraRecorder.start();
    }

    @Override
    public void onSetupRecordError() {
        if (onRecordListener != null) {
            onRecordListener.onRecordStartFail();
        }
    }

    @Override
    public void onStartRecordSuccess() {
        updateActionState(ActionState.Recording);
        if (onRecordListener != null) {
            onRecordListener.onRecordStartSuccess();
        }
    }

    @Override
    public void onStartRecordError() {
        if (onRecordListener != null) {
            onRecordListener.onRecordStartFail();
        }
    }

    @Override
    public void onRecordProgress(int recordTime) {
        if (onRecordListener != null) {
            onRecordListener.onRecordProgress(recordTime);
        }
    }

    @Override
    public void onRecordSuccess(String videoPath) {
        updateActionState(ActionState.Normal);
        if (onRecordListener != null) {
            onRecordListener.onRecordSuccess(videoPath);
        }
    }

    @Override
    public void onRecordError() {
        updateActionState(ActionState.Normal);
        if (onRecordListener != null) {
            onRecordListener.onRecordFail();
        }
    }

    @Override
    public Bitmap provideBitmap() {
        return usbCameraCommand.getLatestBitmap();
    }

    @Override
    public void onObserveStart() {

    }

    @Override
    public void onFPSObserve(int instantFPS, float averageFPS) {
        if (UsbCameraConstant.IS_LOG_FPS) {
            cameraLogger.LogD("FPS : instant = " + instantFPS + " , average = " + averageFPS);
        }
        onControlListener.onLoadFPS(instantFPS, averageFPS);
    }

    @Override
    public void onObserveStop() {

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
            case MSG_TAKE_PHOTO_SUCCESS:
                if (onTakePhotoListener != null) {
                    String photoPath = (String) msg.obj;
                    onTakePhotoListener.onTakePhotoDone();
                    onTakePhotoListener.onTakePhotoSuccess(photoPath);
                }
                break;
            case MSG_TAKE_PHOTO_FAIL:
                if (onTakePhotoListener != null) {
                    onTakePhotoListener.onTakePhotoDone();
                    onTakePhotoListener.onTakePhotoFail();
                }
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
        void onUsbStateUpdate(UsbCameraCommand.State state);

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

    public interface OnTakePhotoListener {

        /**
         * 拍照开始
         */
        void onTakePhotoStart();

        /**
         * 拍照完成
         */
        void onTakePhotoDone();

        /**
         * 拍照成功
         *
         * @param path 图片输出路径
         */
        void onTakePhotoSuccess(String path);

        /**
         * 拍照失败
         */
        void onTakePhotoFail();
    }

    public interface OnRecordListener {

        /**
         * 录像开始成功
         */
        void onRecordStartSuccess();

        /**
         * 录像开始识别
         */
        void onRecordStartFail();

        /**
         * 录像进度回调
         *
         * @param recordSeconds 当前录像时长（秒）
         */
        void onRecordProgress(int recordSeconds);

        /**
         * 录像成功
         *
         * @param path 视频输出路径
         */
        void onRecordSuccess(String path);

        /**
         * 录像失败
         */
        void onRecordFail();
    }
}
