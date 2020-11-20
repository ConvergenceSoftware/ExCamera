package com.convergence.excamera.view.resolution;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.convergence.excamera.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 分辨率选择对话框
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class ResolutionDialog extends Dialog implements ResolutionRvAdapter.OnItemClickListener {

    @BindView(R.id.tv_title_dialog_resolution)
    TextView tvTitleDialogResolution;
    @BindView(R.id.rv_list_dialog_resolution)
    RecyclerView rvListDialogResolution;
    @BindView(R.id.tv_cancel_dialog_resolution)
    TextView tvCancelDialogResolution;
    @BindView(R.id.tv_done_dialog_resolution)
    TextView tvDoneDialogResolution;

    private Context context;
    private List<ResolutionOption> optionList;
    private Size curSize;
    private ResolutionRvAdapter rvAdapter;
    private OnClickListener onClickListener;

    public ResolutionDialog(@NonNull Context context, List<ResolutionOption> data, Size curSize, OnClickListener onClickListener) {
        super(context, R.style.ActionSheetDialogTheme);
        this.context = context;
        this.curSize = curSize;
        this.onClickListener = onClickListener;
        optionList = new ArrayList<>();
        optionList.addAll(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resolution);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        Window dialogWindow = getWindow();
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams attr = dialogWindow.getAttributes();
        if (attr != null) {
            attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attr.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(attr);
        }

        rvAdapter = new ResolutionRvAdapter(context, optionList);
        rvAdapter.setOnItemClickListener(this);
        rvListDialogResolution.setLayoutManager(new LinearLayoutManager(context));
        rvListDialogResolution.setAdapter(rvAdapter);
    }

    private Size getSelectedResolution() {
        for (int i = 0; i < optionList.size(); i++) {
            ResolutionOption option = optionList.get(i);
            if (option.isSelect()) {
                return new Size(option.getWidth(), option.getHeight());
            }
        }
        return null;
    }

    private boolean isSizeEqual(Size size1, Size size2) {
        if (size1 == null || size2 == null) return false;
        return size1.getWidth() == size2.getWidth() && size1.getHeight() == size2.getHeight();
    }

    @OnClick({R.id.tv_cancel_dialog_resolution, R.id.tv_done_dialog_resolution})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel_dialog_resolution:
                break;
            case R.id.tv_done_dialog_resolution:
                Size resultSize = getSelectedResolution();
                if (resultSize != null && !isSizeEqual(curSize, resultSize) && onClickListener != null) {
                    onClickListener.onResolutionUpdate(resultSize);
                }
                break;
        }
        dismiss();
    }

    @Override
    public void onItemClick(ResolutionOption option, int position) {
        for (int i = 0; i < optionList.size(); i++) {
            optionList.get(i).setSelect(i == position);
        }
        rvAdapter.notifyDataSetChanged();
    }

    public interface OnClickListener {

        void onResolutionUpdate(Size resultSize);
    }
}
