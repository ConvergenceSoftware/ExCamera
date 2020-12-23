package com.convergence.excamera.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.convergence.excamera.R;

/**
 * 可调节比例的FrameLayout
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class RatioFrameLayout extends FrameLayout {

    private Context context;
    private boolean isDependOnWidth = true;
    private int widthWeight = 1;
    private int heightWeight = 1;
    private OnSizeChangeListener onSizeChangeListener;

    public RatioFrameLayout(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttributeSet(attrs);
        init();
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttributeSet(attrs);
        init();
    }

    /**
     * 获取自定义属性的值
     */
    private void initAttributeSet(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioFrameLayout);
        widthWeight = typedArray.getInt(R.styleable.RatioFrameLayout_rflWidthWeight, widthWeight);
        heightWeight = typedArray.getInt(R.styleable.RatioFrameLayout_rflHeightWeight, heightWeight);
        isDependOnWidth = typedArray.getBoolean(R.styleable.RatioFrameLayout_isDependOnWidth, isDependOnWidth);
        typedArray.recycle();
    }

    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.EXACTLY;
        float widthSize = (float) MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = (float) MeasureSpec.getSize(heightMeasureSpec);
        if (isDependOnWidth) {
            heightSize = widthSize * heightWeight / widthWeight;
        } else {
            widthSize = heightSize * widthWeight / heightWeight;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec((int) widthSize, mode), MeasureSpec.makeMeasureSpec((int) heightSize, mode));
        if (onSizeChangeListener != null) {
            onSizeChangeListener.onRflSizeChanged(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }

    public void setRatio(int widthWeight, int heightWeight) {
        this.widthWeight = widthWeight;
        this.heightWeight = heightWeight;
        requestLayout();
    }

    public interface OnSizeChangeListener {

        /**
         * 尺寸变化回调
         *
         * @param width  宽度（px）
         * @param height 高度（px）
         */
        void onRflSizeChanged(int width, int height);
    }
}
