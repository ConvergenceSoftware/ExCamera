package com.convergence.excamera.sdk.common.callback;

/**
 * 录像监听
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-25
 * @Organization Convergence Ltd.
 */
public interface OnCameraRecordListener {

    /**
     * 录像开始成功
     */
    void onRecordStartSuccess();

    /**
     * 录像开始识别
     */
    void onRecordStartFail();

    /**
     * 录像进度回调
     *
     * @param recordSeconds 当前录像时长（秒）
     */
    void onRecordProgress(int recordSeconds);

    /**
     * 录像成功
     *
     * @param path 视频输出路径
     */
    void onRecordSuccess(String path);

    /**
     * 录像失败
     */
    void onRecordFail();
}
