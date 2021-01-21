package com.convergence.excamera.sdk.common.callback;

/**
 * 望远相机自动调焦监听
 *
 * @Author WangZiheng
 * @CreateDate 2021-01-20
 * @Organization Convergence Ltd.
 */
public interface OnTeleAFListener {

    /**
     * 望远自动调焦开始
     *
     * @param isRunningReset 是否从正在自动调焦并重置
     */
    void onStartTeleAF(boolean isRunningReset);

    /**
     * 望远自动调焦结束
     */
    void onStopTeleAF();
}
