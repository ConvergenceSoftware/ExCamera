package com.convergence.excamera.view.config.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.convergence.excamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Planet 运动控制布局
 *
 * @Author LiLei
 * @CreateDate 2021-12-06
 * @Organization Convergence Ltd.
 */
public class PlanetControlLayout extends LinearLayout implements View.OnTouchListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "PlanetControlLayout";

    @BindView(R.id.iv_direction_up_layout_planet_control)
    ImageView ivDirectionUpLayoutPlanet;
    @BindView(R.id.iv_direction_down_layout_planet_control)
    ImageView ivDirectionDownLayoutPlanet;
    @BindView(R.id.iv_direction_left_layout_planet_control)
    ImageView ivDirectionLeftLayoutPlanet;
    @BindView(R.id.iv_direction_right_layout_planet_control)
    ImageView ivDirectionRightLayoutPlanet;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton:
                curSpeedStage = SpeedStage.Speed1;
                break;
            case R.id.radioButton2:
                curSpeedStage = SpeedStage.Speed2;
                break;
        }
    }

    public enum SpeedStage {
        Speed1,
        Speed2,
        Speed3,
        Speed4
    }

    private enum PitchPressState {
        /*
        通常状态
         */
        None,
        /*
        向上按钮按下
         */
        Up,
        /*
        向下按钮按下
         */
        Down
    }

    private enum RotatePressState {
        /*
        通常状态
         */
        None,
        /*
        向左按钮按下
         */
        Left,
        /*
        向右按钮按下
         */
        Right
    }


    private Context context;
    private OnPlanetListener onPlanetListener;
    private PitchPressState curPressState = PitchPressState.None;

    private RotatePressState curRotatePressState = RotatePressState.None;

    private SpeedStage curSpeedStage = SpeedStage.Speed1;

    public PlanetControlLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PlanetControlLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public PlanetControlLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_planet_control, this, true);
        ButterKnife.bind(this, view);
        ivDirectionUpLayoutPlanet.setOnTouchListener(this);
        ivDirectionDownLayoutPlanet.setOnTouchListener(this);
        ivDirectionLeftLayoutPlanet.setOnTouchListener(this);
        ivDirectionRightLayoutPlanet.setOnTouchListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(R.id.radioButton);
    }

    /**
     * 设置监听
     */
    public void setOnPlanetListener(OnPlanetListener listener) {
        this.onPlanetListener = listener;
    }


    /**
     * 更新俯仰按钮按压状态
     */
    private void updatePitchPressState(PitchPressState state) {
        if (curPressState == state) {
            return;
        }
        curPressState = state;
    }

    /**
     * 俯仰按钮按下
     *
     * @param isDown 是否下俯
     */
    private void pitchActionDown(boolean isDown) {
        if (curPressState != PitchPressState.None) {
            return;
        }
        if (isDown) {
            ivDirectionDownLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_press));
        } else {
            ivDirectionUpLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_press));
        }
        updatePitchPressState(isDown ? PitchPressState.Down : PitchPressState.Up);
        if (onPlanetListener != null) {
            onPlanetListener.onPitchActionDown(isDown, curSpeedStage);
        }
    }

    /**
     * 俯仰按钮抬起或移出控件外
     *
     * @param isDown 是否向下运动
     */
    private void pitchActionUp(boolean isDown) {
        switch (curPressState) {
            case None:
            default:
                return;
            case Down:
                if (!isDown) {
                    return;
                }
                break;
            case Up:
                if (isDown) {
                    return;
                }
                break;
        }
        if (isDown) {
            ivDirectionDownLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_normal));
        } else {
            ivDirectionUpLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_normal));
        }
        updatePitchPressState(PitchPressState.None);
        if (onPlanetListener != null) {
            onPlanetListener.onPitchActionUp();
        }
    }


    /**
     * 更新左右旋转按钮按压状态
     */
    private void updateRotatePressState(RotatePressState state) {
        if (curRotatePressState == state) {
            return;
        }
        curRotatePressState = state;
    }

    /**
     * 左右旋转按钮按下
     *
     * @param isLeft 是否向左
     */
    private void rotateActionDown(boolean isLeft) {
        if (curRotatePressState != RotatePressState.None) {
            return;
        }
        if (isLeft) {
            ivDirectionLeftLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_press));
        } else {
            ivDirectionRightLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_press));
        }
        updateRotatePressState(isLeft ? RotatePressState.Left : RotatePressState.Right);
        if (onPlanetListener != null) {
            onPlanetListener.onRotateActionDown(isLeft, curSpeedStage);
        }
    }

    /**
     * 左右旋转按钮抬起或移出控件外
     *
     * @param isLeft 是否向左
     */
    private void rotateActionUp(boolean isLeft) {
        switch (curRotatePressState) {
            case None:
            default:
                return;
            case Left:
                if (!isLeft) {
                    return;
                }
                break;
            case Right:
                if (isLeft) {
                    return;
                }
                break;
        }
        if (isLeft) {
            ivDirectionLeftLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_normal));
        } else {
            ivDirectionRightLayoutPlanet.setBackground(getDrawable(R.drawable.ic_bg_btn_tele_focus_normal));
        }
        updateRotatePressState(RotatePressState.None);
        if (onPlanetListener != null) {
            onPlanetListener.onRotateActionUp();
        }
    }

    private int getColor(@ColorRes int colorId) {
        return ResourcesCompat.getColor(getResources(), colorId, null);
    }

    private Drawable getDrawable(@DrawableRes int drawableId) {
        return ResourcesCompat.getDrawable(getResources(), drawableId, null);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.iv_direction_down_layout_planet_control:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pitchActionDown(true);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        pitchActionUp(true);
                        return true;
                    default:
                        return false;
                }
            case R.id.iv_direction_up_layout_planet_control:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pitchActionDown(false);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        pitchActionUp(false);
                        return true;
                    default:
                        return false;
                }
            case R.id.iv_direction_left_layout_planet_control:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rotateActionDown(true);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        rotateActionUp(true);
                        return true;
                    default:
                        return false;
                }
            case R.id.iv_direction_right_layout_planet_control:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rotateActionDown(false);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        rotateActionUp(false);
                        return true;
                    default:
                        return false;
                }

            default:
                return false;
        }
    }

    @OnClick({R.id.tv_reset_layout_planet_control})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_reset_layout_planet_control:
                Log.e(TAG, "onViewClicked tv_reset_layout_Planet_control: ");
                if (onPlanetListener != null) {
                    onPlanetListener.onPlanetReset();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 云台十字按键按钮监听
     */
    public interface OnPlanetListener {


        /**
         * 俯仰按钮按下
         *
         * @param isClockWise 是否顺时针旋转
         */
        void onPitchActionDown(boolean isClockWise, SpeedStage speedStage);

        /**
         * 俯仰按钮抬起
         */
        void onPitchActionUp();

        /**
         * 左右旋转按钮按下
         *
         * @param isLeft 是否向左旋转
         */
        void onRotateActionDown(boolean isLeft, SpeedStage speedStage);

        /**
         * 上下旋转按钮抬起
         */
        void onRotateActionUp();

        void onPlanetReset();
    }

}
