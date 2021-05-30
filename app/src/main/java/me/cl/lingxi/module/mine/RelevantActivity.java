package me.cl.lingxi.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.library.view.MoeToast;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.RelevantAdapter;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.databinding.RelevantActivityBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.module.feed.FeedActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;

/**
 * 与我相关 && 我的回复
 * TODO 未做加载更多，需要后续补充
 */
public class RelevantActivity extends BaseActivity {

    private RelevantActivityBinding mActivityBinding;
    private FeedViewModel mFeedViewModel;

    private RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private final List<Relevant> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = RelevantActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        Toolbar toolbar = mActivityBinding.includeToolbar.toolbar;
        mRecyclerView = mActivityBinding.includeRecyclerView.recyclerView;

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

        ToolbarUtil.init(toolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

        initRecyclerView();
        initViewModel();
        if (isMine) {
            mFeedViewModel.pageMineReply(1, 20, loadingProgress);
        } else {
            mFeedViewModel.pageRelevant(1, 20, loadingProgress);
            mFeedViewModel.updateUnread();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RelevantAdapter(mRelevantList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new RelevantAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Relevant relevant) {
                switch (view.getId()) {
                    case R.id.user_img:
                        break;
                    case R.id.feed_body:
                        gotoFeed(relevant.getFeed());
                        break;
                }
            }
        });
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.getTipMessage().observe(this, this::showTip);
        mFeedViewModel.getRelevantPage().observe(this, relevantPageInfo -> {
            dismissLoading();
            setData(relevantPageInfo.getList());
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

    private void setData(List<Relevant> relevantList) {
        mAdapter.setData(relevantList);
    }

    private void updateData(List<Relevant> relevantList) {
        mAdapter.updateData(relevantList);
    }

    // 前往详情页
    private void gotoFeed(Feed feed) {
        Intent intent = new Intent(RelevantActivity.this, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivity(intent);
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
