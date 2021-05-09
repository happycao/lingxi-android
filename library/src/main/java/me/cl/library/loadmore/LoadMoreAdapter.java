package me.cl.library.loadmore;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.cl.library.R;

/**
 * @author : happyc
 * time    : 2021/05/09
 * desc    :
 * version : 1.0
 */
public class LoadMoreAdapter extends RecyclerView.Adapter<LoadMoreViewHolder> {

    private int status = LoadMord.LOAD_END;

    @NonNull
    @Override
    public LoadMoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
        return new LoadMoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoadMoreViewHolder holder, int position) {
        holder.bindItem(status);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void pullTo() {
        status = LoadMord.LOAD_PULL_TO;
        notifyDataSetChanged();
    }

    public void loading() {
        status = LoadMord.LOAD_MORE;
        notifyDataSetChanged();
    }

    public void loadNone() {
        status = LoadMord.LOAD_NONE;
        notifyDataSetChanged();
    }

    public void loadEnd() {
        status = LoadMord.LOAD_END;
        notifyDataSetChanged();
    }
}
