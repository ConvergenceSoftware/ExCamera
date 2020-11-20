package com.convergence.excamera.sdk.usb.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;

import com.convergence.excamera.sdk.common.BaseCameraView;
import com.convergence.excamera.sdk.common.PreviewTransformInfo;

/**
 * USB相机预览控件
 * 可通过UsbCameraConstant中PREVIEW_TYPE更改预览方式
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class UsbCameraView extends BaseCameraView {

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
