package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.FeedReplyRecycleItemBinding;
import me.cl.lingxi.entity.Reply;

/**
 * Reply Adapter
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private final List<Reply> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Reply reply);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public ReplyAdapter(List<Reply> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FeedReplyRecycleItemBinding binding = FeedReplyRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReplyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatTextView mReplyInfo;
        private Reply mReply;

        public ReplyViewHolder(FeedReplyRecycleItemBinding binding) {
            super(binding.getRoot());

            mReplyInfo = binding.replyInfo;

            mReplyInfo.setOnClickListener(this);
        }

        public void bindItem(Reply reply) {
            mReply = reply;
            String replyStr = "{" + reply.getUser().getUsername() + "}回复{" + reply.getToUser().getUsername() + "}：" + reply.getCommentInfo();
            mReplyInfo.setText(Utils.colorFormat(replyStr));
        }

        @Override
        public void onClick(View view){
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mReply);
        }
    }
}
