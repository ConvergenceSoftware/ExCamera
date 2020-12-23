package com.convergence.excamera.sdk.wifi;

/**
 * WiFi相机状态
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-25
 * @Organization Convergence Ltd.
 */
public enum WifiCameraState {

    /*
    空闲状态，初始状态，未进行网络请求获取图像数据流
    Free, it is the original state and frame stream have been loaded from network
     */
    Free,

    /*
    重试状态，网络请求获取图像数据流失败或连续多帧图片获取失败触发，不断重新请求推流
    Retrying, it will be triggered when fail to load frame stream or acquire consecutive frames
     */
    Retrying,

    /*
    准备状态，过渡状态，网络请求获取图像数据流成功，开启轮询读取画面帧
    Prepared, it is the transition state, load frame stream success and start looping to load bitmap
    frame from stream
     */
    Prepared,

    /*
    预览状态，正常预览并且未因连续多帧图片获取失败触发重试状态
    Previewing，preview is started and frame is available, Retrying state have not been triggered
    because of failing to acquire consecutive frames
     */
    Previewing
}
