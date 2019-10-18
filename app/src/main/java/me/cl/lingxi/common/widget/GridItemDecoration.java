package me.cl.lingxi.common.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.Objects;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int mDecoration;
    private ColorDrawable mDivider;

    public GridItemDecoration() {
        setDecoration(10);
        setDrawable(Color.parseColor("#00ffffff"));
    }

    public void setDecoration(int decoration) {
        mDecoration = decoration;
    }

    public void setDrawable(int color) {
        mDivider = new ColorDrawable(color);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int column_num = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDecoration;

            int top;
            int bottom;
            if ((i / column_num) == 0) {
                //画item最上面的分割线
                top = 0;
                bottom = top + mDecoration;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);

                //画item下面的分割线
                top = child.getBottom() + params.bottomMargin;
                bottom = top + mDecoration;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            } else {
                //画item下面的分割线
                top = child.getBottom() + params.bottomMargin;
                bottom = top + mDecoration;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        int column_num = getSpanCount(parent);

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;

            int left;
            int right;
            if ((i % column_num) == 0) {
                // item左边分割线
                left = 0;
                right = left + mDecoration;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
                // item右边分割线
                left = child.getRight() + params.rightMargin;
                right = left + mDecoration;
            } else {
                left = child.getRight() + params.rightMargin;
                right = left + mDecoration;
            }

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // 如果是最后一列，则不需要绘制右边
            return (pos + 1) % spanCount == 0;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                return (pos + 1) % spanCount == 0;
            } else {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // 如果是最后一行，则不需要绘制底部
            childCount = childCount - childCount % spanCount;
            return pos >= childCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
                // StaggeredGridLayoutManager 且横向滚动
            } else {
                // 如果是最后一行，则不需要绘制底部
                return (pos + 1) % spanCount == 0;
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view);
        int spanCount = getSpanCount(parent);
        int childCount = Objects.requireNonNull(parent.getAdapter()).getItemCount();
        // 如果是最后一行，则不需要绘制底部
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, mDecoration, 0);
            // 如果是最后一列，则不需要绘制右边
        } else if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, 0, mDecoration);
        } else {
            outRect.set(0, 0, mDecoration, mDecoration);
        }
    }
}