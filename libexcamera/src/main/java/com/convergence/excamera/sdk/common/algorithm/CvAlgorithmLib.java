package com.convergence.excamera.sdk.common.algorithm;

import android.graphics.Bitmap;

/**
 * 图像处理算法相关JNI库
 *
 * @Author WangZiheng
 * @CreateDate 2021-01-26
 * @Organization Convergence Ltd.
 */
public class CvAlgorithmLib {

    static {
        System.loadLibrary("CvAlgorithm");
    }

    /**
     * 计算图片梯度
     */
    public static native long calGrad(Bitmap src);

    /**
     * 计算图片梯度
     */
    public static native long calCvGrad(long src);

    /**
     * 计算两张图片的PSNR峰值信噪比
     */
    public static native double calPSNR(Bitmap src1, Bitmap src2);

    /**
     * 计算两张图片的PSNR峰值信噪比
     */
    public static native double calCvPSNR(long src1, long src2);

    /**
     * 计算两张图片的SSIM结构相似性
     * 返回double数值，[0]B,[1]G,[2]R,[3]A
     */
    public static native double[] calSSIM(Bitmap src1, Bitmap src2);

    /**
     * 计算两张图片的SSIM结构相似性
     * 返回double数值，[0]B,[1]G,[2]R,[3]A
     */
    public static native double[] calCvSSIM(long src1, long src2);

    /**
     * 计算两张图片的感知哈希算法汉明距离
     */
    public static native int calPHash(Bitmap src1, Bitmap src2);

    /**
     * 计算两张图片的感知哈希算法汉明距离
     */
    public static native int calCvPHash(long src1, long src2);
}
