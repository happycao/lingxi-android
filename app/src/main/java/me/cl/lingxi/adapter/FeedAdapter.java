package me.cl.lingxi.adapter;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.loadmore.LoadMord;
import me.cl.library.loadmore.LoadMoreViewHolder;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.DateUtil;
import me.cl.lingxi.common.util.FeedContentUtil;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.User;

/**
 * Feed Adapter
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Feed> mList;

    private LoadMoreViewHolder mLoadMoreViewHolder;

    private static final int TYPE_FOOTER = -1;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Feed feed, int position);
        void onPhotoClick(ArrayList<String> photos, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FeedAdapter(List<Feed> list) {
        this.mList = list == null ? new ArrayList<Feed>() : list;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View loadView = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
            return mLoadMoreViewHolder = new LoadMoreViewHolder(loadView);
        } else {
            View feedView = View.inflate(parent.getContext(), R.layout.feed_detail_recycle_item, null);
            return new MoodViewHolder(feedView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if (getItemCount() > 5) {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
            } else {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_END);
            }
        } else {
            MoodViewHolder moodViewHolder = (MoodViewHolder) holder;
            moodViewHolder.bindItem(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        mLoadMoreViewHolder.bindItem(status);
    }

    // 设置数据
    public void setData(List<Feed> data) {
        if (data == null) {
            return;
        }
        mList = data;
        mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(List<Feed> data) {
        if (data == null) {
            return;
        }
        mList.addAll(data);
        mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
        notifyDataSetChanged();
    }

    // 更新item
    public void updateItem(Feed feed, int position) {
        mList.set(position, feed);
        notifyItemChanged(position);
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        ImageView mUserImg;
        @BindView(R.id.user_name)
        TextView mUserName;
        @BindView(R.id.feed_time)
        TextView mFeedTime;
        @BindView(R.id.feed_info)
        AppCompatTextView mFeedInfo;
        @BindView(R.id.feed_body)
        LinearLayout mFeedBody;
        @BindView(R.id.feed_view_num)
        TextView mFeedSeeNum;
        @BindView(R.id.feed_comment_num)
        TextView mFeedCommentNum;
        @BindView(R.id.feed_comment_layout)
        LinearLayout mFeedCommentLayout;
        @BindView(R.id.feed_like_icon)
        ImageView mFeedLikeIcon;
        @BindView(R.id.feed_like_num)
        TextView mFeedLikeNum;
        @BindView(R.id.feed_like_layout)
        LinearLayout mFeedLikeLayout;
        @BindView(R.id.feed_action_layout)
        LinearLayout mFeedActionLayout;
        @BindView(R.id.like_people)
        TextView mLikePeople;
        @BindView(R.id.like_window)
        LinearLayout mLikeWindow;
        @BindView(R.id.feed_card)
        LinearLayout mFeedCard;
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private Feed mFeed;
        private int mPosition;

        public MoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
            mFeedSeeNum.setText(String.valueOf(feed.getViewNum()));
            mFeedCommentNum.setText(String.valueOf(feed.getCommentNum()));
            // 是否已经点赞
            if (feed.isLike()) {
                mFeedLikeIcon.setSelected(true);
            } else {
                mFeedLikeIcon.setSelected(false);
            }
            // 点赞列表
            ContentUtil.setLikePeople(mLikePeople, mFeedLikeNum, mLikeWindow, feed.getLikeList());
        }


        @OnClick({R.id.user_img, R.id.feed_comment_layout, R.id.feed_like_layout, R.id.feed_card})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mFeed, mPosition);
        }
    }
}
