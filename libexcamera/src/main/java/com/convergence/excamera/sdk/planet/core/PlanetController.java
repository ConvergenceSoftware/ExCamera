package com.convergence.excamera.sdk.planet.core;

import android.content.Context;
import android.util.Log;

import com.convergence.excamera.sdk.planet.net.bean.NControlBean;
import com.convergence.excamera.sdk.planet.net.bean.NControlResult;

/**
 * Planet运动控制器，在PlanetCommand基础上封装了复位、运动、停止运动等功能
 * 应用中直接操作此类即可完成大部分操作
 *
 * @Author LiLei
 * @CreateDate 2021-06-02
 * @Organization Convergence Ltd.
 */
public class PlanetController {
    private static final String TAG = "PlanetController";
    private PlanetCommand planetCommand;


    private boolean isStopPitch;
    private boolean isStopRotate;

    public PlanetController(Context context) {
        planetCommand = new PlanetCommand();

    }

    /**
     * 云台复位
     */
    public void resetPitch(PlanetCommand.OnControlListener listener){
        planetCommand.resetPitch(listener);
    }

    public void resetRotate(PlanetCommand.OnControlListener listener) {
        planetCommand.resetRotate(listener);
    }

    public void startPitch(NControlBean controlBean) {
        Log.i(TAG, "俯仰  speed: "+ controlBean.getSpeed());
        planetCommand.startPitch(controlBean);
    }

    public void startPitch(NControlBean controlBean, PlanetCommand.OnControlListener listener) {
        Log.i(TAG, "俯仰  speed: "+ controlBean.getSpeed());
        planetCommand.startPitch(controlBean, listener);
    }

    /**
     * 开始云台俯仰
     *
     * @param isClockWise 是否顺时针旋转
     */
    public void startPitch(boolean isClockWise, int speed, int subDivision) {
        Log.i(TAG, "startPitch: ");
        planetCommand.startPitch(isClockWise, speed, subDivision);
    }

    /**
     * 结束云台俯仰
     */
    public void stopPitch() {
        isStopPitch = true;
        Log.i(TAG, "stopPitch: ");

        planetCommand.stopPitch(new PlanetCommand.OnControlListener() {
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
            public void onFail() {

            }
        });
    }
    public void stopPitch(PlanetCommand.OnControlListener listener) {
        isStopPitch = true;
        planetCommand.stopPitch(listener);
    }

    public void startRotate(NControlBean controlBean) {
        planetCommand.startRotate(controlBean);
    }
    public void startRotate(NControlBean controlBean, PlanetCommand.OnControlListener listener) {
        planetCommand.startRotate(controlBean, listener);
    }

    /**
     * 开始云台旋转
     *
     * @param isClockWise 是否顺时针旋转
     */
    public void startRotate(boolean isClockWise, int speed, int subDivision) {
        planetCommand.startRotate(isClockWise, speed, subDivision);
    }

    /**
     * 结束云台旋转
     */
    public void stopRotate() {
        isStopRotate = true;
        planetCommand.stopRotate(new PlanetCommand.OnControlListener() {
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
            public void onFail() {

            }
        });
    }
    /**
     * 结束云台旋转
     */
    public void stopRotate(PlanetCommand.OnControlListener listener) {
        isStopRotate = true;
        Log.i(TAG, "stopRotate: ");
        planetCommand.stopRotate(listener);
    }
}
