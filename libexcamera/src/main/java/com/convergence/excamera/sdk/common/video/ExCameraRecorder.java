package com.convergence.excamera.sdk.common.video;

import android.content.Context;
import android.util.Size;

import com.convergence.excamera.sdk.common.callback.ImgProvider;


/**
 * 通用的ExCamera录像工具基类
 *
 * @Author WangZiheng
 * @CreateDate 2020-12-07
 * @Organization Convergence Ltd.
 */
public abstract class ExCameraRecorder implements VideoCreator.OnCreateVideoListener {

    protected Context context;
    protected OnRecordListener listener;
    protected ImgProvider imgProvider;
    protected VideoCreator videoCreator;

    protected ExCameraRecorder(Context context, ImgProvider imgProvider, OnRecordListener listener) {
        this.context = context;
        this.imgProvider = imgProvider;
        this.listener = listener;
        videoCreator = bindVideoCreator();
    }

    /**
     * 设置VideoCreator
     *
     * @return VideoCreator
     */
    protected abstract VideoCreator bindVideoCreator();

    /**
     * 初始化配置
     *
     * @param videoPath 视频保存路径
     * @param videoSize 视频保存分辨率
     */
    public void setup(String videoPath, Size videoSize) {
        videoCreator.setup(videoPath, videoSize);
    }

    /**
     * 开始录像
     */
    public void start() {
        videoCreator.start();
    }

    /**
     * 停止录像
     */
    public void stop() {
        videoCreator.stop();
    }

    /**
     * 是否录像中
     *
     * @return 是否录像中
     */
    public boolean isRecording() {
        return videoCreator.isRunning();
    }

    @Override
    public void onStateChange(VideoCreator.State state) {
    }

    @Override
    public void onSetupSuccess() {
        listener.onSetupRecordSuccess();
    }

    @Override
    public void onSetupError(String error) {
        listener.onSetupRecordError();
    }

    @Override
    public void onStartSuccess() {
        listener.onStartRecordSuccess();
    }

    @Override
    public void onStartError(String error) {
        listener.onStartRecordError();
    }

    @Override
    public void onRunning(int runSeconds) {
        listener.onRecordProgress(runSeconds);
    }

    @Override
    public void onCreateVideoSuccess(String path) {
        listener.onRecordSuccess(path);
    }

    @Override
    public void onCreateVideoError(String error) {
        listener.onRecordError();
    }

    public interface OnRecordListener {

        /**
         * 初始化配置录像成功
         */
        void onSetupRecordSuccess();

        /**
         * 初始化配置录像出错
         */
        void onSetupRecordError();

        /**
         * 开始录像成功
         */
        void onStartRecordSuccess();

        /**
         * 开始录像失败
         */
        void onStartRecordError();

        /**
         * 录像读秒回调
         *
         * @param recordTime 当前录像秒数
         */
        void onRecordProgress(int recordTime);

        /**
         * 录像成功
         *
         * @param videoPath 保存路径
         */
        void onRecordSuccess(String videoPath);

        /**
         * 录像出错
         */
        void onRecordError();
    }
}
