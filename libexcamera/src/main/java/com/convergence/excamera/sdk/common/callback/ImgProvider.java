package com.convergence.excamera.sdk.common.callback;

import android.graphics.Bitmap;

/**
 * 通过此接口统一获取图像帧数据
 *
 * @Author WangZiheng
 * @CreateDate 2021-01-26
 * @Organization Convergence Ltd.
 */
public interface ImgProvider {

    /**
     * 图像帧数据Bitmap
     *
     * @return 图像Bitmap
     */
    Bitmap provideBitmap();
}
