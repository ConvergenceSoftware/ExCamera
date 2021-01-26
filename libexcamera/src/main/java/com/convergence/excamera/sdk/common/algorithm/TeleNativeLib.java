package com.convergence.excamera.sdk.common.algorithm;

import android.graphics.Bitmap;

/**
 * 望远相机功能JNI库
 *
 * @Author WangZiheng
 * @CreateDate 2021-01-13
 * @Organization Convergence Ltd.
 */
public class TeleNativeLib {

    /**
     * 停止标识
     */
    public static final int AF_FLAG_FREE = -1;
    /**
     * 向后调焦标识
     */
    public static final int AF_FLAG_BACK = 0;
    /**
     * 向前调焦标识
     */
    public static final int AF_FLAG_FRONT = 1;

    static {
        System.loadLibrary("TeleFun");
    }

    /**
     * 开始自动对焦
     *
     * @param isBack 是否向向后自动调焦
     * @return 自动对焦标识
     */
    public static native int startAF(boolean isBack);

    /**
     * 结束自动对焦
     * @return 自动对焦标识
     */
    public static native int stopAF();

    /**
     * 处理自动对焦图片
     * @param src 图片Bitmap
     * @return 自动对焦标识
     */
    public static native int processBitmapAF(Bitmap src);

    /**
     * 处理自动对焦图片
     * @param src Mat内存地址
     * @return 自动对焦标识
     */
    public static native int processMatAF(long src);

    /**
     * 判断是否正在自动对焦
     * @return 是否正在自动对焦
     */
    public static native boolean isAFRunning();
}
