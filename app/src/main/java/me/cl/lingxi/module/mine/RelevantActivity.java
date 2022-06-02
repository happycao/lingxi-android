package me.cl.lingxi.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.library.view.MoeToast;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.RelevantAdapter;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.databinding.RelevantActivityBinding;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.module.feed.FeedActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;

/**
 * 与我相关 && 我的回复
 * TODO 未做加载更多，需要后续补充
 */
public class RelevantActivity extends BaseActivity {

    private RelevantActivityBinding mBinding;
    private FeedViewModel mFeedViewModel;

    private RelevantAdapter mAdapter;
    private final List<Relevant> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = RelevantActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        int x = (int) (Math.random() * 4) + 1;
        if (x == 1) {
            MoeToast.makeText(this, R.string.egg_can_you_find);
        }

        Intent intent = getIntent();
        String replyType = intent.getStringExtra(Constants.REPLY_TYPE);
        if (TextUtils.isEmpty(replyType)) {
            onBackPressed();
            return;
        }

        boolean isMine = false;
        String title = "";
        switch (replyType) {
            case Constants.REPLY_MY:
                title = getString(R.string.title_bar_my_reply);
                isMine = true;
                break;
            case Constants.REPLY_RELEVANT:
                title = getString(R.string.title_bar_relevant);
                break;
            default:
                onBackPressed();
                break;
        }

        ToolbarUtil.init(mBinding.includeTb.toolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        LoadingDialog loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

        initRecyclerView();
        initViewModel();
        if (isMine) {
            mFeedViewModel.pageMineReply(1, 20, loadingProgress);
        } else {
            mFeedViewModel.pageRelevant(1, 20, loadingProgress);
            mFeedViewModel.updateUnread();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.includeRv.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RelevantAdapter(mRelevantList);
        mBinding.includeRv.recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener((view, relevant) -> {
            switch (view.getId()) {
                case R.id.user_img:
                    break;
                case R.id.feed_body:
                    FeedActivity.gotoFeed(RelevantActivity.this, relevant.getFeed());
                    break;
            }
        });
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.mTipMessage.observe(this, this::showTip);
        mFeedViewModel.mRelevantPage.observe(this, relevantPageInfo -> {
            dismissLoading();
            setData(relevantPageInfo.getList());
        });
    }

    private void setData(List<Relevant> relevantList) {
        mAdapter.setData(relevantList);
    }

    private void updateData(List<Relevant> relevantList) {
        mAdapter.updateData(relevantList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        OkUtil.newInstance().cancelAll();
        super.onDestroy();
    }
}
