package me.cl.library.loadmore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView加载更多
 * Created by Bafsj on 2016/12/20.
 */

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {

    private int itemCount, lastPosition;

    public abstract void onLoadMore();

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            itemCount = linearLayoutManager.getItemCount();
            lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            itemCount = staggeredGridLayoutManager.getItemCount();
            int spanCount = staggeredGridLayoutManager.getSpanCount();
            int[] itemPositions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(new int[spanCount]);
            lastPosition = findLast(itemPositions);
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        // 判断RecyclerView的状态是空闲时，同时，是最后一个可见的ITEM时才加载
        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == itemCount - 1) {
            this.onLoadMore();
        }
    }

    /**
     * 找到最后一个item
     */
    private int findLast(int[] items) {
        int last = items[0];
        for (int item : items) {
            if (item > last) {
                last = item;
            }
        }
        return last;
    }
}
