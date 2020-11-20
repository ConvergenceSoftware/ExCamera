package com.convergence.excamera.sdk.usb.core;

import android.content.Context;
import android.util.Size;

import com.convergence.excamera.sdk.UsbCameraConstant;
import com.convergence.excamera.sdk.common.VideoCreator;

/**
 * USB相机录制视频功能封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraRecorder implements VideoCreator.OnCreateVideoListener {

    private Context context;
    private OnRecordListener listener;
    private VideoCreator videoCreator;

    public UsbCameraRecorder(Context context, VideoCreator.DataProvider dataProvider, OnRecordListener listener) {
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

        void onSetupRecordSuccess();

        void onSetupRecordError();

        void onStartRecordSuccess();

        void onStartRecordError();

        void onRecordProgress(int recordTime);

        void onRecordSuccess(String videoPath);

        void onRecordError();
    }
}
