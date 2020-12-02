package com.convergence.excamera.sdk.wifi.net.callback;

import androidx.annotation.Nullable;

import com.convergence.excamera.sdk.wifi.net.bean.NCommandResult;

/**
 * Retrofit网络请求参数操作回调
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class CommandNetCallback implements NetCallback {

    private OnResultListener onResultListener;

    public CommandNetCallback(@Nullable OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDone(Object result) {
        if (!(result instanceof NCommandResult)) {
            onResultListener.onResult(false);
            return;
        }
        NCommandResult configResult = (NCommandResult) result;
        if (onResultListener != null) {
            onResultListener.onResult(configResult.getResult() == 0);
        }
    }

    @Override
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    public interface OnResultListener {

        /**
         * 结果回调
         *
         * @param isSuccess 是否成功
         */
        void onResult(boolean isSuccess);
    }
}
