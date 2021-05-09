package me.cl.lingxi.adapter;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.util.DateUtil;
import me.cl.lingxi.databinding.FutureRecycleItemBinding;
import me.cl.lingxi.entity.Future;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2020/05/14
 * desc   :
 * version: 1.0
 */
public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.ViewHolder> {

    private List<Future> mList;

    private OnItemListener mOnItemListener;

    public void setData(List<Future> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void addData(List<Future> data) {
        if (!data.isEmpty()) {
            mList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public interface OnItemListener {
        void onItemClick(View view, Future future);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FutureAdapter(List<Future> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FutureRecycleItemBinding binding = FutureRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mFutureInfo;
        private final TextView mShowTime;

        private Future mFuture;

        public ViewHolder(FutureRecycleItemBinding binding) {
            super(binding.getRoot());

            mFutureInfo = binding.tvFutureInfo;
            mShowTime = binding.tvShowTime;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) mOnItemListener.onItemClick(v, mFuture);
                }
            });
        }

        public void bindItem(Future future) {
            mFuture = future;
            @SuppressLint("DefaultLocale") String futureInfo = String.format("致%d天后的自己：<br/><span style='text-indent:2em;'/>", future.getDays());
            futureInfo = futureInfo + future.getFutureInfo();
            mFutureInfo.setText(Html.fromHtml(futureInfo));
            mShowTime.setText(DateUtil.showTime(future.getShowTime()));
        }
    }
}
