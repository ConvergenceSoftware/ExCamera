package com.convergence.excamera.sdk.planet.callback;

import android.graphics.Bitmap;

public interface OnPlanetWholeSceneListener {
    /**
     * 全景拼图开始
     */
    void onWholeSceneStart();
    /**
     * 全景拼图开始
     */
    void onWholeSceneProgressUpdate(int progress);

    /**
     * 全景拼图取消
     */
    void onWholeSceneCancel();

    /**
     * 全景拼图成功
     * @param bitmap   最终Bitmap
     */
    void onWholeSceneSuccess(Bitmap bitmap);

    /**
     * 全景拼图出错
     *
     * @param error 错误信息
     */
    void onWholeSceneError(String error);
    /**
     * 全景拼图出错
     *
     */
    void onWholeSceneLocationError(boolean isRotate);

    /**
     * 开始移动至点击碎片
     * @param rotateTime
     * @param pitchTime
     */
    void onWholeSceneMoveToTileStart(double startTileX, double startTileY, double endTileX, double endTileY, int rotateTime, int pitchTime);

    /**
     * 当前显示碎片坐标
     * @param x 当前显示碎片列
     * @param y 当前显示碎片行
     */
    void onWholeSceneShownLocation(double x, double y);

}
