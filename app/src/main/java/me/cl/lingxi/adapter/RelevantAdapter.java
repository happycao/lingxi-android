package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.RelevantRecycleItemBinding;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.entity.Reply;
import me.cl.lingxi.entity.User;

/**
 * Reply Adapter
 */
public class RelevantAdapter extends RecyclerView.Adapter<RelevantAdapter.RelevantViewHolder> {

    private final List<Relevant> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Relevant relevant);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public RelevantAdapter(List<Relevant> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public RelevantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelevantRecycleItemBinding binding = RelevantRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RelevantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelevantViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Relevant> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class RelevantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mUserImg;
        private final TextView mUserName;
        private final TextView mRelevantTime;
        private final AppCompatTextView mRelevantInfo;
        private final AppCompatTextView mFeedInfo;
        private final LinearLayout mFeedBody;

        private Relevant mRelevant;

        public RelevantViewHolder(RelevantRecycleItemBinding binding) {
            super(binding.getRoot());

            mUserImg = binding.userImg;
            mUserName = binding.userName;
            mRelevantTime = binding.relevantTime;
            mRelevantInfo = binding.relevantInfo;
            mFeedInfo = binding.feedInfo;
            mFeedBody = binding.feedBody;

            mUserImg.setOnClickListener(this);
            mFeedBody.setOnClickListener(this);
        }

        public void bindItem(Relevant relevant) {
            mRelevant = relevant;
            Comment comment = relevant.getComment();
            User user = comment.getUser();
            Integer replyNum = relevant.getReplyNum();
            StringBuilder relevantInfo = new StringBuilder();
            String timeStr = "";
            if (replyNum > 0) {
                List<Reply> replyList = relevant.getReplyList();
                for (int i = 0, size = replyList.size(); i < size; i++) {
                    Reply reply = replyList.get(i);
                    if (i == 0) {
                        user = reply.getUser();
                        timeStr = reply.getCreateTime();
                        relevantInfo.append(reply.getCommentInfo());
                    } else {
                        relevantInfo.append("//{@").append(reply.getUser().getUsername()).append("}:")
                                .append(reply.getCommentInfo());
                    }

                }
                relevantInfo.append("//{@").append(comment.getUser().getUsername()).append("}:")
                        .append(comment.getCommentInfo());
            } else {
                timeStr = comment.getCreateTime();
                relevantInfo.append(comment.getCommentInfo());
            }

            ContentUtil.loadUserAvatar(mUserImg, user.getAvatar());

            mUserName.setText(user.getUsername());
            mRelevantTime.setText(timeStr);

            mRelevantInfo.setText(Utils.colorFormat(relevantInfo.toString()));

            Feed feed = relevant.getFeed();
            String feedInfo = "{" + feed.getUser().getUsername() + "}：" + feed.getFeedInfo();
            mFeedInfo.setText(Utils.colorFormat(feedInfo));
        }

        @Override
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mRelevant);
        }
    }
}
