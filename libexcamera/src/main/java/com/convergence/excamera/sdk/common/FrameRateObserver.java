package com.convergence.excamera.sdk.common;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 帧率记录器
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-19
 * @Organization Convergence Ltd.
 */
public class FrameRateObserver {

    private static final long FRAME_RATE_OBSERVE_PERIOD = 200;

    private OnFrameRateListener onFrameRateListener;
    private Observable<Long> observable;
    private Disposable disposable;
    private boolean isObserving = false;

    private Queue<Long> markTimeQueue;
    private Queue<Integer> instantFpsQueue;

    public FrameRateObserver(OnFrameRateListener onFrameRateListener) {
        this.onFrameRateListener = onFrameRateListener;
        markTimeQueue = new LinkedList<>();
        instantFpsQueue = new LinkedList<>();
        observable = Observable.interval(FRAME_RATE_OBSERVE_PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 开始计算帧率
     */
    public void startObserve() {
        if (isObserving()) {return;}
        isObserving = true;
        markTimeQueue.clear();
        instantFpsQueue.clear();
        onFrameRateListener.onObserveStart();
        disposable = observable.subscribe(aLong -> calculateFrameRate());
    }

    /**
     * 停止计算帧率
     */
    public void stopObserve() {
        if (isObserving) {
            isObserving = false;
        }
        if (disposable != null) {
            disposable.dispose();
            onFrameRateListener.onObserveStop();
            disposable = null;
        }
    }

    /**
     * 记录下当前时间，用于计算帧率
     */
    public void mark() {
        if (isObserving()) {
            markTimeQueue.offer(System.currentTimeMillis());
        }
    }

    /**
     * 计算当前帧率
     */
    private void calculateFrameRate() {
        int instantFps = 0;
        long curTime = System.currentTimeMillis();
        long threshold = curTime - 1000;
        while (!markTimeQueue.isEmpty()) {
            long time = markTimeQueue.peek();
            if (time <= threshold) {
                markTimeQueue.remove();
            } else {
                instantFps = markTimeQueue.size();
                break;
            }
        }
        instantFpsQueue.offer(instantFps);
        int size = (int) (1000L / FRAME_RATE_OBSERVE_PERIOD);
        if (instantFpsQueue.size() > size) {
            instantFpsQueue.remove();
        }
        float averageFps = 0.0f;
        for (int fps : instantFpsQueue) {
            averageFps += (float) fps / instantFpsQueue.size();
        }
        onFrameRateListener.onObserveFPS(instantFps, Float.parseFloat(String.format("%.1f", averageFps)));
    }

    /**
     * 是否正在记录帧率
     */
    public boolean isObserving() {
        return isObserving && disposable != null && !disposable.isDisposed();
    }

    public interface OnFrameRateListener {

        /**
         * 开始记录帧率
         */
        void onObserveStart();

        /**
         * 帧率回调
         *
         * @param instantFPS 即时帧率
         * @param averageFPS 平均帧率
         */
        void onObserveFPS(int instantFPS, float averageFPS);

        /**
         * 结束计算帧率
         */
        void onObserveStop();
    }
}
