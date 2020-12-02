package com.convergence.excamera.view.config.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.convergence.excamera.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 通用参数布局，无法调节自动手动
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class ConfigComLayout extends ConfigBaseLayout implements ConfigSeekBarLayout.OnConfigSeekBarListener {

    @BindView(R.id.tv_name_layout_config_com)
    TextView tvNameLayoutConfigCom;
    @BindView(R.id.csbl_layout_config_com)
    ConfigSeekBarLayout csblLayoutConfigCom;
    @BindView(R.id.tv_reset_layout_config_com)
    TextView tvResetLayoutConfigCom;

    private OnComConfigListener onComConfigListener;

    public ConfigComLayout(Context context) {
        super(context);
    }

    public ConfigComLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfigComLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.layout_config_com;
    }

    @Override
    protected void init() {
        super.init();
        tvNameLayoutConfigCom.setText(configType.getName());
        csblLayoutConfigCom.setOnConfigSeekBarListener(this);
    }

    public void resetData(int min, int max, int cur) {
        csblLayoutConfigCom.resetData(min, max, cur);
    }

    public void setSeekBarValue(int cur) {
        csblLayoutConfigCom.setCur(cur);
    }

    public void setOnComConfigListener(OnComConfigListener onComConfigListener) {
        this.onComConfigListener = onComConfigListener;
    }

    @Override
    public void onConfigProgressChange(int value, int percent) {
        if (onComConfigListener != null) {
            onComConfigListener.onComConfigProgressChange(this, value, percent);
        }
    }

    @OnClick({R.id.tv_reset_layout_config_com})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_reset_layout_config_com:
                if (onComConfigListener != null) {
                    onComConfigListener.onComConfigReset(this);
                }
                break;
            default:
                break;
        }
    }

    public interface OnComConfigListener {

        /**
         * 通用参数进度条数值变化（用户操作）
         *
         * @param view    控件
         * @param value   参数对应值
         * @param percent 参数百分比
         */
        void onComConfigProgressChange(ConfigComLayout view, int value, int percent);

        /**
         * 通用参数重置
         *
         * @param view 控件
         */
        void onComConfigReset(ConfigComLayout view);
    }
}
