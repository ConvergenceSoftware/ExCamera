package com.convergence.excamera.sdk.common;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Size;

import androidx.core.math.MathUtils;

/**
 * 预览控件平移、缩放信息封装类，可设置回调进行相关操作
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class PreviewTransformInfo {

    public enum TouchState {
        /*
        默认状态
         */
        Normal,
        /*
        缩放状态
         */
        Zoom,
        /*
        单独拖动状态
         */
        Move
    }

    public static final float ZOOM_DEFAULT = 1.0f;
    public static final float ZOOM_MIN = 1.0f;
    public static final float ZOOM_MAX = 10.0f;

    private Size viewSize;
    private Size bitmapSize;
    private PointF centerPoint;
    private Matrix transformMatrix;
    private OnTransformListener listener;

    private float defaultScale;
    private float defaultPreviewWidth;
    private float defaultPreviewHeight;
    private float defaultRatioX = 1.0f;
    private float defaultRatioY = 1.0f;

    private float zoom = 1.0f;
    private float flipX;
    private float flipY;
    private boolean isFlipHorizontal = false;
    private boolean isFlipVertical = false;
    private boolean isAvailable = false;

    public PreviewTransformInfo() {
        centerPoint = new PointF();
        transformMatrix = new Matrix();
        flipX = isFlipHorizontal ? -1f : 1f;
        flipY = isFlipVertical ? -1f : 1f;
    }

    private void init() {
        if (!isPrepared()) {
            return;
        }
        float scaleWidth = (float) viewSize.getWidth() / bitmapSize.getWidth();
        float scaleHeight = (float) viewSize.getHeight() / bitmapSize.getHeight();
        if (scaleWidth < scaleHeight) {
            defaultScale = scaleWidth;
            defaultPreviewWidth = bitmapSize.getWidth() * defaultScale;
            defaultPreviewHeight = bitmapSize.getHeight() * defaultScale;
            defaultRatioX = 1.0f;
            defaultRatioY = defaultPreviewHeight / viewSize.getHeight();
        } else {
            defaultScale = scaleHeight;
            defaultPreviewWidth = bitmapSize.getWidth() * defaultScale;
            defaultPreviewHeight = bitmapSize.getHeight() * defaultScale;
            defaultRatioX = defaultPreviewWidth / viewSize.getWidth();
            defaultRatioY = 1.0f;
        }
        isAvailable = true;
        reset();
    }

    /**
     * 设置控件尺寸
     */
    public void setViewSize(int width, int height) {
        if (viewSize == null || viewSize.getWidth() != width || viewSize.getHeight() != height) {
            viewSize = new Size(width, height);
            init();
        }
    }

    /**
     * 设置Bitmap尺寸
     */
    public void setBitmapSize(int width, int height) {
        if (bitmapSize == null || bitmapSize.getWidth() != width || bitmapSize.getHeight() != height) {
            bitmapSize = new Size(width, height);
            init();
        }
    }

    /**
     * 设置镜像翻转
     *
     * @param isFlipHorizontal 是否水平翻转
     * @param isFlipVertical   是否垂直翻转
     */
    public void setFlip(boolean isFlipHorizontal, boolean isFlipVertical) {
        this.isFlipHorizontal = isFlipHorizontal;
        this.isFlipVertical = isFlipVertical;
        flipX = isFlipHorizontal ? -1f : 1f;
        flipY = isFlipVertical ? -1f : 1f;
    }

    /**
     * 设置监听
     */
    public void setListener(OnTransformListener listener) {
        this.listener = listener;
    }

    /**
     * 重置
     */
    public void reset() {
        centerPoint.set(viewSize.getWidth() / 2f, viewSize.getHeight() / 2f);
        zoom = ZOOM_DEFAULT;
        if (listener != null) {
            listener.onMatrixUpdate(getMatrix());
        }
    }

    public void setZoom(float zoom) {
        this.zoom = MathUtils.clamp(zoom, ZOOM_MIN, ZOOM_MAX);
        if (listener != null) {
            listener.onMatrixUpdate(getMatrix());
        }
    }

    public void moveCenter(float offsetX, float offsetY) {
        float scaleX = defaultRatioX * zoom * flipX;
        float scaleY = defaultRatioY * zoom * flipY;
        float pointX = centerPoint.x - offsetX * flipX;
        float pointY = centerPoint.y - offsetY * flipY;
        pointX = clampX(pointX);
        pointY = clampY(pointY);
        transformMatrix.setScale(scaleX, scaleY, pointX, pointY);
        if (listener != null) {
            listener.onMatrixUpdate(transformMatrix);
        }
    }

    public void resetCenter(float offsetX, float offsetY) {
        float pointX = centerPoint.x - offsetX * flipX;
        float pointY = centerPoint.y - offsetY * flipY;
        pointX = clampX(pointX);
        pointY = clampY(pointY);
        centerPoint.set(pointX, pointY);
        if (listener != null) {
            listener.onMatrixUpdate(getMatrix());
        }
    }

    public Matrix getMatrix() {
        transformMatrix.setScale(defaultRatioX * zoom * flipX, defaultRatioY * zoom * flipY, centerPoint.x, centerPoint.y);
        return transformMatrix;
    }

    public PointF getCenterPoint() {
        return centerPoint;
    }

    public float getAspectRatio() {
        return defaultScale;
    }

    public float getPreviewWidth() {
        return defaultPreviewWidth * zoom;
    }

    public float getPreviewHeight() {
        return defaultPreviewHeight * zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public boolean isPrepared() {
        return viewSize != null && bitmapSize != null;
    }

    public boolean isAvailable() {
        return isPrepared() && isAvailable;
    }

    private float clampX(float pointX) {
        //防止镜像翻转后画面移出控件外
        float minPointX = isFlipHorizontal ? viewSize.getWidth() * 0.05f : 0;
        float maxPointX = isFlipHorizontal ? viewSize.getWidth() * 0.95f : viewSize.getWidth();
        return MathUtils.clamp(pointX, minPointX, maxPointX);
    }

    private float clampY(float pointY) {
        //防止镜像翻转后画面移出控件外
        float minPointY = isFlipVertical ? viewSize.getHeight() * 0.05f : 0;
        float maxPointY = isFlipVertical ? viewSize.getHeight() * 0.95f : viewSize.getHeight();
        return MathUtils.clamp(pointY, minPointY, maxPointY);
    }

    public interface OnTransformListener {

        /**
         * 图像矩阵变换回调
         *
         * @param matrix 变换矩阵
         */
        void onMatrixUpdate(Matrix matrix);
    }
}
