package com.convergence.excamera.sdk.wifi.core;

import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.common.CameraLogger;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * WiFi相机获取图像帧轮询
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraStreamLooper {

    private OnLoopListener onLoopListener;
    private Observable<Long> observable;
    private Disposable disposable;
    private boolean isLooping = false;
    private CameraLogger cameraLogger = WifiCameraConstant.GetLogger();

    public WifiCameraStreamLooper(OnLoopListener onLoopListener) {
        this.onLoopListener = onLoopListener;
        observable = Observable.interval(WifiCameraConstant.DEFAULT_WIFI_CAMERA_STREAM_PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * 开始轮询
     */
    public void startLoop() {
        if (isLooping()) {
            return;
        }
        isLooping = true;
        onLoopListener.onStartLoop();
        disposable = observable.subscribe(aLong -> {
            long startTime = System.currentTimeMillis();
            onLoopListener.onLooping();
            long costTime = System.currentTimeMillis() - startTime;
            if (WifiCameraConstant.IS_LOG_FRAME_DATA) {
                cameraLogger.LogD("load one frame cost time : " + costTime);
            }
        });
    }

    /**
     * 停止轮询
     */
    public void stopLoop() {
        if (isLooping) {
            isLooping = false;
        }
        if (disposable != null) {
            disposable.dispose();
            onLoopListener.onStopLoop();
            disposable = null;
        }
    }

    /**
     * 是否正在轮询
     */
    public boolean isLooping() {
        return isLooping && disposable != null && !disposable.isDisposed();
    }

    /**
     * 轮询监听
     */
    public interface OnLoopListener {

        /**
         * 轮询开始
         */
        void onStartLoop();

        /**
         * 轮询操作
         */
        void onLooping();

        /**
         * 轮询停止
         */
        void onStopLoop();
    }
}
