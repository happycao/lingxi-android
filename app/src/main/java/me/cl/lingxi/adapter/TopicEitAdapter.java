package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.databinding.TopicEitRecycleItemBinding;
import me.cl.lingxi.entity.Topic;
import me.cl.lingxi.module.feed.TopicEitActivity;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2020/05/14
 * desc   : 话题
 * version: 1.0
 */
public class TopicEitAdapter extends RecyclerView.Adapter<TopicEitAdapter.ViewHolder> {

    private List<Topic> mList;
    private TopicEitActivity.Type mType;

    private OnItemListener mOnItemListener;

    public void setData(List<Topic> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void addData(List<Topic> data) {
        if (!data.isEmpty()) {
            mList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void updateData(Topic topic, int position) {
        mList.set(position, topic);
        notifyItemChanged(position);
    }

    public List<Topic> getData() {
        return mList;
    }

    public boolean isSelected() {
        for (Topic topic : mList) {
            if (topic.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public interface OnItemListener {
        void onItemClick(View view, Topic future);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public TopicEitAdapter(List<Topic> list, TopicEitActivity.Type type) {
        this.mList = list;
        this.mType = type;
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

        private final ImageView mIvImg;
        private final TextView mTvInfo;
        private final ImageView mIvOk;

        private Topic mTopic;
        private int mPosition;

        public ViewHolder(TopicEitRecycleItemBinding binding) {
            super(binding.getRoot());

            mIvImg = binding.ivImg;
            mTvInfo = binding.tvInfo;
            mIvOk = binding.ivOk;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTopic.setSelected(!mTopic.isSelected());
                    updateData(mTopic, mPosition);
                    if (mOnItemListener != null) mOnItemListener.onItemClick(v, mTopic);
                }
            });
        }

        public void bindItem(Topic topic, int position) {
            mTopic = topic;
            mPosition = position;
            mTvInfo.setText(topic.getTopicName());
            if (mType == TopicEitActivity.Type.EIT) {
                mIvImg.setVisibility(View.VISIBLE);
                ContentUtil.loadUserAvatar(mIvImg, topic.getAvatar());
            } else {
                mIvImg.setVisibility(View.GONE);
            }
            if (topic.isSelected()) {
                mIvOk.setVisibility(View.VISIBLE);
            } else {
                mIvOk.setVisibility(View.GONE);
            }
        }
    }
}
