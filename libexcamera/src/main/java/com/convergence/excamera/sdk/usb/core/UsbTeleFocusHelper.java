package com.convergence.excamera.sdk.usb.core;

import com.convergence.excamera.sdk.common.TeleFocusHelper;
import com.serenegiant.usb.config.base.UVCConfig;

/**
 * 望远相机USB连接调焦助手
 *
 * @Author WangZiheng
 * @CreateDate 2020-12-07
 * @Organization Convergence Ltd.
 */
public class UsbTeleFocusHelper extends TeleFocusHelper {

    protected final UsbCameraController usbCameraController;

    public UsbTeleFocusHelper(UsbCameraController usbCameraController) {
        super();
        this.usbCameraController = usbCameraController;
        bindImgProvider(usbCameraController);
        bindAFCallback(usbCameraController);
    }

    @Override
    protected void setFocus(boolean isAF, int value) {
        if (usbCameraController != null && usbCameraController.isPreviewing()) {
            usbCameraController.setParam(UVCConfig.TAG_PARAM_FOCUS, value);
        }
    }
}
