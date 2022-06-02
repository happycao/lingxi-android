package me.cl.lingxi.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.databinding.TopicEitRecycleItemBinding;
import me.cl.lingxi.entity.User;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2020/05/14
 * desc   : 用户信息
 * version: 1.0
 */
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {

    private List<User> mList;

    private OnItemListener mOnItemListener;

    public UserInfoAdapter(List<User> list) {
        this.mList = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<User> data) {
        mList = data;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<User> data) {
        if (!data.isEmpty()) {
            mList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public List<User> getData() {
        return mList;
    }

    public interface OnItemListener {
        void onItemClick(View view, User item);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TopicEitRecycleItemBinding binding = TopicEitRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TopicEitRecycleItemBinding mBinding;

        private User mItem;

        public ViewHolder(TopicEitRecycleItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindItem(User item, int position) {
            mItem = item;
            mBinding.ivOk.setVisibility(View.GONE);
            mBinding.ivImg.setVisibility(View.VISIBLE);
            mBinding.tvInfo.setText(item.getUsername());
            ContentUtil.loadUserAvatar(mBinding.ivImg, item.getAvatar());

            itemView.setOnClickListener(v -> {
                if (mOnItemListener != null) mOnItemListener.onItemClick(v, mItem);
            });
        }
    }
}
