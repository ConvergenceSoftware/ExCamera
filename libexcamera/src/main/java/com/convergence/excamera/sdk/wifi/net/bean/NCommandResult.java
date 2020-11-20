package com.convergence.excamera.sdk.wifi.net.bean;

/**
 * 参数操作网络请求结果
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class NCommandResult {

    /**
     * result : 0
     */

    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    /**
     * 当前result = 0时，代表参数操作成功
     */
    public boolean isSuccess() {
        return result == 0;
    }
}
