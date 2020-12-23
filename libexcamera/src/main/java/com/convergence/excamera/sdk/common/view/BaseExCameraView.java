package com.convergence.excamera.sdk.common.view;

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

import com.convergence.excamera.sdk.common.PreviewTransformInfo;

/**
 * 相机预览控件基类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-11
 * @Organization Convergence Ltd.
 */
public abstract class BaseExCameraView extends TextureView implements Handler.Callback,
        TextureView.SurfaceTextureListener, PreviewTransformInfo.OnTransformListener {

    private static final int MSG_SET_TRANSFORM = 100;

    private static final int THRESHOLD_ZOOM = 20;

    protected Context context;
    protected Handler handler;
    protected PreviewTransformInfo transformInfo;
    protected PreviewTransformInfo.TouchState touchState;

    private ScaleGestureDetector gestureDetector;
    private Paint bitmapPaint;
    private Paint cleanPaint;
    private Rect srcRect;
    private RectF dstRect;
    private PointF touchPoint;
    private boolean isSurfaceAvailable = false;

    public BaseExCameraView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BaseExCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public BaseExCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    protected void initAttrs(AttributeSet attrs) {

    }

    protected void init() {
        handler = new Handler(this);
        gestureDetector = new ScaleGestureDetector(context, zoomGestureListener);
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        cleanPaint = new Paint();
        cleanPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        srcRect = new Rect();
        dstRect = new RectF();
        touchPoint = new PointF();
        transformInfo = new PreviewTransformInfo();
        transformInfo.setListener(this);
        setSurfaceTextureListener(this);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        handler.post(() -> {
            Canvas canvas = lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawPaint(cleanPaint);
            srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            dstRect.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(bitmap, srcRect, dstRect, bitmapPaint);
            unlockCanvasAndPost(canvas);
            if (transformInfo.isAvailable() && touchState == PreviewTransformInfo.TouchState.Normal) {
                sendTransformMessage(transformInfo.getMatrix());
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isAvailable()) {
            return false;
        }
        if (event.getPointerCount() >= 2) {
            gestureDetector.onTouchEvent(event);
        } else {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    touchState = PreviewTransformInfo.TouchState.Move;
                    touchPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (touchState == PreviewTransformInfo.TouchState.Move && transformInfo != null) {
                        transformInfo.moveCenter(event.getX() - touchPoint.x, event.getY() - touchPoint.y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (touchState == PreviewTransformInfo.TouchState.Move && transformInfo != null) {
                        transformInfo.resetCenter(event.getX() - touchPoint.x, event.getY() - touchPoint.y);
                    }
                    touchState = PreviewTransformInfo.TouchState.Normal;
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * 调整控件显示图像区域大小，即Bitmap的尺寸
     */
    public void resize(int width, int height) {
        transformInfo.setBitmapSize(width, height);
    }

    /**
     * 设置镜像翻转
     *
     * @param isFlipHorizontal 是否水平翻转
     * @param isFlipVertical   是否垂直翻转
     */
    public void setFlip(boolean isFlipHorizontal, boolean isFlipVertical) {
        transformInfo.setFlip(isFlipHorizontal, isFlipVertical);
        if (transformInfo.isAvailable()) {
            sendTransformMessage(transformInfo.getMatrix());
        }
    }

    /**
     * 设置TextureView显示变换矩阵
     *
     * @param matrix 显示变换矩阵
     */
    protected void sendTransformMessage(Matrix matrix) {
        Message message = new Message();
        message.what = MSG_SET_TRANSFORM;
        message.obj = matrix;
        handler.sendMessage(message);
    }

    /**
     * 是否控件可用
     */
    @Override
    public boolean isAvailable() {
        return isSurfaceAvailable && super.isAvailable();
    }

    /**
     * 缩放手势监听
     */
    private ScaleGestureDetector.OnScaleGestureListener zoomGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

        private float originSpan = 0.0f;
        private float originZoom = PreviewTransformInfo.ZOOM_DEFAULT;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float spanChange = detector.getCurrentSpan() - originSpan;
            if (originZoom <= 0) {
                return false;
            }
            float zoomResult = originZoom;
            float minZoom = PreviewTransformInfo.ZOOM_MIN;
            float maxZoom = PreviewTransformInfo.ZOOM_MAX;
            float ratio = (maxZoom - minZoom) * 30;
            if (spanChange < -THRESHOLD_ZOOM || spanChange > THRESHOLD_ZOOM) {
                zoomResult = originZoom + spanChange / ratio;
            }
            zoomResult = MathUtils.clamp(zoomResult, minZoom, maxZoom);
            if (transformInfo != null) {
                transformInfo.setZoom(zoomResult);
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            touchState = PreviewTransformInfo.TouchState.Zoom;
            originSpan = detector.getCurrentSpan();
            originZoom = transformInfo.getZoom();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            touchState = PreviewTransformInfo.TouchState.Normal;
            originSpan = detector.getCurrentSpan();
            originZoom = PreviewTransformInfo.ZOOM_DEFAULT;
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        isSurfaceAvailable = true;
        transformInfo.setViewSize(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        transformInfo.setViewSize(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        isSurfaceAvailable = false;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_SET_TRANSFORM:
                Matrix matrix = (Matrix) msg.obj;
                setTransform(matrix);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onMatrixUpdate(Matrix matrix) {
        sendTransformMessage(matrix);
    }
}
