package com.convergence.excamera.sdk.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.common.algorithm.StackAvgOperator;
import com.convergence.excamera.sdk.common.callback.ImgProvider;
import com.convergence.excamera.sdk.common.callback.OnCameraPhotographListener;
import com.convergence.excamera.sdk.common.callback.OnCameraRecordListener;
import com.convergence.excamera.sdk.common.callback.OnCameraStackAvgListener;
import com.convergence.excamera.sdk.common.callback.OnCameraTLRecordListener;
import com.convergence.excamera.sdk.common.callback.OnTeleAFListener;
import com.convergence.excamera.sdk.common.video.ExCameraRecorder;
import com.convergence.excamera.sdk.common.video.ExCameraTLRecorder;
import com.convergence.excamera.sdk.common.view.BaseExCameraView;

/**
 * 外置相机控制器基类
 *
 * @Author WangZiheng
 * @CreateDate 2021-02-02
 * @Organization Convergence Ltd.
 */
public abstract class BaseExCamController<V extends BaseExCameraView> implements ImgProvider,
        PhotoSaver.OnPhotoSaverListener, StackAvgOperator.OnStackAvgListener, TeleFocusHelper.TeleAFCallback,
        FrameRateObserver.OnFrameRateListener, ExCameraRecorder.OnRecordListener, ExCameraTLRecorder.OnTLRecordListener {

    /**
     * 上下文
     */
    protected Context context;
    /**
     * 外置相机预览控件
     */
    protected V exCameraView;
    /**
     * 日志打印工具类
     */
    protected CameraLogger cameraLogger;
    /**
     * 拍照图片保存封装工具类
     */
    protected PhotoSaver photoSaver;
    /**
     * 叠加平均去噪算法操作类
     */
    protected StackAvgOperator stackAvgOperator;
    /**
     * 望远相机调焦助手
     */
    protected TeleFocusHelper teleFocusHelper;
    /**
     * 录像工具类
     */
    protected ExCameraRecorder exCameraRecorder;
    /**
     * 延时摄影工具类
     */
    protected ExCameraTLRecorder exCameraTLRecorder;
    /**
     * 帧率记录器
     */
    protected FrameRateObserver frameRateObserver;
    /**
     * 当前操作状态
     */
    protected ActionState curActionState = ActionState.Normal;

    /**
     * 拍照监听
     */
    protected OnCameraPhotographListener onCameraPhotographListener;
    /**
     * 叠加平均监听
     */
    protected OnCameraStackAvgListener onCameraStackAvgListener;
    /**
     * 录像监听
     */
    protected OnCameraRecordListener onCameraRecordListener;
    /**
     * 延时摄影监听
     */
    protected OnCameraTLRecordListener onCameraTLRecordListener;
    /**
     * 望远相机自动对焦监听
     */
    protected OnTeleAFListener onTeleAFListener;

    protected BaseExCamController(Context context, V exCameraView) {
        this.context = context;
        this.exCameraView = exCameraView;
        cameraLogger = bindLogger();
        photoSaver = bindPhotoSaver();
        stackAvgOperator = bindStackAvgOperator();
        teleFocusHelper = bindTeleFocusHelper();
        exCameraRecorder = bindExCameraRecorder();
        exCameraTLRecorder = bindExCameraTLRecorder();
        frameRateObserver = bindFrameRateObserver();
        init();
    }

    /**
     * 设置拍照图片保存封装工具类
     *
     * @return 拍照图片保存封装工具类
     */
    @NonNull
    protected PhotoSaver bindPhotoSaver() {
        return new PhotoSaver(this);
    }

    /**
     * 设置叠加平均操作类
     *
     * @return 叠加平均操作类
     */
    @NonNull
    protected StackAvgOperator bindStackAvgOperator() {
        return new StackAvgOperator.Builder(context, this)
                .setOnStackAvgListener(this)
                .build();
    }

    /**
     * 设置录像工具类
     *
     * @return 录像工具类
     */
    @NonNull
    protected ExCameraRecorder bindExCameraRecorder() {
        return new ExCameraRecorder(context, this, this);
    }

    /**
     * 设置延时摄影工具类
     *
     * @return 延时摄影工具类
     */
    @NonNull
    protected ExCameraTLRecorder bindExCameraTLRecorder() {
        return new ExCameraTLRecorder(context, this, this);
    }

    /**
     * 设置帧率记录器
     *
     * @return 帧率记录器
     */
    @NonNull
    protected FrameRateObserver bindFrameRateObserver() {
        return new FrameRateObserver(this);
    }

    /**
     * 更新当前功能状态
     */
    protected void updateActionState(ActionState state) {
        if (curActionState == state) {
            return;
        }
        cameraLogger.LogD("Action State Update : " + curActionState + " ==> " + state);
        curActionState = state;
        onUpdateActionState(state);
    }

    /**
     * 更新分辨率
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    public void updateResolution(int width, int height) {
        onUpdateResolution(width, height);
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        takePhoto(OutputUtil.getRandomPicPath());
    }

    /**
     * 拍照
     *
     * @param path 自定义路径
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
                onTakePhoto(path);
                break;
            case Photographing:
            default:
                break;
            case Recording:
            case StackAvgRunning:
            case TLRecording:
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
        startRecord(OutputUtil.getRandomVideoPath());

    }

    /**
     * 开始录像
     *
     * @param path 自定义路径
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
                onSetupRecord(path);
                break;
            case Photographing:
            case StackAvgRunning:
            case TLRecording:
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
        onStopRecord();
    }

    /**
     * 开始延时摄影
     *
     * @param timeLapseRate 延时摄影倍率
     */
    public void startTLRecord(int timeLapseRate) {
        startTLRecord(OutputUtil.getRandomVideoPath(), timeLapseRate);
    }

    /**
     * 开始延时摄影
     *
     * @param path          自定义路径
     * @param timeLapseRate 延时摄影倍率
     */
    public void startTLRecord(String path, int timeLapseRate) {
        if (!isPreviewing()) {
            if (onCameraTLRecordListener != null) {
                onCameraTLRecordListener.onTLRecordStartFail();
            }
            return;
        }
        switch (curActionState) {
            case Normal:
                onSetupTLRecord(path, timeLapseRate);
                break;
            case Photographing:
            case Recording:
            case StackAvgRunning:
                if (onCameraTLRecordListener != null) {
                    onCameraTLRecordListener.onTLRecordStartFail();
                }
                break;
            case TLRecording:
            default:
                break;
        }
    }

    /**
     * 停止延时摄影
     */
    public void stopTLRecord() {
        onStopTLRecord();
    }

    /**
     * 开始叠加平均去噪拍照
     */
    public void startStackAvg() {
        startStackAvg(OutputUtil.getRandomPicPath());
    }

    /**
     * 开始叠加平均去噪拍照
     *
     * @param path 自定义路径
     */
    public void startStackAvg(String path) {
        if (!isPreviewing()) {
            if (onCameraStackAvgListener != null) {
                onCameraStackAvgListener.onStackAvgError("It is not previewing now");
            }
            return;
        }
        switch (curActionState) {
            case Normal:
                onStartStackAvg(path);
                break;
            case Photographing:
            case Recording:
            case TLRecording:
                if (onCameraStackAvgListener != null) {
                    onCameraStackAvgListener.onStackAvgError("Other task is working");
                }
                break;
            case StackAvgRunning:
            default:
                break;
        }
    }

    /**
     * 取消叠加平均去噪拍照
     */
    public void cancelStackAvg() {
        onCancelStackAvg();
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
     * 是否正在望远自动对焦
     */
    public boolean isTeleAFRunning() {
        return teleFocusHelper.isAFRunning();
    }

    /**
     * 获取当前操作状态
     */
    public ActionState getCurActionState() {
        return curActionState;
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
     * 设置延时摄影监听
     */
    public void setOnCameraTLRecordListener(OnCameraTLRecordListener onCameraTLRecordListener) {
        this.onCameraTLRecordListener = onCameraTLRecordListener;
    }

    /**
     * 设置叠加平均去噪监听
     */
    public void setOnCameraStackAvgListener(OnCameraStackAvgListener onCameraStackAvgListener) {
        this.onCameraStackAvgListener = onCameraStackAvgListener;
    }

    /**
     * 设置望远自动调焦监听
     */
    public void setOnTeleAFListener(OnTeleAFListener onTeleAFListener) {
        this.onTeleAFListener = onTeleAFListener;
    }

    /**
     * 设置日志打印工具类
     *
     * @return 日志打印工具类
     */
    @NonNull
    protected abstract CameraLogger bindLogger();

    /**
     * 设置望远相机调焦工具类
     *
     * @return 望远相机调焦工具类
     */
    @NonNull
    protected abstract TeleFocusHelper bindTeleFocusHelper();

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 进行功能状态更新操作
     *
     * @param state 新的功能状态
     */
    protected abstract void onUpdateActionState(ActionState state);

    /**
     * 进行更新分辨率操作
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    protected abstract void onUpdateResolution(int width, int height);

    /**
     * 进行拍照操作
     *
     * @param path 图片保存路径
     */
    protected abstract void onTakePhoto(String path);

    /**
     * 进行配置录像操作
     *
     * @param path 视频保存路径
     */
    protected abstract void onSetupRecord(String path);

    /**
     * 进行停止录像操作
     */
    protected abstract void onStopRecord();

    /**
     * 进行配置延时摄影操作
     *
     * @param path          视频保存路径
     * @param timeLapseRate 延时摄影倍率
     */
    protected abstract void onSetupTLRecord(String path, int timeLapseRate);

    /**
     * 进行停止延时摄影操作
     */
    protected abstract void onStopTLRecord();

    /**
     * 进行开始叠加平均操作
     *
     * @param path 图片保存路径
     */
    protected abstract void onStartStackAvg(String path);

    /**
     * 进行取消叠加平均操作
     */
    protected abstract void onCancelStackAvg();

    /**
     * 判断是否正在预览
     *
     * @return 是否正在预览
     */
    public abstract boolean isPreviewing();

    @Override
    public void onSavePhotoSuccess(String path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{path}, null, null);
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
        exCameraRecorder.start();
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
    public void onSetupTLRecordSuccess() {
        exCameraTLRecorder.start();
    }

    @Override
    public void onSetupTLRecordError() {
        if (onCameraTLRecordListener != null) {
            onCameraTLRecordListener.onTLRecordStartFail();
        }
    }

    @Override
    public void onStartTLRecordSuccess() {
        updateActionState(ActionState.TLRecording);
        if (onCameraTLRecordListener != null) {
            onCameraTLRecordListener.onTLRecordStartSuccess();
        }
    }

    @Override
    public void onStartTLRecordError() {
        if (onCameraTLRecordListener != null) {
            onCameraTLRecordListener.onTLRecordStartFail();
        }
    }

    @Override
    public void onTLRecordProgress(int recordTime) {
        if (onCameraTLRecordListener != null) {
            onCameraTLRecordListener.onTLRecordProgress(recordTime);
        }
    }

    @Override
    public void onTLRecordSuccess(String videoPath) {
        updateActionState(ActionState.Normal);
        if (onCameraTLRecordListener != null) {
            onCameraTLRecordListener.onTLRecordSuccess(videoPath);
        }
    }

    @Override
    public void onTLRecordError() {
        updateActionState(ActionState.Normal);
        if (onCameraTLRecordListener != null) {
            onCameraTLRecordListener.onTLRecordFail();
        }
    }

    @Override
    public void onStackAvgStart() {
        updateActionState(ActionState.StackAvgRunning);
        if (onCameraStackAvgListener != null) {
            onCameraStackAvgListener.onStackAvgStart();
        }
    }

    @Override
    public void onStackAvgCancel() {
        updateActionState(ActionState.Normal);
        if (onCameraStackAvgListener != null) {
            onCameraStackAvgListener.onStackAvgCancel();
        }
    }

    @Override
    public void onStackAvgSuccess(Bitmap bitmap, String path) {
        updateActionState(ActionState.Normal);
        if (onCameraStackAvgListener != null) {
            onCameraStackAvgListener.onStackAvgSuccess(bitmap, path);
        }
    }

    @Override
    public void onStackAvgError(String error) {
        updateActionState(ActionState.Normal);
        if (onCameraStackAvgListener != null) {
            onCameraStackAvgListener.onStackAvgError(error);
        }
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
    public void onAFError(String error) {
        cameraLogger.LogD(error);
    }

    @Override
    public void onObserveStart() {
        cameraLogger.LogD("Start Observe FPS");
    }

    @Override
    public void onObserveStop() {
        cameraLogger.LogD("Stop Observe FPS");
    }
}
