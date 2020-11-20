package com.convergence.excamera.view.config.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.convergence.excamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 镜像翻转布局
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class MirrorFlipLayout extends LinearLayout implements Switch.OnCheckedChangeListener {

    @BindView(R.id.sw_flip_horizontal_layout_mirror_flip)
    Switch swFlipHorizontalLayoutMirrorFlip;
    @BindView(R.id.sw_flip_vertical_layout_mirror_flip)
    Switch swFlipVerticalLayoutMirrorFlip;

    private Context context;
    private boolean isUserAction = true;
    private OnMirrorFlipListener onMirrorFlipListener;

    public MirrorFlipLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MirrorFlipLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public MirrorFlipLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mirror_flip, this, true);
        ButterKnife.bind(this, view);
        swFlipHorizontalLayoutMirrorFlip.setOnCheckedChangeListener(this);
        swFlipVerticalLayoutMirrorFlip.setOnCheckedChangeListener(this);
    }

    public void setOnMirrorFlipListener(OnMirrorFlipListener onMirrorFlipListener) {
        this.onMirrorFlipListener = onMirrorFlipListener;
    }

    public void initSwitch(boolean isFlipHorizontal, boolean isFlipVertical) {
        isUserAction = false;
        swFlipHorizontalLayoutMirrorFlip.setChecked(isFlipHorizontal);
        swFlipVerticalLayoutMirrorFlip.setChecked(isFlipVertical);
        isUserAction = true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isUserAction && onMirrorFlipListener != null) {
            onMirrorFlipListener.onFlipCheckedChange(swFlipHorizontalLayoutMirrorFlip.isChecked(),
                    swFlipVerticalLayoutMirrorFlip.isChecked());
        }
    }

    public interface OnMirrorFlipListener {

        /**
         * 镜像翻转切换
         *
         * @param isFlipHorizontal 是否水平翻转
         * @param isFlipVertical   是否垂直翻转
         */
        void onFlipCheckedChange(boolean isFlipHorizontal, boolean isFlipVertical);
    }
}
