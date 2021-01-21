package com.convergence.excamera.sdk.wifi.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.common.FrameLooper;
import com.convergence.excamera.sdk.wifi.WifiCameraConstant;
import com.convergence.excamera.sdk.wifi.WifiCameraState;
import com.convergence.excamera.sdk.wifi.config.base.WifiAutoConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiConfig;
import com.convergence.excamera.sdk.wifi.config.base.WifiParamConfig;
import com.convergence.excamera.sdk.wifi.entity.ImageStream;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraParam;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraResolution;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSP;
import com.convergence.excamera.sdk.wifi.entity.WifiCameraSetting;
import com.convergence.excamera.sdk.wifi.net.WifiCameraNetWork;
import com.convergence.excamera.sdk.wifi.net.bean.NCommandResult;
import com.convergence.excamera.sdk.wifi.net.bean.NConfigList;
import com.convergence.excamera.sdk.wifi.net.callback.ComNetCallback;
import com.convergence.excamera.sdk.wifi.net.callback.CommandNetCallback;

import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * WiFi相机控制，封装了对WiFi相机操作的大部分命令
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public class WifiCameraCommand implements FrameLooper.OnLoopListener, Handler.Callback {

    private static final int MSG_START_STREAM = 100;
    private static final int MSG_STOP_STREAM = 101;
    private static final int MSG_RETRY_STREAM = 102;
    private static final int MSG_LOAD_FRAME = 103;

    private final WifiCameraNetWork netWork = WifiCameraNetWork.getInstance();
    private CameraLogger cameraLogger = WifiCameraConstant.GetLogger();

    private Context context;
    private WifiCameraView wifiCameraView;
    private Handler handler;
    private WifiCameraSetting cameraSetting;
    private WifiCameraFrameLooper streamLooper;
    private WifiCameraState curState = WifiCameraState.Free;

    private Bitmap latestBitmap;
    private int retryTimes = 0;
    private boolean isPrepared = false;
    private boolean isWaitingRetry = false;
    private boolean isReleased;
    private OnCommandListener onCommandListener;

    public WifiCameraCommand(Context context, WifiCameraView wifiCameraView) {
        this.context = context;
        this.wifiCameraView = wifiCameraView;
        handler = new Handler(this);
        cameraSetting = WifiCameraSetting.getInstance();
        streamLooper = new WifiCameraFrameLooper(this);
        isReleased = false;
    }

    /**
     * 重置Wifi连接ip
     */
    public void updateNetBaseUrl(String baseUrl) {
        netWork.updateBaseUrl(baseUrl);
    }

    /**
     * 开始获取图像数据流
     */
    public void startStream() {
        netWork.loadStream(new ComNetCallback<ResponseBody>(new ComNetCallback.OnResultListener<ResponseBody>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {
                isWaitingRetry = false;
            }

            @Override
            public void onSuccess(ResponseBody result) {
                InputStream inputStream = result.byteStream();
                ImageStream.initInstance(inputStream);
                isPrepared = true;
                Message msg = new Message();
                msg.what = MSG_START_STREAM;
                msg.obj = curState == WifiCameraState.Retrying;
                handler.sendMessage(msg);
                updateState(WifiCameraState.Prepared);
                cameraLogger.LogD("start stream success");
                loadConfig(true);
            }

            @Override
            public void onError(String error) {
                cameraLogger.LogD("start stream error : " + error);
                retryStream();
            }
        }));
    }

    /**
     * 停止获取图像数据流
     */
    public void stopStream() {
        boolean isStopSteam = ImageStream.getInstance() != null;
        ImageStream.closeInstance();
        isPrepared = false;
        if (isStopSteam) {
            Message msg = new Message();
            msg.what = MSG_STOP_STREAM;
            msg.obj = false;
            handler.sendMessage(msg);
        }
        updateState(WifiCameraState.Free);
    }

    /**
     * 重新获取图像数据流
     */
    public void retryStream() {
        if (isWaitingRetry || isReleased) {
            return;
        }
        isWaitingRetry = true;
        boolean isStopSteam = ImageStream.getInstance() != null;
        ImageStream.closeInstance();
        isPrepared = false;
        if (isStopSteam) {
            Message msg = new Message();
            msg.what = MSG_STOP_STREAM;
            msg.obj = true;
            handler.sendMessage(msg);
        }
        updateState(WifiCameraState.Retrying);
        cameraLogger.LogD("retry stream");
        handler.sendEmptyMessageDelayed(MSG_RETRY_STREAM, WifiCameraConstant.DEFAULT_WIFI_CAMERA_RETRY_PERIOD);
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        streamLooper.startLoop();
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        streamLooper.stopLoop();
    }

    /**
     * 释放资源
     */
    public void release() {
        handler.removeMessages(MSG_RETRY_STREAM);
        isReleased = true;
    }

    /**
     * 获取wifi相机配置信息（须处于正在视频推流状态）
     *
     * @param isReset 是否需要重置相关UI（一般仅开启推流时需重置）
     */
    public void loadConfig(boolean isReset) {
        if (!isPrepared()) {
            return;
        }
        netWork.loadConfig(new ComNetCallback<NConfigList>(new ComNetCallback.OnResultListener<NConfigList>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(NConfigList result) {
                cameraSetting.updateParam(result);
                WifiCameraParam wifiCameraParam = cameraSetting.getWifiCameraParam();
                if (wifiCameraParam.isAvailable()) {
                    WifiCameraResolution.Resolution resolution = wifiCameraParam.getWifiCameraResolution().getCurResolution();
                    wifiCameraView.resize(resolution.getWidth(), resolution.getHeight());
                }
                if (onCommandListener != null) {
                    cameraLogger.LogD("load config success : isReset = " + isReset);
                    onCommandListener.onParamUpdate(wifiCameraParam, isReset);
                }
            }

            @Override
            public void onError(String error) {

            }
        }), isReset);
    }

    /**
     * 获取单帧图片
     */
    public void loadOneFrame(OnLoadOneFrameListener listener) {
        if (!isPrepared()) {
            return;
        }
        netWork.loadFrame(new ComNetCallback<ResponseBody>(new ComNetCallback.OnResultListener<ResponseBody>() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onDone() {

            }

            @Override
            public void onSuccess(ResponseBody result) {
                try {
                    byte[] bytes = result.bytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    listener.onDone();
                    listener.onSuccess(flipBitmap(bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                listener.onDone();
                listener.onError(error);
            }
        }));
    }

    /**
     * 获取自动类配置信息
     */
    public WifiAutoConfig getAutoConfig(String tag) {
        if (!isParamAvailable() || !WifiAutoConfig.isTagSupport(tag)) {
            return null;
        }
        WifiConfig wifiConfig = cameraSetting.getWifiCameraParam().getConfig(tag);
        if (wifiConfig instanceof WifiAutoConfig) {
            return (WifiAutoConfig) wifiConfig;
        } else {
            return null;
        }
    }

    /**
     * 获取参数类配置信息
     */
    public WifiParamConfig getParamConfig(String tag) {
        if (!isParamAvailable() || !WifiParamConfig.isTagSupport(tag)) {
            return null;
        }
        WifiConfig wifiConfig = cameraSetting.getWifiCameraParam().getConfig(tag);
        if (wifiConfig instanceof WifiParamConfig) {
            return (WifiParamConfig) wifiConfig;
        } else {
            return null;
        }
    }

    /**
     * 获取是否自动
     */
    public boolean getAuto(String tag) {
        if (!isParamAvailable()) {
            return false;
        }
        WifiAutoConfig wifiAutoConfig = getAutoConfig(tag);
        return wifiAutoConfig != null && wifiAutoConfig.isAuto();
    }

    /**
     * 获取参数值
     */
    public int getParam(String tag) {
        if (!isParamAvailable()) {
            return 0;
        }
        WifiParamConfig wifiParamConfig = getParamConfig(tag);
        return wifiParamConfig != null ? wifiParamConfig.getCur() : 0;
    }

    /**
     * 设置自动
     */
    public void setAuto(String tag, boolean isAuto) {
        setAuto(tag, isAuto, null);
    }

    /**
     * 设置自动
     */
    public void setAuto(String tag, boolean isAuto, OnConfigCommandListener listener) {
        if (!isParamAvailable()) {
            return;
        }
        WifiAutoConfig wifiAutoConfig = getAutoConfig(tag);
        if (wifiAutoConfig == null) {
            return;
        }
        wifiAutoConfig.setAuto(isAuto);
        requestComCommand(wifiAutoConfig, listener);
    }

    /**
     * 设置参数
     */
    public void setParam(String tag, int value) {
        setParam(tag, value, null);
    }

    /**
     * 设置参数
     */
    public void setParam(String tag, int value, OnConfigCommandListener listener) {
        if (!isParamAvailable()) {
            return;
        }
        WifiParamConfig wifiParamConfig = getParamConfig(tag);
        if (wifiParamConfig == null) {
            return;
        }
        wifiParamConfig.setParam(value);
        requestComCommand(wifiParamConfig, listener);
    }

    /**
     * 重置参数
     */
    public void resetConfig(String tag) {
        resetConfig(tag, null);
    }


    /**
     * 重置参数
     */
    public void resetConfig(String tag, OnConfigCommandListener listener) {
        if (!isParamAvailable()) {
            return;
        }
        WifiConfig wifiConfig = cameraSetting.getWifiCameraParam().getConfig(tag);
        if (wifiConfig == null) {
            return;
        }
        wifiConfig.reset();
        requestComCommand(wifiConfig, listener);
    }

    /**
     * 更新分辨率
     * 若当前未获取图像推流成功，则直接失败
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    public void updateResolution(int width, int height, OnResolutionUpdateListener listener) {
        int position = getResolutionPosition(width, height);
        if (!isPrepared() || position < 0) {
            if (listener != null) {
                listener.onFail();
            }
            return;
        }
        netWork.updateResolution(position, new ComNetCallback<NCommandResult>(new ComNetCallback.OnResultListener<NCommandResult>() {
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
            public void onSuccess(NCommandResult result) {
                if (result.isSuccess()) {
                    loadConfig(true);
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    if (listener != null) {
                        listener.onFail();
                    }
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
     * 同步网络请求更新对焦
     *
     * @param focus 参数
     */
    public boolean updateFocusExecute(int focus) {
        if (!isParamAvailable()) {
            return false;
        }
        WifiParamConfig wifiParamConfig = getParamConfig(WifiConfig.TAG_PARAM_FOCUS);
        if (wifiParamConfig == null) {
            return false;
        }
        wifiParamConfig.setParam(focus);
        boolean result = netWork.updateFocusExecute(focus);
        if (result) {
            refreshConfig(wifiParamConfig);
        }
        return result;
    }

    /**
     * 从图像数据流中获取图像Bitmap
     *
     * @return 是否成功获取数据帧
     */
    private boolean loadFrameFromStream() {
        if (!isPrepared()) {
            retryStream();
            return false;
        }
        Bitmap frame = null;
        try {
            frame = ImageStream.getInstance().readImageFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (frame == null) {
            retryTimes++;
//            cameraLogger.LogD("retryTimes = " + retryTimes);
            if (retryTimes >= WifiCameraConstant.DEFAULT_WIFI_CAMERA_RETRY_TIMES_THRESHOLD) {
                retryTimes = 0;
                retryStream();
            }
            return false;
        }
        latestBitmap = flipBitmap(frame);
        updateState(WifiCameraState.Previewing);
        handler.sendEmptyMessage(MSG_LOAD_FRAME);
        return true;
    }

    /**
     * 更新当前WiFi相机状态
     */
    private void updateState(WifiCameraState state) {
        if (curState == state) {
            return;
        }
        cameraLogger.LogD("WiFi State update : " + curState + " ==> " + state);
        curState = state;
        if (onCommandListener != null) {
            onCommandListener.onStateUpdate(curState);
        }
    }

    /**
     * 修改参数通用网络请求
     * 成功后自动更新参数配置，监听返回结果是否成功
     *
     * @param wifiConfig 需要修改的配置
     * @param listener   监听器
     */
    private void requestComCommand(WifiConfig wifiConfig, OnConfigCommandListener listener) {
        requestComCommand(wifiConfig, isSuccess -> {
            refreshConfig(wifiConfig);
            if (listener != null) {
                listener.onResult(isSuccess, wifiConfig);
            }
        });
    }

    /**
     * 修改参数通用网络请求
     * 未更新参数配置，监听返回结果是否成功
     *
     * @param wifiConfig 需要修改的配置
     * @param listener   监听器
     */
    private void requestComCommand(WifiConfig wifiConfig, CommandNetCallback.OnResultListener listener) {
        synchronized (netWork) {
            netWork.command(wifiConfig, new CommandNetCallback(listener));
        }
    }

    /**
     * 修改参数通用网络请求
     * 未更新参数配置，可在各个步骤进行监听
     *
     * @param wifiConfig 需要修改的配置
     * @param listener   监听器
     */
    private void requestComCommand(WifiConfig wifiConfig, ComNetCallback.OnResultListener<NCommandResult> listener) {
        synchronized (netWork) {
            netWork.command(wifiConfig, new ComNetCallback<NCommandResult>(listener));
        }
    }

    /**
     * 修改参数通用网络请求成功后，更新参数当前值，并打印日志
     *
     * @param wifiConfig 修改成功的参数
     */
    private void refreshConfig(WifiConfig wifiConfig) {
        int oldValue = wifiConfig.getCur();
        wifiConfig.refresh();
        int newValue = wifiConfig.getCur();

        StringBuilder stringBuilder = new StringBuilder("Config update : ");
        stringBuilder.append(wifiConfig.getName()).append(" : ");
        if (wifiConfig instanceof WifiAutoConfig) {
            WifiAutoConfig wifiAutoConfig = (WifiAutoConfig) wifiConfig;
            stringBuilder.append("Auto : ").append(wifiAutoConfig.isValueAuto(oldValue)).append(" ==> ")
                    .append(wifiAutoConfig.isValueAuto(newValue));
        } else {
            stringBuilder.append("Param : ").append(oldValue).append(" ==> ").append(newValue);
        }
        cameraLogger.LogD(stringBuilder.toString());
    }

    /**
     * 对bitmap进行镜像翻转处理
     */
    private Bitmap flipBitmap(Bitmap bitmap) {
        WifiCameraSP.Editor editor = WifiCameraSP.getEditor(context);
        boolean isFlipHorizontal = editor.isFlipHorizontal();
        boolean isFlipVertical = editor.isFlipVertical();
        if (isFlipHorizontal || isFlipVertical) {
            Matrix matrix = new Matrix();
            matrix.postScale(isFlipHorizontal ? -1f : 1f, isFlipVertical ? -1f : 1f);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        } else {
            return bitmap;
        }
    }

    /**
     * 获取分辨率在参数中对应的序号
     * 若获取失败返回-1
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    private int getResolutionPosition(int width, int height) {
        WifiCameraParam wifiCameraParam = cameraSetting.getWifiCameraParam();
        if (!wifiCameraParam.isAvailable()) {
            return -1;
        }
        WifiCameraResolution wifiCameraResolution = wifiCameraParam.getWifiCameraResolution();
        WifiCameraResolution.Resolution resolution = wifiCameraResolution.findResolution(width, height);
        return resolution != null ? resolution.getPosition() : -1;
    }

    /************************************设置监听，获取信息******************************************/

    /**
     * 设置WiFi相机控制监听
     */
    public void setOnCommandListener(OnCommandListener onCommandListener) {
        this.onCommandListener = onCommandListener;
    }

    /**
     * 获取当前WiFi相机状态
     */
    public WifiCameraState getCurState() {
        return curState;
    }

    /**
     * 获取最新获取的图像Bitmap
     */
    public Bitmap getLatestBitmap() {
        return latestBitmap;
    }

    /**
     * 是否获取图像数据流成功
     */
    public boolean isPrepared() {
        return isPrepared && ImageStream.getInstance() != null;
    }

    /**
     * 是否wifi相机参数可用
     */
    public boolean isParamAvailable() {
        return cameraSetting.isAvailable();
    }

    /**
     * 是否正常预览中
     */
    public boolean isPreviewing() {
        return isPrepared() && isParamAvailable() && curState == WifiCameraState.Previewing;
    }

    @Override
    public void onStartLoop() {

    }

    @Override
    public void onLooping() {
        if (curState == WifiCameraState.Free || isReleased) {
            return;
        }
        long startTime = System.currentTimeMillis();
        boolean result = loadFrameFromStream();
        long costTime = System.currentTimeMillis() - startTime;
        if (result && WifiCameraConstant.IS_LOG_FRAME_DATA) {
            cameraLogger.LogD("load one frame cost time : " + costTime);
        }
    }

    @Override
    public void onStopLoop() {

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_START_STREAM:
                if (onCommandListener != null) {
                    boolean isRetry = (boolean) msg.obj;
                    onCommandListener.onStreamStart(isRetry);
                }
                break;
            case MSG_STOP_STREAM:
                if (onCommandListener != null) {
                    boolean isRetry = (boolean) msg.obj;
                    onCommandListener.onStreamStop(isRetry);
                }
                break;
            case MSG_RETRY_STREAM:
                startStream();
                break;
            case MSG_LOAD_FRAME:
                wifiCameraView.setBitmap(latestBitmap);
                if (onCommandListener != null) {
                    onCommandListener.onLoadFrame(latestBitmap);
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * WiFi相机控制监听
     */
    public interface OnCommandListener {

        /**
         * 获取图像数据流开始
         *
         * @param isRetry 是否重试
         */
        void onStreamStart(boolean isRetry);

        /**
         * 获取图像数据流停止
         *
         * @param isRetry 是否重试
         */
        void onStreamStop(boolean isRetry);

        /**
         * WiFi相机状态更新
         *
         * @param state 当前WiFi相机连接状态
         */
        void onStateUpdate(WifiCameraState state);

        /**
         * 获取画面帧
         *
         * @param bitmap 画面帧Bitmap
         */
        void onLoadFrame(Bitmap bitmap);

        /**
         * WiFi相机参数更新
         *
         * @param param   当前参数配置
         * @param isReset 是否需要重置UI控件
         */
        void onParamUpdate(WifiCameraParam param, boolean isReset);
    }

    /**
     * 读取单帧图片监听
     */
    public interface OnLoadOneFrameListener {

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
         *
         * @param bitmap 单帧图片位图
         */
        void onSuccess(Bitmap bitmap);

        /**
         * 出错
         *
         * @param error 错误信息
         */
        void onError(String error);
    }

    /**
     * 参数操作命令监听
     */
    public interface OnConfigCommandListener {

        /**
         * 操作结果
         *
         * @param isSuccess 是否成功
         * @param config    更新后的对应参数信息
         */
        void onResult(boolean isSuccess, WifiConfig config);
    }

    /**
     * 分辨率更新监听
     */
    public interface OnResolutionUpdateListener {

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
        void onSuccess();

        /**
         * 失败
         */
        void onFail();
    }
}
