package com.convergence.excamera.sdk.usb.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.R;
import com.convergence.excamera.sdk.usb.UsbCameraConstant;
import com.convergence.excamera.sdk.common.BitmapUtil;
import com.convergence.excamera.sdk.common.CameraLogger;
import com.convergence.excamera.sdk.usb.UsbCameraState;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSP;
import com.convergence.excamera.sdk.usb.entity.UsbCameraSetting;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.config.base.UVCAutoConfig;
import com.serenegiant.usb.config.base.UVCConfig;
import com.serenegiant.usb.config.base.UVCParamConfig;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * UVC Camera控制器，封装了对UVC Camera底层调用的大部分命令
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraCommand implements IFrameCallback, Handler.Callback {

    private static final int MSG_LOAD_FRAME = 100;
    private static final int MSG_START_PREVIEW = 101;

    private final Object configSync = new Object();
    private Context context;
    private UsbCameraView usbCameraView;
    private UsbDevConnection usbDevConnection;
    private UsbCameraSetting usbCameraSetting;
    private USBMonitor usbMonitor;
    private UVCCamera uvcCamera;
    private Handler handler;
    private CameraLogger cameraLogger = UsbCameraConstant.GetLogger();

    private int previewWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
    private int previewHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
    private List<Size> supportedSizeList;
    private android.util.Size updateSize;

    private Bitmap latestBitmap;
    private UsbCameraState curState = UsbCameraState.Free;
    private OnCommandListener onCommandListener;
    private OnConnectListener onConnectListener;

    public UsbCameraCommand(Context context, UsbCameraView usbCameraView) {
        this.context = context;
        this.usbCameraView = usbCameraView;
        usbDevConnection = new UsbDevConnection(this);
        usbMonitor = new USBMonitor(context.getApplicationContext(), usbDevConnection);
        usbCameraSetting = UsbCameraSetting.getInstance();
        handler = new Handler(this);
    }

    /****************************************预览基础命令*******************************************/

    /**
     * 注册USB广播监听
     */
    public void registerUsb() {
        usbMonitor.register();
    }

    /**
     * 注销USB广播监听
     */
    public void unregisterUsb() {
        usbMonitor.unregister();
    }

    /**
     * 连接默认的USB设备
     */
    public void requestUsbPermission() {
        List<UsbDevice> usbDeviceList = getUsbDeviceList();
        if (usbDeviceList == null || usbDeviceList.isEmpty()) {
            return;
        }
        requestUsbPermission(usbDeviceList.get(0));
    }

    /**
     * 连接指定的USB设备
     *
     * @param usbDevice 指定的USB设备
     */
    public void requestUsbPermission(UsbDevice usbDevice) {
        if (usbDevice == null) {
            return;
        }
        usbMonitor.requestPermission(usbDevice);
    }

    /**
     * 打开UVC Camera
     * 须获取已连接上的USB设备信息
     */
    public void openCamera() {
        boolean result = false;
        UsbDevConnection.ConnectionInfo connectionInfo = usbDevConnection.getConnectionInfo();
        if (connectionInfo == null) {
            return;
        }
        try {
            uvcCamera = new UVCCamera();
            uvcCamera.open(connectionInfo.getCtrlBlock());
            supportedSizeList = uvcCamera.getSupportedSizeList();
            usbCameraSetting.initConnection(connectionInfo);
            usbCameraSetting.initResolution(supportedSizeList);
            updateState(UsbCameraState.Opened);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!result) {
            return;
        }
        refreshPreviewSize();
        if (onConnectListener != null) {
            onConnectListener.onCameraOpen();
        }
        handler.sendEmptyMessageDelayed(MSG_START_PREVIEW, 500);
    }

    /**
     * 关闭UVC Camera
     */
    public void closeCamera() {
        if (uvcCamera == null) {
            return;
        }
        stopPreview();
        uvcCamera.destroy();
        uvcCamera = null;
        updateState(UsbCameraState.Free);
        if (onConnectListener != null) {
            onConnectListener.onCameraClose();
        }
    }

    /**
     * 开启USB相机预览画面
     */
    public void startPreview() {
        if (uvcCamera == null || !isOpened()) {
            return;
        }
        try {
            if (updateSize != null && isSizeSupported(updateSize.getWidth(), updateSize.getHeight())) {
                uvcCamera.setPreviewSize(updateSize.getWidth(), updateSize.getHeight());
                updateSize = null;
            } else {
                uvcCamera.setPreviewSize(previewWidth, previewHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (UsbCameraConstant.PREVIEW_TYPE) {
            case Surface:
                uvcCamera.setPreviewDisplay(usbCameraView.getSurfaceTexture());
                break;
            case Draw:
                SurfaceTexture texture = new SurfaceTexture(0);
                uvcCamera.setPreviewDisplay(new Surface(texture));
                texture.release();
                break;
        }
        uvcCamera.setFrameCallback(this, UVCCamera.PIXEL_FORMAT_YUV420SP);
        uvcCamera.startPreview();
        uvcCamera.updateCameraParams();
        refreshPreviewSize();
        usbCameraView.resize(previewWidth, previewHeight);
        usbCameraSetting.refreshCurResolution(previewWidth, previewHeight);
        updateFlip();
        updateState(UsbCameraState.Previewing);
        if (onConnectListener != null) {
            onConnectListener.onPreviewStart();
        }
    }

    /**
     * 关闭USB相机预览画面
     */
    public void stopPreview() {
        if (uvcCamera != null) {
            uvcCamera.stopPreview();
        }
        if (curState != UsbCameraState.Free) {
            updateState(UsbCameraState.Opened);
        }
        if (onConnectListener != null) {
            onConnectListener.onPreviewStop();
        }
    }

    /**
     * 更新当前USB相机状态
     */
    public void updateState(UsbCameraState state) {
        if (curState == state) {
            return;
        }
        cameraLogger.LogD("Usb State update : " + curState + " ==> " + state);
        curState = state;
        if (onCommandListener != null) {
            onCommandListener.onStateUpdate(curState);
        }
    }

    /**
     * 更新分辨率（根据当前USB相机状态执行不同的操作）
     * Free:直接跳过
     * Connected:打开相应分辨率的USB相机
     * Opened:开始相应分辨率的预览
     * Previewing:先停止预览，延时开始预览
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    public void updateResolution(int width, int height) {
        switch (curState) {
            case Free:
                return;
            case Connected:
                updateSize = new android.util.Size(width, height);
                openCamera();
                break;
            case Opened:
                updateSize = new android.util.Size(width, height);
                startPreview();
                break;
            case Previewing:
                stopPreview();
                updateSize = new android.util.Size(width, height);
                handler.sendEmptyMessageDelayed(MSG_START_PREVIEW, 500);
                break;
            default:
                break;
        }
    }

    /**
     * 从UsbCameraSetting中获取镜像翻转参数，若是采用Surface方式预览，则应用于UsbCameraView的TransformInfo
     */
    public void updateFlip() {
        if (UsbCameraConstant.PREVIEW_TYPE == UsbCameraConstant.PreviewType.Surface) {
            UsbCameraSP.Editor editor = UsbCameraSP.getEditor(context);
            usbCameraView.setFlip(editor.isFlipHorizontal(), editor.isFlipVertical());
        }
    }

    /**************************************调节参数命令*********************************************/

    /**
     * 获取自动类调节参数信息
     */
    public UVCAutoConfig getAutoConfig(String tag) {
        if (!isOpened()) {
            return null;
        }
        switch (tag) {
            case UVCConfig.TAG_AUTO_FOCUS_AUTO:
                return uvcCamera.getConfigFocusAuto();
            case UVCConfig.TAG_AUTO_PRIVACY:
                return uvcCamera.getConfigPrivacy();
            case UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO:
                return uvcCamera.getConfigWhiteBalanceAuto();
            case UVCConfig.TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO:
                return uvcCamera.getConfigWhiteBalanceComponentAuto();
            default:
                return null;
        }
    }

    /**
     * 获取数值类调节参数信息
     */
    public UVCParamConfig getParamConfig(String tag) {
        if (!isOpened()) {
            return null;
        }
        switch (tag) {
            case UVCConfig.TAG_PARAM_SCANNING_MODE:
                return uvcCamera.getConfigScanningMode();
            case UVCConfig.TAG_PARAM_EXPOSURE_MODE:
                return uvcCamera.getConfigExposureMode();
            case UVCConfig.TAG_PARAM_EXPOSURE_PRIORITY:
                return uvcCamera.getConfigExposurePriority();
            case UVCConfig.TAG_PARAM_EXPOSURE:
                return uvcCamera.getConfigExposure();
            case UVCConfig.TAG_PARAM_FOCUS:
                return uvcCamera.getConfigFocus();
            case UVCConfig.TAG_PARAM_FOCUS_REL:
                return uvcCamera.getConfigFocusRel();
            case UVCConfig.TAG_PARAM_IRIS:
                return uvcCamera.getConfigIris();
            case UVCConfig.TAG_PARAM_IRIS_REL:
                return uvcCamera.getConfigIrisRel();
            case UVCConfig.TAG_PARAM_ZOOM:
                return uvcCamera.getConfigZoom();
            case UVCConfig.TAG_PARAM_ZOOM_REL:
                return uvcCamera.getConfigZoomRel();
            case UVCConfig.TAG_PARAM_PAN:
                return uvcCamera.getConfigPan();
            case UVCConfig.TAG_PARAM_PAN_REL:
                return uvcCamera.getConfigPanRel();
            case UVCConfig.TAG_PARAM_TILT:
                return uvcCamera.getConfigTilt();
            case UVCConfig.TAG_PARAM_TILT_REL:
                return uvcCamera.getConfigTiltRel();
            case UVCConfig.TAG_PARAM_ROLL:
                return uvcCamera.getConfigRoll();
            case UVCConfig.TAG_PARAM_ROLL_REL:
                return uvcCamera.getConfigRollRel();
            case UVCConfig.TAG_PARAM_BRIGHTNESS:
                return uvcCamera.getConfigBrightness();
            case UVCConfig.TAG_PARAM_CONTRAST:
                return uvcCamera.getConfigContrast();
            case UVCConfig.TAG_PARAM_HUE:
                return uvcCamera.getConfigHue();
            case UVCConfig.TAG_PARAM_SATURATION:
                return uvcCamera.getConfigSaturation();
            case UVCConfig.TAG_PARAM_SHARPNESS:
                return uvcCamera.getConfigSharpness();
            case UVCConfig.TAG_PARAM_GAMMA:
                return uvcCamera.getConfigGamma();
            case UVCConfig.TAG_PARAM_GAIN:
                return uvcCamera.getConfigGain();
            case UVCConfig.TAG_PARAM_WHITE_BALANCE:
                return uvcCamera.getConfigWhiteBalance();
            case UVCConfig.TAG_PARAM_WHITE_BALANCE_COMPONENT:
                return uvcCamera.getConfigWhiteBalanceComponent();
            case UVCConfig.TAG_PARAM_BACKLIGHT_COMPENSATION:
                return uvcCamera.getConfigBacklightCompensation();
            case UVCConfig.TAG_PARAM_POWER_LINE_FREQUENCY:
                return uvcCamera.getConfigPowerLineFrequency();
            case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER:
                return uvcCamera.getConfigDigitalMultiplier();
            case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT:
                return uvcCamera.getConfigDigitalMultiplierLimit();
            case UVCConfig.TAG_PARAM_ANALOG_VIDEO_STANDARD:
                return uvcCamera.getConfigAnalogVideoStandard();
            case UVCConfig.TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS:
                return uvcCamera.getConfigAnalogVideoLockStatus();
            default:
                return null;
        }
    }

    /**
     * 判断参数是否可进行调节
     */
    public boolean checkConfigEnable(String tag) {
        UVCConfig config = getAutoConfig(tag);
        if (config == null) {
            config = getParamConfig(tag);
        }
        return config != null && config.isEnable();
    }

    /**
     * 判断自动类调节参数当前是否自动状态
     */
    public boolean getAuto(String tag) {
        if (!isOpened() || !checkConfigEnable(tag)) {
            return false;
        }
        synchronized (configSync) {
            switch (tag) {
                case UVCConfig.TAG_AUTO_FOCUS_AUTO:
                    return uvcCamera.getFocusAuto();
                case UVCConfig.TAG_AUTO_PRIVACY:
                    return uvcCamera.getPrivacy();
                case UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO:
                    return uvcCamera.getWhiteBalanceAuto();
                case UVCConfig.TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO:
                    return uvcCamera.getWhiteBalanceComponentAuto();
                default:
                    return false;
            }
        }
    }

    /**
     * 设置自动类调节参数当前状态
     */
    public void setAuto(String tag, boolean value) {
        if (!isOpened() || !checkConfigEnable(tag)) {
            return;
        }
        synchronized (configSync) {
            switch (tag) {
                case UVCConfig.TAG_AUTO_FOCUS_AUTO:
                    uvcCamera.setFocusAuto(value);
                    break;
                case UVCConfig.TAG_AUTO_PRIVACY:
                    uvcCamera.setPrivacy(value);
                    break;
                case UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO:
                    uvcCamera.setWhiteBalanceAuto(value);
                    break;
                case UVCConfig.TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO:
                    uvcCamera.setWhiteBalanceComponentAuto(value);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 重置自动类调节参数为默认
     */
    public void resetAuto(String tag) {
        if (!isOpened() || !checkConfigEnable(tag)) {
            return;
        }
        synchronized (configSync) {
            switch (tag) {
                case UVCConfig.TAG_AUTO_FOCUS_AUTO:
                    uvcCamera.resetFocusAuto();
                    break;
                case UVCConfig.TAG_AUTO_PRIVACY:
                    uvcCamera.resetPrivacy();
                    break;
                case UVCConfig.TAG_AUTO_WHITE_BALANCE_AUTO:
                    uvcCamera.resetWhiteBalanceAuto();
                    break;
                case UVCConfig.TAG_AUTO_WHITE_BALANCE_COMPONENT_AUTO:
                    uvcCamera.resetWhiteBalanceComponentAuto();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取数值类参数当前数值
     */
    public int getParam(String tag) {
        if (!isOpened() || !checkConfigEnable(tag)) {
            return 0;
        }
        synchronized (configSync) {
            switch (tag) {
                case UVCConfig.TAG_PARAM_SCANNING_MODE:
                    return uvcCamera.getScanningMode();
                case UVCConfig.TAG_PARAM_EXPOSURE_MODE:
                    return uvcCamera.getExposureMode();
                case UVCConfig.TAG_PARAM_EXPOSURE_PRIORITY:
                    return uvcCamera.getExposurePriority();
                case UVCConfig.TAG_PARAM_EXPOSURE:
                    return uvcCamera.getExposure();
                case UVCConfig.TAG_PARAM_FOCUS:
                    return uvcCamera.getFocus();
                case UVCConfig.TAG_PARAM_FOCUS_REL:
                    return uvcCamera.getFocusRel();
                case UVCConfig.TAG_PARAM_IRIS:
                    return uvcCamera.getIris();
                case UVCConfig.TAG_PARAM_IRIS_REL:
                    return uvcCamera.getIrisRel();
                case UVCConfig.TAG_PARAM_ZOOM:
                    return uvcCamera.getZoom();
                case UVCConfig.TAG_PARAM_ZOOM_REL:
                    return uvcCamera.getZoomRel();
                case UVCConfig.TAG_PARAM_PAN:
                    return uvcCamera.getPan();
                case UVCConfig.TAG_PARAM_PAN_REL:
                    return uvcCamera.getPanRel();
                case UVCConfig.TAG_PARAM_TILT:
                    return uvcCamera.getTilt();
                case UVCConfig.TAG_PARAM_TILT_REL:
                    return uvcCamera.getTiltRel();
                case UVCConfig.TAG_PARAM_ROLL:
                    return uvcCamera.getRoll();
                case UVCConfig.TAG_PARAM_ROLL_REL:
                    return uvcCamera.getRollRel();
                case UVCConfig.TAG_PARAM_BRIGHTNESS:
                    return uvcCamera.getBrightness();
                case UVCConfig.TAG_PARAM_CONTRAST:
                    return uvcCamera.getContrast();
                case UVCConfig.TAG_PARAM_HUE:
                    return uvcCamera.getHue();
                case UVCConfig.TAG_PARAM_SATURATION:
                    return uvcCamera.getSaturation();
                case UVCConfig.TAG_PARAM_SHARPNESS:
                    return uvcCamera.getSharpness();
                case UVCConfig.TAG_PARAM_GAMMA:
                    return uvcCamera.getGamma();
                case UVCConfig.TAG_PARAM_GAIN:
                    return uvcCamera.getGain();
                case UVCConfig.TAG_PARAM_WHITE_BALANCE:
                    return uvcCamera.getWhiteBalance();
                case UVCConfig.TAG_PARAM_WHITE_BALANCE_COMPONENT:
                    return uvcCamera.getWhiteBalanceComponent();
                case UVCConfig.TAG_PARAM_BACKLIGHT_COMPENSATION:
                    return uvcCamera.getBacklightCompensation();
                case UVCConfig.TAG_PARAM_POWER_LINE_FREQUENCY:
                    return uvcCamera.getPowerLineFrequency();
                case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER:
                    return uvcCamera.getDigitalMultiplier();
                case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT:
                    return uvcCamera.getDigitalMultiplierLimit();
                case UVCConfig.TAG_PARAM_ANALOG_VIDEO_STANDARD:
                    return uvcCamera.getAnalogVideoStandard();
                case UVCConfig.TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS:
                    return uvcCamera.getAnalogVideoLockStatus();
                default:
                    return 0;
            }
        }
    }

    /**
     * 设置数值类参数为指定数值
     */
    public void setParam(String tag, int value) {
        if (!isOpened() || !checkConfigEnable(tag)) {
            return;
        }
        synchronized (configSync) {
            switch (tag) {
                case UVCConfig.TAG_PARAM_SCANNING_MODE:
                    uvcCamera.setScanningMode(value);
                    break;
                case UVCConfig.TAG_PARAM_EXPOSURE_MODE:
                    uvcCamera.setExposureMode(value);
                    break;
                case UVCConfig.TAG_PARAM_EXPOSURE_PRIORITY:
                    uvcCamera.setExposurePriority(value);
                    break;
                case UVCConfig.TAG_PARAM_EXPOSURE:
                    uvcCamera.setExposure(value);
                    break;
                case UVCConfig.TAG_PARAM_FOCUS:
                    uvcCamera.setFocus(value);
                    break;
                case UVCConfig.TAG_PARAM_FOCUS_REL:
                    uvcCamera.setFocusRel(value);
                    break;
                case UVCConfig.TAG_PARAM_IRIS:
                    uvcCamera.setIris(value);
                    break;
                case UVCConfig.TAG_PARAM_IRIS_REL:
                    uvcCamera.setIrisRel(value);
                    break;
                case UVCConfig.TAG_PARAM_ZOOM:
                    uvcCamera.setZoom(value);
                    break;
                case UVCConfig.TAG_PARAM_ZOOM_REL:
                    uvcCamera.setZoomRel(value);
                    break;
                case UVCConfig.TAG_PARAM_PAN:
                    uvcCamera.setPan(value);
                    break;
                case UVCConfig.TAG_PARAM_PAN_REL:
                    uvcCamera.setPanRel(value);
                    break;
                case UVCConfig.TAG_PARAM_TILT:
                    uvcCamera.setTilt(value);
                    break;
                case UVCConfig.TAG_PARAM_TILT_REL:
                    uvcCamera.setTiltRel(value);
                    break;
                case UVCConfig.TAG_PARAM_ROLL:
                    uvcCamera.setRoll(value);
                    break;
                case UVCConfig.TAG_PARAM_ROLL_REL:
                    uvcCamera.setRollRel(value);
                    break;
                case UVCConfig.TAG_PARAM_BRIGHTNESS:
                    uvcCamera.setBrightness(value);
                    break;
                case UVCConfig.TAG_PARAM_CONTRAST:
                    uvcCamera.setContrast(value);
                    break;
                case UVCConfig.TAG_PARAM_HUE:
                    uvcCamera.setHue(value);
                    break;
                case UVCConfig.TAG_PARAM_SATURATION:
                    uvcCamera.setSaturation(value);
                    break;
                case UVCConfig.TAG_PARAM_SHARPNESS:
                    uvcCamera.setSharpness(value);
                    break;
                case UVCConfig.TAG_PARAM_GAMMA:
                    uvcCamera.setGamma(value);
                    break;
                case UVCConfig.TAG_PARAM_GAIN:
                    uvcCamera.setGain(value);
                    break;
                case UVCConfig.TAG_PARAM_WHITE_BALANCE:
                    uvcCamera.setWhiteBalance(value);
                    break;
                case UVCConfig.TAG_PARAM_WHITE_BALANCE_COMPONENT:
                    uvcCamera.setWhiteBalanceComponent(value);
                    break;
                case UVCConfig.TAG_PARAM_BACKLIGHT_COMPENSATION:
                    uvcCamera.setBacklightCompensation(value);
                    break;
                case UVCConfig.TAG_PARAM_POWER_LINE_FREQUENCY:
                    uvcCamera.setPowerLineFrequency(value);
                    break;
                case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER:
                    uvcCamera.setDigitalMultiplier(value);
                    break;
                case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT:
                    uvcCamera.setDigitalMultiplierLimit(value);
                    break;
                case UVCConfig.TAG_PARAM_ANALOG_VIDEO_STANDARD:
                    uvcCamera.setAnalogVideoStandard(value);
                    break;
                case UVCConfig.TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS:
                    uvcCamera.setAnalogVideoLockStatus(value);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 重置数值类参数为默认
     */
    public void resetParam(String tag) {
        if (!isOpened() || !checkConfigEnable(tag)) {
            return;
        }
        synchronized (configSync) {
            switch (tag) {
                case UVCConfig.TAG_PARAM_SCANNING_MODE:
                    uvcCamera.resetScanningMode();
                    break;
                case UVCConfig.TAG_PARAM_EXPOSURE_MODE:
                    uvcCamera.resetExposureMode();
                    break;
                case UVCConfig.TAG_PARAM_EXPOSURE_PRIORITY:
                    uvcCamera.resetExposurePriority();
                    break;
                case UVCConfig.TAG_PARAM_EXPOSURE:
                    uvcCamera.resetExposure();
                    break;
                case UVCConfig.TAG_PARAM_FOCUS:
                    uvcCamera.resetFocus();
                    break;
                case UVCConfig.TAG_PARAM_FOCUS_REL:
                    uvcCamera.resetFocusRel();
                    break;
                case UVCConfig.TAG_PARAM_IRIS:
                    uvcCamera.resetIris();
                    break;
                case UVCConfig.TAG_PARAM_IRIS_REL:
                    uvcCamera.resetIrisRel();
                    break;
                case UVCConfig.TAG_PARAM_ZOOM:
                    uvcCamera.resetZoom();
                    break;
                case UVCConfig.TAG_PARAM_ZOOM_REL:
                    uvcCamera.resetZoomRel();
                    break;
                case UVCConfig.TAG_PARAM_PAN:
                    uvcCamera.resetPan();
                    break;
                case UVCConfig.TAG_PARAM_PAN_REL:
                    uvcCamera.resetPanRel();
                    break;
                case UVCConfig.TAG_PARAM_TILT:
                    uvcCamera.resetTilt();
                    break;
                case UVCConfig.TAG_PARAM_TILT_REL:
                    uvcCamera.resetTiltRel();
                    break;
                case UVCConfig.TAG_PARAM_ROLL:
                    uvcCamera.resetRoll();
                    break;
                case UVCConfig.TAG_PARAM_ROLL_REL:
                    uvcCamera.resetRollRel();
                    break;
                case UVCConfig.TAG_PARAM_BRIGHTNESS:
                    uvcCamera.resetBrightness();
                    break;
                case UVCConfig.TAG_PARAM_CONTRAST:
                    uvcCamera.resetContrast();
                    break;
                case UVCConfig.TAG_PARAM_HUE:
                    uvcCamera.resetHue();
                    break;
                case UVCConfig.TAG_PARAM_SATURATION:
                    uvcCamera.resetSaturation();
                    break;
                case UVCConfig.TAG_PARAM_SHARPNESS:
                    uvcCamera.resetSharpness();
                    break;
                case UVCConfig.TAG_PARAM_GAMMA:
                    uvcCamera.resetGamma();
                    break;
                case UVCConfig.TAG_PARAM_GAIN:
                    uvcCamera.resetGain();
                    break;
                case UVCConfig.TAG_PARAM_WHITE_BALANCE:
                    uvcCamera.resetWhiteBalance();
                    break;
                case UVCConfig.TAG_PARAM_WHITE_BALANCE_COMPONENT:
                    uvcCamera.resetWhiteBalanceComponent();
                    break;
                case UVCConfig.TAG_PARAM_BACKLIGHT_COMPENSATION:
                    uvcCamera.resetBacklightCompensation();
                    break;
                case UVCConfig.TAG_PARAM_POWER_LINE_FREQUENCY:
                    uvcCamera.resetPowerLineFrequency();
                    break;
                case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER:
                    uvcCamera.resetDigitalMultiplier();
                    break;
                case UVCConfig.TAG_PARAM_DIGITAL_MULTIPLIER_LIMIT:
                    uvcCamera.resetDigitalMultiplierLimit();
                    break;
                case UVCConfig.TAG_PARAM_ANALOG_VIDEO_STANDARD:
                    uvcCamera.resetAnalogVideoStandard();
                    break;
                case UVCConfig.TAG_PARAM_ANALOG_VIDEO_LOCK_STATUS:
                    uvcCamera.resetAnalogVideoLockStatus();
                    break;
                default:
                    break;
            }
        }
    }

    /************************************设置监听，获取信息******************************************/

    /**
     * 设置UVC Camera控制监听
     */
    public void setOnCommandListener(OnCommandListener onCommandListener) {
        this.onCommandListener = onCommandListener;
    }

    /**
     * 设置UVC Camera连接监听
     */
    public void setOnConnectListener(OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
        usbDevConnection.setOnConnectListener(onConnectListener);
    }

    /**
     * 获取最新获取的Bitmap
     */
    public Bitmap getLatestBitmap() {
        return latestBitmap;
    }

    /**
     * 获取全部参数描述
     */
    public String getAllConfigDes() {
        return isOpened() ? uvcCamera.getAllConfigDes() : "";
    }

    /**
     * 获取设备连接信息
     */
    public UsbDevConnection.ConnectionInfo getConnectionInfo() {
        return isOpened() ? usbDevConnection.getConnectionInfo() : null;
    }

    /**
     * 获取当前连接上的USB设备列表
     */
    public List<UsbDevice> getUsbDeviceList() {
        List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(context.getApplicationContext(), R.xml.device_filter);
        return usbMonitor.getDeviceList(deviceFilters);
    }

    /**
     * 获取当前预览分辨率，若USB相机未连接并打开，则返回null
     */
    public Size getCurPreviewSize() {
        return uvcCamera != null ? uvcCamera.getPreviewSize() : null;
    }

    /**
     * 获取当前USB相机状态
     */
    public UsbCameraState getCurState() {
        return curState;
    }

    /**
     * 打开相机后，更新当前预览分辨率
     */
    private void refreshPreviewSize() {
        if (uvcCamera == null) {
            return;
        }
        Size size = getCurPreviewSize();
        if (size != null) {
            previewWidth = size.width;
            previewHeight = size.height;
        } else {
            previewWidth = uvcCamera.getCurrentWidth();
            previewHeight = uvcCamera.getCurrentHeight();
        }
    }

    /**
     * 对bitmap进行镜像翻转处理
     */
    private Bitmap flipBitmap(Bitmap bitmap) {
        UsbCameraSP.Editor editor = UsbCameraSP.getEditor(context);
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
     * 是否USB相机支持该分辨率
     *
     * @param width  分辨率宽
     * @param height 分辨率高
     */
    private boolean isSizeSupported(int width, int height) {
        if (supportedSizeList == null || supportedSizeList.isEmpty()) {
            return false;
        }
        for (int i = 0; i < supportedSizeList.size(); i++) {
            Size size = supportedSizeList.get(i);
            if (size.width == width && size.height == height) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否已经连接上USB设备
     */
    public boolean isConnected() {
        return curState != UsbCameraState.Free && usbDevConnection.isConnected();
    }

    /**
     * 是否已获取USB权限并打开相机
     */
    public boolean isOpened() {
        return uvcCamera != null && isConnected() && (curState == UsbCameraState.Opened || curState == UsbCameraState.Previewing);
    }

    /**
     * 是否正在预览
     */
    public boolean isPreviewing() {
        return uvcCamera != null && isOpened() && curState == UsbCameraState.Previewing;
    }

    /**
     * 获取图像帧数据
     *
     * @param frame this is direct ByteBuffer from JNI layer and you should handle it's byte order and limitation.
     */
    @Override
    public void onFrame(ByteBuffer frame) {
        int len = frame.capacity();
        final byte[] yuv = new byte[len];
        frame.get(yuv);
        Bitmap bitmap = BitmapUtil.getBitmapByNV21(context, yuv, previewWidth, previewHeight);
        if (bitmap != null) {
            latestBitmap = flipBitmap(bitmap);
            if (UsbCameraConstant.PREVIEW_TYPE == UsbCameraConstant.PreviewType.Draw) {
                usbCameraView.setBitmap(latestBitmap);
            }
            handler.sendEmptyMessage(MSG_LOAD_FRAME);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_LOAD_FRAME:
                if (onCommandListener != null) {
                    onCommandListener.onLoadFrame(latestBitmap);
                }
                break;
            case MSG_START_PREVIEW:
                startPreview();
                break;
        }
        return false;
    }

    /**
     * UVC Camera控制监听
     */
    public interface OnCommandListener {

        /**
         * UVC Camera状态更新
         *
         * @param state 当前UVC Camera状态
         */
        void onStateUpdate(UsbCameraState state);

        /**
         * 获取画面帧
         *
         * @param bitmap 画面帧Bitmap
         */
        void onLoadFrame(Bitmap bitmap);
    }

    /**
     * UVC Camera连接监听
     */
    public interface OnConnectListener {

        /**
         * USB设备连接通信建立
         */
        void onUsbConnect();

        /**
         * USB设备连接通信断开
         */
        void onUsbDisConnect();

        /**
         * UVC Camera开启
         */
        void onCameraOpen();

        /**
         * UVC Camera关闭
         */
        void onCameraClose();

        /**
         * 预览开始
         */
        void onPreviewStart();

        /**
         * 预览停止
         */
        void onPreviewStop();
    }
}
