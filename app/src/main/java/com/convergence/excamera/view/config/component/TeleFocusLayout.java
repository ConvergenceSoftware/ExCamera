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
import butterknife.OnClick;

/**
 * 望远相机调焦控件
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-10
 * @Organization Convergence Ltd.
 */
public class TeleFocusLayout extends LinearLayout implements View.OnTouchListener {

    @BindView(R.id.tv_auto_focus_layout_tele_focus)
    TextView tvAutoFocusLayoutTeleFocus;
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

    private boolean isAFClickable = true;

    private enum PressState {
        /*
        通常状态
         */
        None,
        /*
        向后调焦按钮按下
         */
        Back,
        /*
        向前调焦按钮按下
         */
        Front,
        /*
        自动对焦中
         */
        AutoFocus
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
     * 更新自动对焦状态
     *
     * @param isAFRunning 当前是否正在自动对焦
     */
    public void updateAFState(boolean isAFRunning) {
        if (isAFRunning){
            updatePressState(PressState.AutoFocus);
            tvAutoFocusLayoutTeleFocus.setTextColor(getColor(R.color.colorWhite));
            tvAutoFocusLayoutTeleFocus.setBackgroundColor(getColor(R.color.colorPrimary));
        }else {
            updatePressState(PressState.None);
            tvAutoFocusLayoutTeleFocus.setTextColor(getColor(R.color.colorPrimary));
            tvAutoFocusLayoutTeleFocus.setBackgroundColor(getColor(R.color.colorWhite));
        }
    }

    /**
     * 更新按压状态
     */
    private void updatePressState(PressState state) {
        if (curPressState == state) {
            return;
        }
        curPressState = state;
    }

    /**
     * 按钮按下
     *
     * @param isBack 是否向后调焦
     */
    private void focusActionDown(boolean isBack) {
        if (curPressState != PressState.None) {
            return;
        }
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
            default:
                return;
            case Back:
                if (!isBack) {
                    return;
                }
                break;
            case Front:
                if (isBack) {
                    return;
                }
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

    @OnClick({R.id.tv_auto_focus_layout_tele_focus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_auto_focus_layout_tele_focus:
                if (isAFClickable && curPressState != PressState.Back && curPressState != PressState.Front) {
                    isAFClickable = false;
                    postDelayed(() -> isAFClickable = true, 1000);
                    if (onTeleFocusListener != null) {
                        onTeleFocusListener.onTeleAFClick();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 望远相机调焦按钮监听
     */
    public interface OnTeleFocusListener {

        /**
         * 自动调焦按钮点击
         */
        void onTeleAFClick();

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
