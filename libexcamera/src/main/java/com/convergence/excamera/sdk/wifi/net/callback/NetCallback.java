package com.convergence.excamera.sdk.wifi.net.callback;

/**
 * Retrofit网络请求回调接口
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
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
