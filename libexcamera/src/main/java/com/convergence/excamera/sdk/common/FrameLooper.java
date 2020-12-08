package com.convergence.excamera.sdk.common;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 获取图像轮询（RxJava实现）
 *
 * @Author WangZiheng
 * @CreateDate 2020-12-07
 * @Organization Convergence Ltd.
 */
public abstract class FrameLooper {

    private OnLoopListener onLoopListener;
    private Observable<Long> observable;
    private Disposable disposable;
    private boolean isLooping = false;

    protected FrameLooper(OnLoopListener onLoopListener) {
        this.onLoopListener = onLoopListener;
        observable = bindObservable();
    }

    /**
     * 设置Observable
     *
     * @return Observable
     */
    protected abstract Observable<Long> bindObservable();

    /**
     * 开始轮询
     */
    public void startLoop() {
        if (isLooping()) {
            return;
        }
        isLooping = true;
        onLoopListener.onStartLoop();
        disposable = observable.subscribe(aLong -> onLoopListener.onLooping());
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
