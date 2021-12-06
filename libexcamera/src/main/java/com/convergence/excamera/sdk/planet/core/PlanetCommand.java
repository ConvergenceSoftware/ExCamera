package com.convergence.excamera.sdk.planet.core;

import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.planet.PlanetConstant;
import com.convergence.excamera.sdk.planet.net.PlanetNetWork;
import com.convergence.excamera.sdk.planet.net.bean.NControlBean;
import com.convergence.excamera.sdk.planet.net.bean.NControlResult;
import com.convergence.excamera.sdk.planet.net.callback.ComNetCallback;

/**
 * 云台控制，封装了对WiFi相机操作的大部分命令
 *
 * @Author LiLei
 * @CreateDate 2021-05-21
 * @Organization Convergence Ltd.
 */
public class PlanetCommand {

    private final PlanetNetWork netWork = PlanetNetWork.getInstance();
    private CameraLogger cameraLogger = PlanetConstant.GetLogger();

    private boolean isReleased;

    public PlanetCommand() {
        isReleased = false;
    }

    /**
     * 重置云台控制连接ip
     */
    public void updateNetBaseUrl(String baseUrl) {
        netWork.updateBaseUrl(baseUrl);
    }
    /**
     * 云台俯仰复位
     */
    public void resetPitch(OnControlListener listener) {
        NControlBean controlBean = new NControlBean(1, 3, 0, 0, 400, 2,0,0);
        netWork.reset(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onDone() {
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onSuccess(NControlResult result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) {
                    listener.onFail();
                }
            }
        }));
    }
    /**
     * 云台旋转复位
     */
    public void resetRotate(OnControlListener listener) {
        NControlBean controlBean = new NControlBean(0, 3, 0, 0, 400, 2,0,0);
        netWork.reset(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onDone() {
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onSuccess(NControlResult result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) {
                    listener.onFail();
                }
            }
        }));
    }

    public void startPitch(NControlBean controlBean) {
        netWork.control(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NControlResult result) {

            }

            @Override
            public void onError(String error) {

            }
        }));
    }

    /**
     * 同步网络俯仰
     */
    public void startPitchExecute(NControlBean controlBean, OnControlListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        NControlResult result = netWork.controlExecute(controlBean);
        if (result!=null) {
            if (listener != null) {
                listener.onDone();
            }
        }
    }

    public void startPitch(NControlBean controlBean, OnControlListener listener) {
        netWork.control(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onDone() {
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onSuccess(NControlResult result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) {
                    listener.onFail();
                }
            }
        }));
    }

    /**
     * 云台开始俯仰
     */
    public void startPitch(boolean isClockWise, int speed, int subDivision) {
        NControlBean controlBean = new NControlBean(1, isClockWise?1:2, 5, 0, speed, subDivision,0,0);

        netWork.control(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NControlResult result) {

            }

            @Override
            public void onError(String error) {

            }
        }));
    }

    public void stopPitch(OnControlListener listener) {
        NControlBean controlBean = new NControlBean(1, 0, 5, 0, 400, 2,0,0);
        netWork.controlStop(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onDone() {
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onSuccess(NControlResult result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(String error) {
                retryStopPitch();
                if (listener != null) {
                    listener.onFail();
                }
            }
        }));
    }

    public void retryStopPitch() {
        NControlBean controlBean = new NControlBean(1, 0, 5, 0, 400, 2,0,0);
        netWork.controlStop(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NControlResult result) {

            }

            @Override
            public void onError(String error) {

            }
        }));
    }

    public void startRotate(NControlBean controlBean) {
        netWork.control(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NControlResult result) {

            }

            @Override
            public void onError(String error) {

            }
        }));
    }

    public void startRotate(NControlBean controlBean, OnControlListener listener) {
        netWork.control(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onDone() {
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onSuccess(NControlResult result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) {
                    listener.onFail();
                }
            }
        }));
    }

    public void startRotate(boolean isClockWise, int speed, int subDivision) {
        NControlBean controlBean = new NControlBean(0, isClockWise?1:2, 5, 0, speed, subDivision,1,120);
        netWork.control(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NControlResult result) {

            }

            @Override
            public void onError(String error) {

            }
        }));
    }

    public void stopRotate(OnControlListener listener) {
        NControlBean controlBean = new NControlBean(0, 0, 5, 0, 400, 2,0,0);
        netWork.controlStop(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onDone() {
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onSuccess(NControlResult result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(String error) {
                reTryStopRotate();
                if (listener != null) {
                    listener.onFail();
                }
            }
        }));
    }
    public void reTryStopRotate() {
        NControlBean controlBean = new NControlBean(0, 0, 5, 0, 400, 2,0,0);
        netWork.controlStop(controlBean, new ComNetCallback<NControlResult>(new ComNetCallback.OnResultListener<NControlResult>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NControlResult result) {

            }

            @Override
            public void onError(String error) {

            }
        }));
    }

    /**
     * 云台电机控制监听
     */
    public interface OnControlListener {

        /**
         * 开始
         */
        void onStart();

        /**
         * 完成
         */
        void onDone();

        /**
         * 成功
         */
        void onSuccess(NControlResult result);

        /**
         * 失败
         */
        void onFail();
    }

}
