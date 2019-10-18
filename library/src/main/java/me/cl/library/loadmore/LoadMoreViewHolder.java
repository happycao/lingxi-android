package me.cl.library.loadmore;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import me.cl.library.R;


/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/04/21
 * desc   : 加载更多ViewHolder
 * version: 1.0
 */
public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

    private ProgressBar mProgress;
    private TextView mLoadPrompt;

    public LoadMoreViewHolder(View itemView) {
        super(itemView);
        initView(itemView);
    }

    private void initView(View itemView) {
        mProgress = itemView.findViewById(R.id.lib_progress);
        mLoadPrompt = itemView.findViewById(R.id.lib_load_prompt);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        itemView.setVisibility(View.GONE);
    }

    public void bindItem(int status) {
        switch (status) {
            case LoadMord.LOAD_MORE:
                mProgress.setVisibility(View.VISIBLE);
                mLoadPrompt.setText(R.string.lib_load_more);
                itemView.setVisibility(View.VISIBLE);
                break;
            case LoadMord.LOAD_PULL_TO:
                mProgress.setVisibility(View.INVISIBLE);
                mLoadPrompt.setText(R.string.lib_load_pull_to);
                itemView.setVisibility(View.VISIBLE);
                break;
            case LoadMord.LOAD_NONE:
                mProgress.setVisibility(View.INVISIBLE);
                mLoadPrompt.setText(R.string.lib_load_none);
                break;
            case LoadMord.LOAD_END:
            default:
                itemView.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
