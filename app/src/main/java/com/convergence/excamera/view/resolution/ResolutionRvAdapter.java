package com.convergence.excamera.view.resolution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.convergence.excamera.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分辨率列表适配器
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class ResolutionRvAdapter extends RecyclerView.Adapter<ResolutionRvAdapter.ViewHolder> {

    private Context context;
    private List<ResolutionOption> optionList;
    private OnItemClickListener onItemClickListener;

    public ResolutionRvAdapter(Context context, List<ResolutionOption> optionList) {
        this.context = context;
        this.optionList = optionList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_resolution, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResolutionOption option = optionList.get(position);
        holder.tvContentRvResolution.setText(option.getDes());
        holder.ivSelectRvResolution.setVisibility(option.isSelect() ? View.VISIBLE : View.GONE);
        holder.itemRvResolution.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(option, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_content_rv_resolution)
        TextView tvContentRvResolution;
        @BindView(R.id.iv_select_rv_resolution)
        ImageView ivSelectRvResolution;
        @BindView(R.id.item_rv_resolution)
        FrameLayout itemRvResolution;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(ResolutionOption option, int position);
    }
}
