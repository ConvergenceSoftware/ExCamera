package com.convergence.excamera.sdk.usb.core;

import android.content.Context;

import com.convergence.excamera.sdk.common.video.ExCameraRecorder;
import com.convergence.excamera.sdk.common.video.VideoCreator;

/**
 * USB相机录制视频功能封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraRecorder extends ExCameraRecorder {

    protected UsbCameraRecorder(Context context, VideoCreator.DataProvider dataProvider, OnRecordListener listener) {
        super(context, dataProvider, listener);
    }

    @Override
    protected VideoCreator bindVideoCreator() {
        return new VideoCreator.Builder(context, dataProvider, this)
                .setFrame(VideoCreator.FRAME_STANDARD_RECORD)
                .build();
    }
}
