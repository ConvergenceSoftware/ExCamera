package com.convergence.excamera.view.config.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;

import com.convergence.excamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 参数布局中的滑动条模块
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class ConfigSeekBarLayout extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.sb_layout_config_seek_bar)
    SeekBar sbLayoutConfigSeekBar;
    @BindView(R.id.tv_min_layout_config_seek_bar)
    TextView tvMinLayoutConfigSeekBar;
    @BindView(R.id.tv_cur_layout_config_seek_bar)
    TextView tvCurLayoutConfigSeekBar;
    @BindView(R.id.tv_max_layout_config_seek_bar)
    TextView tvMaxLayoutConfigSeekBar;

    public enum Type {
        Percent, PercentQuadratic
    }

    private Context context;
    private Type type = Type.Percent;
    private int min = 0;
    private int max = 100;
    private int cur = 0;

    private OnConfigSeekBarListener onConfigSeekBarListener;

    public ConfigSeekBarLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ConfigSeekBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    public ConfigSeekBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_config_seek_bar, this, true);
        ButterKnife.bind(this, view);
        sbLayoutConfigSeekBar.setOnSeekBarChangeListener(this);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void resetData(int min, int max, int cur) {
        this.min = min;
        this.max = max;
        this.cur = cur;
        tvMinLayoutConfigSeekBar.setText(min + "");
        tvMaxLayoutConfigSeekBar.setText(max + "");
        tvCurLayoutConfigSeekBar.setText(cur + "");
        switch (type) {
            case Percent:
                sbLayoutConfigSeekBar.setProgress(getPercentByValue(cur));
                break;
            case PercentQuadratic:
                sbLayoutConfigSeekBar.setProgress(getPercentByValueQuadratic(cur));
                break;
        }
    }

    public void setCur(int cur) {
        this.cur = cur;
        tvCurLayoutConfigSeekBar.setText(cur + "");
        switch (type) {
            case Percent:
                sbLayoutConfigSeekBar.setProgress(getPercentByValue(cur));
                break;
            case PercentQuadratic:
                sbLayoutConfigSeekBar.setProgress(getPercentByValueQuadratic(cur));
                break;
        }
    }

    public void setOnConfigSeekBarListener(OnConfigSeekBarListener onConfigSeekBarListener) {
        this.onConfigSeekBarListener = onConfigSeekBarListener;
    }

    private int getValueByPercent(int percent) {
        float value = (float) (max - min) * percent / 100 + min;
        return (int) MathUtils.clamp(value, min, max);
    }

    private int getPercentByValue(int value) {
        float percent = (float) (value - min) / (max - min) * 100;
        return (int) MathUtils.clamp(percent, 0, 100);
    }

    private int getValueByPercentQuadratic(int percent) {
        float b = (float) min;
        float k = ((float) max - b) / 10000.f;
        float value = k * percent * percent + b;
        return (int) MathUtils.clamp(value, min, max);
    }

    private int getPercentByValueQuadratic(int value) {
        float b = (float) min;
        float k = ((float) max - b) / 10000.f;
        float percent = (float) (Math.sqrt((float) value + b) / k);
        return (int) MathUtils.clamp(percent, 0, 100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            switch (type) {
                case Percent:
                    cur = getValueByPercent(progress);
                    break;
                case PercentQuadratic:
                    cur = getValueByPercentQuadratic(progress);
                    break;
            }
            tvCurLayoutConfigSeekBar.setText(cur + "");
            if (onConfigSeekBarListener != null) {
                onConfigSeekBarListener.onConfigProgressChange(cur, progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnConfigSeekBarListener {

        /**
         * 进度条数值变化
         *
         * @param value   参数对应值
         * @param percent 参数百分比
         */
        void onConfigProgressChange(int value, int percent);
    }
}
