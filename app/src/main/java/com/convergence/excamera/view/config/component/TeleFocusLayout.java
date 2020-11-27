package com.convergence.excamera.view.config.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.convergence.excamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 望远相机调焦控件
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-10
 * @Organization Convergence Ltd.
 */
public class TeleFocusLayout extends LinearLayout implements View.OnTouchListener {

    @BindView(R.id.tv_focus_back_layout_tele_focus)
    TextView tvFocusBackLayoutTeleFocus;
    @BindView(R.id.iv_focus_back_layout_tele_focus)
    ImageView ivFocusBackLayoutTeleFocus;
    @BindView(R.id.item_focus_back_layout_tele_focus)
    FrameLayout itemFocusBackLayoutTeleFocus;
    @BindView(R.id.iv_focus_front_layout_tele_focus)
    ImageView ivFocusFrontLayoutTeleFocus;
    @BindView(R.id.tv_focus_front_layout_tele_focus)
    TextView tvFocusFrontLayoutTeleFocus;
    @BindView(R.id.item_focus_front_layout_tele_focus)
    FrameLayout itemFocusFrontLayoutTeleFocus;

    private enum PressState {
        None, Back, Front;
    }

    private Context context;
    private OnTeleFocusListener onTeleFocusListener;
    private PressState curPressState = PressState.None;

    public TeleFocusLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TeleFocusLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public TeleFocusLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tele_focus, this, true);
        ButterKnife.bind(this, view);
        itemFocusBackLayoutTeleFocus.setOnTouchListener(this);
        itemFocusFrontLayoutTeleFocus.setOnTouchListener(this);
    }

    /**
     * 设置监听
     */
    public void setOnTeleFocusListener(OnTeleFocusListener onTeleFocusListener) {
        this.onTeleFocusListener = onTeleFocusListener;
    }

    /**
     * 更新按压状态
     */
    private void updatePressState(PressState state) {
        if (curPressState == state) return;
        curPressState = state;
    }

    /**
     * 按钮按下
     *
     * @param isBack 是否向后调焦
     */
    private void focusActionDown(boolean isBack) {
        if (curPressState != PressState.None) return;
        if (isBack) {
            itemFocusBackLayoutTeleFocus.setBackgroundColor(getColor(R.color.colorPrimary));
            tvFocusBackLayoutTeleFocus.setTextColor(getColor(R.color.colorWhite));
            ivFocusBackLayoutTeleFocus.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_press));
        } else {
            itemFocusFrontLayoutTeleFocus.setBackgroundColor(getColor(R.color.colorPrimary));
            tvFocusFrontLayoutTeleFocus.setTextColor(getColor(R.color.colorWhite));
            ivFocusFrontLayoutTeleFocus.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_press));
        }
        updatePressState(isBack ? PressState.Back : PressState.Front);
        if (onTeleFocusListener != null) {
            onTeleFocusListener.onTeleFocusDown(isBack);
        }
    }

    /**
     * 按钮抬起或移出控件外
     *
     * @param isBack 是否向后调焦
     */
    private void focusActionUp(boolean isBack) {
        switch (curPressState) {
            case None:
                return;
            case Back:
                if (!isBack) return;
                break;
            case Front:
                if (isBack) return;
                break;
        }
        if (isBack) {
            itemFocusBackLayoutTeleFocus.setBackgroundColor(getColor(R.color.colorTransparent));
            tvFocusBackLayoutTeleFocus.setTextColor(getColor(R.color.colorPrimary));
            ivFocusBackLayoutTeleFocus.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_normal));
        } else {
            itemFocusFrontLayoutTeleFocus.setBackgroundColor(getColor(R.color.colorTransparent));
            tvFocusFrontLayoutTeleFocus.setTextColor(getColor(R.color.colorPrimary));
            ivFocusFrontLayoutTeleFocus.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_normal));
        }
        updatePressState(PressState.None);
        if (onTeleFocusListener != null) {
            onTeleFocusListener.onTeleFocusUp(isBack);
        }
    }

    private int getColor(@ColorRes int colorId) {
        return ResourcesCompat.getColor(getResources(), colorId, null);
    }

    private Drawable getDrawable(@DrawableRes int drawableId) {
        return ResourcesCompat.getDrawable(getResources(), drawableId, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.item_focus_back_layout_tele_focus:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        focusActionDown(true);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        focusActionUp(true);
                        return true;
                    default:
                        return false;
                }
            case R.id.item_focus_front_layout_tele_focus:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        focusActionDown(false);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        focusActionUp(false);
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    /**
     * 望远相机调焦按钮监听
     */
    public interface OnTeleFocusListener {

        /**
         * 调焦按钮按下
         *
         * @param isBack 是否向后调焦
         */
        void onTeleFocusDown(boolean isBack);

        /**
         * 调焦按钮抬起
         *
         * @param isBack 是否向后调焦
         */
        void onTeleFocusUp(boolean isBack);
    }
}
