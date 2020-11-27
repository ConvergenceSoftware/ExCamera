package com.convergence.excamera.sdk.common.callback;

/**
 * 拍照监听
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-25
 * @Organization Convergence Ltd.
 */
public interface OnCameraPhotographListener {

    /**
     * 拍照开始
     */
    void onTakePhotoStart();

    /**
     * 拍照完成
     */
    void onTakePhotoDone();

    /**
     * 拍照成功
     *
     * @param path 图片输出路径
     */
    void onTakePhotoSuccess(String path);

    /**
     * 拍照失败
     */
    void onTakePhotoFail();
}
