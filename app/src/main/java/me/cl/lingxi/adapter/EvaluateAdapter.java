package me.cl.lingxi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.loadmore.LoadMord;
import me.cl.library.loadmore.LoadMoreViewHolder;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Reply;
import me.cl.lingxi.entity.User;

/**
 * Evaluate Adapter
 */
public class EvaluateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Comment> mList;

    private static final int TYPE_FOOTER = -1;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Comment comment);

        void onItemChildClick(View view, String eid, Reply reply);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public EvaluateAdapter(Context context, List<Comment> list) {
        this.mContext = context;
        this.mList = list;
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
            View view = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
            return new LoadMoreViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.feed_evaluate_recycle_item, null);
            return new EvaluateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            loadMoreViewHolder.bindItem(LoadMord.LOAD_END);
        } else {
            EvaluateViewHolder evaluateViewHolder = (EvaluateViewHolder) holder;
            evaluateViewHolder.bindItem(mContext, mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        notifyDataSetChanged();
    }

    public void setDate(List<Comment> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void updateData(List<Comment> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class EvaluateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.evaluate_body)
        RelativeLayout mEvaluateBody;
        @BindView(R.id.user_img)
        ImageView mUserImg;
        @BindView(R.id.user_name)
        TextView mUserName;
        @BindView(R.id.evaluate_time)
        TextView mEvaluateTime;
        @BindView(R.id.evaluate_info)
        AppCompatTextView mEvaluateInfo;
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;
        private Comment mComment;

        public EvaluateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), RecyclerView.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
        }

        public void bindItem(Context context, Comment comment) {
            mComment = comment;
            User user = comment.getUser();

            ContentUtil.loadUserAvatar(mUserImg, user.getAvatar());

            mUserName.setText(user.getUsername());
            mEvaluateTime.setText(comment.getCreateTime());
            mEvaluateInfo.setText(comment.getCommentInfo());
            ReplyAdapter adapter = new ReplyAdapter(context, comment.getReplyList());
            mRecyclerView.setAdapter(adapter);

            final String eid = comment.getId();
            adapter.setOnItemListener(new ReplyAdapter.OnItemListener() {
                @Override
                public void onItemClick(View view, Reply reply) {
                    if (mOnItemListener != null) mOnItemListener.onItemChildClick(view, eid, reply);
                }
            });
        }

        @OnClick({R.id.user_img, R.id.evaluate_body})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mComment);
        }
    }
}
