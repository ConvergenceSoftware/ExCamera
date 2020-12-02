package com.convergence.excamera.sdk.wifi.core;

import android.content.Context;
import android.util.Size;

import com.convergence.excamera.sdk.common.video.VideoCreator;

/**
 * WiFi相机录制视频功能封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraRecorder implements VideoCreator.OnCreateVideoListener {

    private Context context;
    private OnRecordListener listener;
    private VideoCreator videoCreator;

    public WifiCameraRecorder(Context context, VideoCreator.DataProvider dataProvider, OnRecordListener listener) {
        this.context = context;
        this.listener = listener;
        videoCreator = new VideoCreator.Builder(context, dataProvider, this)
                .setFrame(VideoCreator.FRAME_STANDARD_RECORD)
                .build();
    }

    public void setup(String videoPath, Size videoSize) {
        videoCreator.setup(videoPath, videoSize);
    }

    public void start() {
        videoCreator.start();
    }

    public void stop() {
        videoCreator.stop();
    }

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
