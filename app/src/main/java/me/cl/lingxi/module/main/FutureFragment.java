package me.cl.lingxi.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseFragment;
import me.cl.library.recycle.ItemAnimator;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FutureAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.databinding.FutureFragmentBinding;
import me.cl.lingxi.entity.Future;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.module.future.FutureActivity;
import okhttp3.Call;

/**
 * 写给未来
 */
public class FutureFragment extends BaseFragment {

    private static final String PARAM_TYPE = "_type";

    private FutureFragmentBinding mFragmentBinding;

    private RecyclerView mRecyclerView;
    private TextView mTvToFuture;

    private List<Future> mList = new ArrayList<>();
    private FutureAdapter mAdapter;

    private int mPage = 1;
    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

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

        getFutureList(mPage, mCount);
    }

    //初始化事件
    private void initEvent() {
        // item点击
        mAdapter.setOnItemListener((view, future) -> {

        });

        // 致未来
        mTvToFuture.setOnClickListener(view -> {
            gotoFuture();
        });
    }

    private void gotoFuture() {
        Intent intent = new Intent(getActivity(), FutureActivity.class);
        startActivity(intent);
    }

    // 获取未来日记列表
    private void getFutureList(int pageNum, int pageSize) {
        OkUtil.post()
                .url(Api.pageFuture)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Future>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Future>> response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast(R.string.toast_get_future_error);
                            return;
                        }
                        PageInfo<Future> page = response.getData();
                        Integer size = page.getSize();
                        if (size == 0) {
                            return;
                        }
                        mPage++;
                        List<Future> list = page.getList();
                        switch (RefreshMODE) {
                            case MOD_LOADING:
                                updateData(list);
                                break;
                            default:
                                setData(list);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast(R.string.toast_get_future_error);
                    }
                });
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
