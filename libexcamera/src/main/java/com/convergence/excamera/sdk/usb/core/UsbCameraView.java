package com.convergence.excamera.sdk.usb.core;

import android.content.Context;
import android.util.AttributeSet;

import com.convergence.excamera.sdk.common.view.BaseExCameraView;

/**
 * USB相机预览控件
 * 可通过UsbCameraConstant中PREVIEW_TYPE更改预览方式
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraView extends BaseExCameraView {

    public UsbCameraView(Context context) {
        super(context);
    }

    public UsbCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UsbCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
