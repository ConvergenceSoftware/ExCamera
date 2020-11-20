package com.convergence.excamera.view.config.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.convergence.excamera.R;
import com.convergence.excamera.view.config.ConfigType;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 混合参数布局，可调节自动手动
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class ConfigMixLayout extends ConfigBaseLayout implements CompoundButton.OnCheckedChangeListener,
        ConfigSeekBarLayout.OnConfigSeekBarListener {

    @BindView(R.id.tv_name_layout_config_mix)
    TextView tvNameLayoutConfigMix;
    @BindView(R.id.tv_auto_layout_config_mix)
    TextView tvAutoLayoutConfigMix;
    @BindView(R.id.sw_auto_layout_config_mix)
    Switch swAutoLayoutConfigMix;
    @BindView(R.id.csbl_layout_config_mix)
    ConfigSeekBarLayout csblLayoutConfigMix;
    @BindView(R.id.tv_reset_layout_config_mix)
    TextView tvResetLayoutConfigMix;
    @BindView(R.id.item_param_layout_config_mix)
    LinearLayout itemParamLayoutConfigMix;

    private boolean isAuto = false;
    private boolean isUserAction = true;
    private OnMixConfigListener onMixConfigListener;

    public ConfigMixLayout(Context context) {
        super(context);
    }

    public ConfigMixLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfigMixLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.layout_config_mix;
    }

    @Override
    protected void init() {
        super.init();
        if (configType == ConfigType.Exposure) {
            csblLayoutConfigMix.setType(ConfigSeekBarLayout.Type.PercentQuadratic);
        }
        tvNameLayoutConfigMix.setText(configType.getName());
        swAutoLayoutConfigMix.setOnCheckedChangeListener(this);
        csblLayoutConfigMix.setOnConfigSeekBarListener(this);
    }

    public void resetData(boolean isAuto, int min, int max, int cur) {
        isUserAction = false;
        this.isAuto = isAuto;
        swAutoLayoutConfigMix.setChecked(isAuto);
        isUserAction = true;
        csblLayoutConfigMix.resetData(min, max, cur);
    }

    public void setSeekBarValue(int cur) {
        csblLayoutConfigMix.setCur(cur);
    }

    public void setOnMixConfigListener(OnMixConfigListener onMixConfigListener) {
        this.onMixConfigListener = onMixConfigListener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAuto = isChecked;
        itemParamLayoutConfigMix.setVisibility(!isChecked ? VISIBLE : GONE);
        tvAutoLayoutConfigMix.setText(isChecked ? "Auto" : "Manual");
        if (isUserAction && onMixConfigListener != null) {
            onMixConfigListener.onMixConfigAutoChange(this, isAuto);
        }
    }

    @Override
    public void onConfigProgressChange(int value, int percent) {
        if (onMixConfigListener != null) {
            onMixConfigListener.onMixConfigProgressChange(this, value, percent);
        }
    }

    @OnClick({R.id.tv_reset_layout_config_mix})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_reset_layout_config_mix:
                if (onMixConfigListener != null) {
                    onMixConfigListener.onMixConfigReset(this);
                }
                break;
        }
    }

    public interface OnMixConfigListener {

        /**
         * 混合参数自动手动切换（用户操作）
         *
         * @param view   控件
         * @param isAuto 是否切换至自动
         */
        void onMixConfigAutoChange(ConfigMixLayout view, boolean isAuto);

        /**
         * 混合参数进度条数值变化（用户操作）
         *
         * @param view    控件
         * @param value   参数对应值
         * @param percent 参数百分比
         */
        void onMixConfigProgressChange(ConfigMixLayout view, int value, int percent);

        /**
         * 混合参数重置
         *
         * @param view 控件
         */
        void onMixConfigReset(ConfigMixLayout view);
    }
}
