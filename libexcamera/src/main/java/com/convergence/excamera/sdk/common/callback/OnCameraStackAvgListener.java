package com.convergence.excamera.sdk.common.callback;

import android.graphics.Bitmap;

/**
 * 叠加平均去噪监听
 *
 * @Author WangZiheng
 * @CreateDate 2021-01-27
 * @Organization Convergence Ltd.
 */
public interface OnCameraStackAvgListener {

    /**
     * 叠加平均开始
     */
    void onStackAvgStart();

    /**
     * 叠加平均取消
     */
    void onStackAvgCancel();

    /**
     * 叠加平均成功
     *
     * @param bitmap 最终结果Bitmap
     * @param path   最终结果保存路径
     */
    void onStackAvgSuccess(Bitmap bitmap, String path);

    /**
     * 叠加平均出错
     *
     * @param error 错误信息
     */
    void onStackAvgError(String error);
}
