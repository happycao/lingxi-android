package me.cl.lingxi.module.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
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
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.RelevantActivityBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.module.feed.FeedActivity;
import okhttp3.Call;

/**
 * 与我相关 && 我的回复
 */
public class RelevantActivity extends BaseActivity {

    private RelevantActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private String saveId;
    private final List<Relevant> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = RelevantActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
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

        ToolbarUtil.init(mToolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        saveId = SPUtil.build().getString(Constants.SP_USER_ID);
        loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

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

        if (isMine) {
            getRelevantList(Api.mineReply);
        } else {
            getRelevantList(Api.relevant);
            updateUnread();
        }
    }

    /**
     * 更新未读条数
     */
    public void updateUnread() {
        String userId = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.post()
                .url(Api.updateUnread)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        Constants.isRead = true;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    // 请求与我相关
    public void getRelevantList(String url) {
        Integer pageNum = 1;
        Integer pageSize = 20;
        OkUtil.post()
                .url(url)
                .addParam("userId", saveId)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .setLoadDelay()
                .setProgressDialog(loadingProgress)
                .execute(new ResultCallback<Result<PageInfo<Relevant>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Relevant>> response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("加载失败，下拉重新加载");
                            return;
                        }
                        updateData(response.getData().getList());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("加载失败，下拉重新加载");
                    }
                });
    }

    private void updateData(List<Relevant> relevantList) {
        mAdapter.updateData(relevantList);
    }

    //前往详情页
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
