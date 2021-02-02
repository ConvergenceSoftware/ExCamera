package com.convergence.excamera.sdk.common.video;

import android.content.Context;
import android.util.Size;

import com.convergence.excamera.sdk.common.callback.ImgProvider;

/**
 * 通用的ExCamera延时摄影工具类
 *
 * @Author WangZiheng
 * @CreateDate 2021-02-01
 * @Organization Convergence Ltd.
 */
public class ExCameraTLRecorder implements VideoCreator.OnCreateVideoListener {

    protected Context context;
    protected OnTLRecordListener listener;
    protected ImgProvider imgProvider;
    protected VideoCreator videoCreator;

    public ExCameraTLRecorder(Context context, ImgProvider imgProvider, OnTLRecordListener listener) {
        this.context = context;
        this.imgProvider = imgProvider;
        this.listener = listener;
        videoCreator = new VideoCreator.Builder(context, imgProvider, this)
                .setFrame(VideoCreator.FRAME_TIME_LAPSE_RECORD)
                .build();
    }

    /**
     * 初始化配置
     *
     * @param videoPath 视频保存路径
     * @param videoSize 视频保存分辨率
     */
    public void setup(String videoPath, Size videoSize, int timeLapseRate) {
        videoCreator.setup(videoPath, videoSize, timeLapseRate);
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
        listener.onSetupTLRecordSuccess();
    }

    @Override
    public void onSetupError(String error) {
        listener.onSetupTLRecordError();
    }

    @Override
    public void onStartSuccess() {
        listener.onStartTLRecordSuccess();
    }

    @Override
    public void onStartError(String error) {
        listener.onStartTLRecordError();
    }

    @Override
    public void onRunning(int runSeconds) {
        listener.onTLRecordProgress(runSeconds);
    }

    @Override
    public void onCreateVideoSuccess(String path) {
        listener.onTLRecordSuccess(path);
    }

    @Override
    public void onCreateVideoError(String error) {
        listener.onTLRecordError();
    }

    public interface OnTLRecordListener {

        /**
         * 初始化配置延时摄影成功
         */
        void onSetupTLRecordSuccess();

        /**
         * 初始化配置延时摄影出错
         */
        void onSetupTLRecordError();

        /**
         * 开始延时摄影成功
         */
        void onStartTLRecordSuccess();

        /**
         * 开始延时摄影失败
         */
        void onStartTLRecordError();

        /**
         * 延时摄影读秒回调
         *
         * @param recordTime 当前录像秒数
         */
        void onTLRecordProgress(int recordTime);

        /**
         * 延时摄影成功
         *
         * @param videoPath 保存路径
         */
        void onTLRecordSuccess(String videoPath);

        /**
         * 延时摄影出错
         */
        void onTLRecordError();
    }
}
