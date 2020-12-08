package com.convergence.excamera.sdk.wifi.core;

import com.convergence.excamera.sdk.common.FrameLooper;
import com.convergence.excamera.sdk.wifi.WifiCameraConstant;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * WiFi相机获取图像帧轮询
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraFrameLooper extends FrameLooper {

    protected WifiCameraFrameLooper(OnLoopListener onLoopListener) {
        super(onLoopListener);
    }

    @Override
    protected Observable<Long> bindObservable() {
        return Observable.interval(WifiCameraConstant.DEFAULT_WIFI_CAMERA_STREAM_PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
}
