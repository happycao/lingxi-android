package me.cl.lingxi.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseFragment;
import me.cl.library.recycle.ItemAnimator;
import me.cl.lingxi.adapter.FutureAdapter;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.databinding.FutureFragmentBinding;
import me.cl.lingxi.entity.Future;
import me.cl.lingxi.module.future.FutureActivity;
import me.cl.lingxi.viewmodel.FutureViewModel;

/**
 * 写给未来
 */
public class FutureFragment extends BaseFragment {

    private static final String PARAM_TYPE = "_type";

    private FutureFragmentBinding mFragmentBinding;
    private FutureViewModel mFutureViewModel;

    private RecyclerView mRecyclerView;
    private TextView mTvToFuture;

    private final List<Future> mList = new ArrayList<>();
    private FutureAdapter mAdapter;

    private int mPageNum = 1;
    private static final int PAGE_SIZE = 10;

    public FutureFragment() {

    }

    public static FutureFragment newInstance(String type) {
        FutureFragment fragment = new FutureFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mType = getArguments().getString(PARAM_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentBinding = FutureFragmentBinding.inflate(inflater, container, false);
        init();
        return mFragmentBinding.getRoot();
    }

    private void init() {
        mRecyclerView = mFragmentBinding.recyclerView;
        mTvToFuture = mFragmentBinding.tvToFuture;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        // 列表再底部开始展示，反转后由上面开始展示
        layoutManager.setStackFromEnd(true);
        // 列表翻转
        layoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        mAdapter = new FutureAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        initEvent();
        initViewModel();

        // 获取未来日记列表 TODO 后续添加加载更多功能
        mFutureViewModel.doPageFuture(mPageNum, PAGE_SIZE);
    }

    // 初始化事件
    private void initEvent() {
        // item点击
        mAdapter.setOnItemListener((view, future) -> {

        });

        // 致未来
        mTvToFuture.setOnClickListener(view -> {
            gotoFuture();
        });
    }

    private void initViewModel() {
        mFutureViewModel = new ViewModelProvider(this).get(FutureViewModel.class);
        mFutureViewModel.getTipMessage().observe(requireActivity(), this::showTip);
        mFutureViewModel.getFutures().observe(requireActivity(), futurePageInfo -> {
            Integer pageNum = futurePageInfo.getPageNum();
            Integer size = futurePageInfo.getSize();
            if (size == 0) {
                return;
            }
            mPageNum = pageNum + 1;
            List<Future> list = futurePageInfo.getList();
            if (pageNum == 1) {
                setData(list);
            } else {
                updateData(list);
            }

        });
    }

    // 提示
    private void showTip(TipMessage tipMessage) {
        if (tipMessage.isRes()) {
            showToast(tipMessage.getMsgId());
        } else {
            showToast(tipMessage.getMsgStr());
        }
    }

    private void gotoFuture() {
        Intent intent = new Intent(getActivity(), FutureActivity.class);
        startActivity(intent);
    }

    // 设置数据
    private void setData(List<Future> data) {
        mAdapter.setData(data);
    }

    // 更新数据
    private void updateData(List<Future> data) {
        mAdapter.addData(data);
    }

}
