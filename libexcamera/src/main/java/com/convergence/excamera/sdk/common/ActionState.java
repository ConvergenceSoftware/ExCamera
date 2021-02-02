package com.convergence.excamera.sdk.common;

/**
 * 操作状态
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-25
 * @Organization Convergence Ltd.
 */
public enum ActionState {
    /*
    正常状态
     */
    Normal,
    /*
    正在获取拍照画面
     */
    Photographing,
    /*
    正在录像
     */
    Recording,
    /*
    正在延时摄影
     */
    TLRecording,
    /*
    正在进行叠加平均去噪
     */
    StackAvgRunning
}
