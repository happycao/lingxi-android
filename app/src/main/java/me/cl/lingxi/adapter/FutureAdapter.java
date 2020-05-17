package me.cl.lingxi.adapter;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.DateUtil;
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
        View view = View.inflate(parent.getContext(), R.layout.future_recycle_item, null);
        return new ViewHolder(view);
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

        @BindView(R.id.tv_future_info)
        TextView mFutureInfo;
        @BindView(R.id.tv_show_time)
        TextView mShowTime;

        private Future mFuture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
