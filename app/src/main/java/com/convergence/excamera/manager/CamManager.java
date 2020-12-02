package com.convergence.excamera.manager;

/**
 * 相机功能接口
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public interface CamManager {

    /**
     * 在Activity生命周期nStart中调用
     */
    void onStart();

    /**
     * 在Activity生命周期nStart中调用
     */
    void onResume();

    /**
     * 在Activity生命周期nStart中调用
     */
    void onPause();

    /**
     * 在Activity生命周期nStart中调用
     */
    void onStop();

    /**
     * 在Activity生命周期nStart中调用
     */
    void onDestroy();

    /**
     * 拍照
     */
    void takePhoto();

    /**
     * 开始录像
     */
    void startRecord();

    /**
     * 停止录像
     */
    void stopRecord();

    /**
     * 显示分辨率选择弹窗
     */
    void showResolutionSelection();

    /**
     * 更新分辨率
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    void updateResolution(int width, int height);

    /**
     * 是否预览中
     *
     * @return 是否预览中
     */
    boolean isPreviewing();

    /**
     * 是否录像中
     *
     * @return 是否录像中
     */
    boolean isRecording();
}
