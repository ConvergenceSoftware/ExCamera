package com.convergence.excamera.sdk.common.callback;

/**
 * 延时摄影监听
 *
 * @Author WangZiheng
 * @CreateDate 2021-02-01
 * @Organization Convergence Ltd.
 */
public interface OnCameraTLRecordListener {

    /**
     * 延时摄影开始成功
     */
    void onTLRecordStartSuccess();

    /**
     * 延时摄影开始失败
     */
    void onTLRecordStartFail();

    /**
     * 延时摄影进度回调
     *
     * @param recordSeconds 当前录像时长（秒）
     */
    void onTLRecordProgress(int recordSeconds);

    /**
     * 延时摄影成功
     *
     * @param path 视频输出路径
     */
    void onTLRecordSuccess(String path);

    /**
     * 延时摄影失败
     */
    void onTLRecordFail();
}
