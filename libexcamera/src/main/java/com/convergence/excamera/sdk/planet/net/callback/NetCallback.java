package com.convergence.excamera.sdk.planet.net.callback;

/**
 * Retrofit网络请求回调接口
 *
 * @Author LiLei
 * @CreateDate 2021-05-24
 * @Organization Convergence Ltd.
 */
public interface NetCallback {

    /**
     * 网络通信开始
     */
    void onStart();

    /**
     * 网络通信完成
     *
     * @param result 请求结果对象
     */
    void onDone(Object result);

    /**
     * 网络通信出错
     *
     * @param error 异常
     */
    void onError(Throwable error);
}
