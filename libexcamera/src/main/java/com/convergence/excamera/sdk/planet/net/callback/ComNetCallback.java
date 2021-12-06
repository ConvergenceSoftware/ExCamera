package com.convergence.excamera.sdk.planet.net.callback;

import androidx.annotation.Nullable;

/**
 * 通用Retrofit网络请求回调，监听网络请求开始、完成、成功、失败各个状态
 *
 * @Author LiLei
 * @CreateDate 2021-05-24
 * @Organization Convergence Ltd.
 */
public class ComNetCallback<T> implements NetCallback {

    private OnResultListener<T> onResultListener;

    public ComNetCallback(@Nullable OnResultListener<T> onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Override
    public void onStart() {
        if (onResultListener != null) {
            onResultListener.onStart();
        }
    }

    @Override
    public void onDone(Object result) {
        if (onResultListener != null) {
            onResultListener.onDone();
        }
        if (result == null) {
            if (onResultListener != null) {
                onResultListener.onError("Null Data");
            }
            return;
        }
        try {
            T data = (T) result;
            if (onResultListener != null) {
                onResultListener.onSuccess(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onResultListener != null) {
                onResultListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void onError(Throwable error) {
        error.printStackTrace();
        if (onResultListener != null) {
            onResultListener.onDone();
            onResultListener.onError(error.getMessage());
        }
    }

    /**
     * 网络请求结果回调
     *
     * @param <T> 返回结果类型
     */
    public interface OnResultListener<T> {

        /**
         * 网络请求开始
         */
        void onStart();

        /**
         * 网络请求完成
         */
        void onDone();

        /**
         * 网络请求成功
         *
         * @param result 请求结果
         */
        void onSuccess(T result);

        /**
         * 网络请求失败
         *
         * @param error 出错信息
         */
        void onError(String error);
    }
}
