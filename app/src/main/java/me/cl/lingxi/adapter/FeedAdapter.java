package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.DateUtil;
import me.cl.lingxi.common.util.FeedContentUtil;
import me.cl.lingxi.databinding.FeedActionIncludeBinding;
import me.cl.lingxi.databinding.FeedDetailRecycleItemBinding;
import me.cl.lingxi.databinding.FeedInfoIncludeBinding;
import me.cl.lingxi.databinding.FeedLikeIncludeBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.User;

/**
 * Feed Adapter
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MoodViewHolder> {

    private List<Feed> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Feed feed, int position);
        void onPhotoClick(ArrayList<String> photos, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FeedAdapter(List<Feed> list) {
        this.mList = list == null ? new ArrayList<>() : list;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FeedDetailRecycleItemBinding binding = FeedDetailRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        holder.bindItem(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // 设置数据
    public void setData(List<Feed> data) {
        if (data == null) {
            return;
        }
        mList = data;
        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(List<Feed> data) {
        if (data == null) {
            return;
        }
        mList.addAll(data);
        notifyDataSetChanged();
    }

    // 更新item
    public void updateItem(Feed feed, int position) {
        mList.set(position, feed);
        notifyItemChanged(position);
    }

    class MoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mUserImg;
        private final TextView mUserName;
        private final TextView mFeedTime;
        private final AppCompatTextView mFeedInfo;
        private final LinearLayout mFeedBody;
        private final TextView mFeedViewNum;
        private final TextView mFeedCommentNum;
        private final LinearLayout mFeedCommentLayout;
        private final ImageView mFeedLikeIcon;
        private final TextView mFeedLikeNum;
        private final LinearLayout mFeedLikeLayout;
        private final LinearLayout mFeedActionLayout;
        private final TextView mLikePeople;
        private final LinearLayout mLikeWindow;
        private final LinearLayout mFeedCard;
        private final RecyclerView mRecyclerView;

        private Feed mFeed;
        private int mPosition;

        public MoodViewHolder(FeedDetailRecycleItemBinding binding) {
            super(binding.getRoot());
            FeedInfoIncludeBinding infoBinding = binding.includeFeedInfo;
            FeedActionIncludeBinding actionBinding = binding.includeFeedAction;
            FeedLikeIncludeBinding likeBinding = binding.includeFeedLike;

            mUserImg = infoBinding.userImg;
            mUserName = infoBinding.userName;
            mFeedTime = infoBinding.feedTime;
            mFeedInfo = infoBinding.feedInfo;
            mFeedBody = infoBinding.feedBody;
            mFeedViewNum = actionBinding.feedViewNum;
            mFeedCommentNum = actionBinding.feedCommentNum;
            mFeedCommentLayout = actionBinding.feedCommentLayout;
            mFeedLikeIcon = actionBinding.feedLikeIcon;
            mFeedLikeNum = actionBinding.feedLikeNum;
            mFeedLikeLayout = actionBinding.feedLikeLayout;
            mFeedActionLayout = actionBinding.feedActionLayout;
            mLikePeople = likeBinding.likePeople;
            mLikeWindow = likeBinding.likeWindow;
            mFeedCard = binding.feedCard;
            mRecyclerView = binding.recyclerView;

            mUserImg.setOnClickListener(this);
            mFeedCommentLayout.setOnClickListener(this);
            mFeedLikeLayout.setOnClickListener(this);
            mUserImg.setOnClickListener(this);
        }

        public void bindItem(Feed feed, int position) {
            mFeed = feed;
            mPosition = position;
            User user = feed.getUser();
            user = user == null ? new User() : user;
            // 动态详情
            ContentUtil.loadUserAvatar(mUserImg, user.getAvatar());

            mUserName.setText(user.getUsername());
            mFeedTime.setText(DateUtil.showTime(feed.getCreateTime()));
            mFeedInfo.setText(FeedContentUtil.getFeedText(feed.getFeedInfo(), mFeedInfo));
            // 图片
            final List<String> photos = feed.getPhotoList();
            if (photos != null && photos.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                int size = photos.size();
                // 如果只有一张或四张图，设置两列，否则三列
                int column = (size == 1 || size == 4) ? 2 : 3;
                mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), column));
                // 设置动态图片适配器
                ContentUtil.setFeedPhotoAdapter(mRecyclerView, photos, mOnItemListener);
            } else {
                mRecyclerView.setVisibility(View.GONE);
            }
            // 查看评论点赞数
            mFeedViewNum.setText(String.valueOf(feed.getViewNum()));
            mFeedCommentNum.setText(String.valueOf(feed.getCommentNum()));
            // 是否已经点赞
            mFeedLikeIcon.setSelected(feed.isLike());
            // 点赞列表
            ContentUtil.setLikePeople(mLikePeople, mFeedLikeNum, mLikeWindow, feed.getLikeList());
        }

        @Override
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mFeed, mPosition);
        }
    }
}
