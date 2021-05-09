package me.cl.lingxi.module.member;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.loadmore.LoadMoreAdapter;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.recycle.ItemDecoration;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.databinding.UserActivityBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.module.feed.FeedActivity;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

/**
 * 用户界面
 */
public class UserActivity extends BaseActivity implements View.OnClickListener {

    private UserActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBar;
    private RelativeLayout mButtonBar;
    private ImageView mParallax;
    private TextView mTitleName;
    private TextView mUserName;
    private TextView mContact;
    private TextView mFeedNum;

    private boolean isPostUser = true;
    private String mUserId;
    private List<Feed> mFeedList = new ArrayList<>();
    private ConcatAdapter mConcatAdapter;
    private FeedAdapter mFeedAdapter;
    private LoadMoreAdapter mLoadMoreAdapter = new LoadMoreAdapter();

    private int mPageNum = 1;
    private int mPageSize = 10;
    private final int MODE_REFRESH = 1;
    private final int MODE_LOADING = 2;
    private int mRefreshMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = UserActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.toolbar;
        mSwipeRefreshLayout = mActivityBinding.swipeRefreshLayout;
        mRecyclerView = mActivityBinding.recyclerView;
        mAppBar = mActivityBinding.appBar;
        mButtonBar = mActivityBinding.buttonBar;
        mParallax = mActivityBinding.parallax;
        mTitleName = mActivityBinding.titleName;
        mUserName = mActivityBinding.userName;
        mContact = mActivityBinding.contact;
        mFeedNum = mActivityBinding.feedNum;

        mContact.setOnClickListener(this);

        ToolbarUtil.init(mToolbar, this)
                .setBack()
                .build();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String username = intent.getStringExtra(Constants.PASSED_USER_NAME);
        if (bundle != null) {
            User user = (User) bundle.getSerializable(Constants.PASSED_USER_INFO);
            if (user != null) {
                isPostUser = false;
                username = user.getUsername();
                setAvatar(user.getAvatar());
            }
        }
        postSearchUser(username);

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(LinearLayoutCompat.VERTICAL, 10, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemDecoration.setGoneLast(true);
        mRecyclerView.addItemDecoration(itemDecoration);
        mFeedAdapter = new FeedAdapter(mFeedList);
        mConcatAdapter = new ConcatAdapter(mFeedAdapter, mLoadMoreAdapter);
        mRecyclerView.setAdapter(mConcatAdapter);
    }

    /**
     * 搜索用户
     */
    private void postSearchUser(String username) {
        OkUtil.post()
                .url(Api.searchUser)
                .addParam("username", username)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        initUser(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        initUser(null);
                    }
                });
    }

    /**
     * 设置用户相关信息
     */
    private void initUser(UserInfo userInfo) {
        boolean isRc = false;
        String avatar = "";
        String username;
        if (userInfo != null) {
            mUserId = userInfo.getId();
            username = userInfo.getUsername();
            avatar = userInfo.getAvatar();
            if (!TextUtils.isEmpty(userInfo.getImToken())) {
                isRc = true;
            }
            initEvent();
            pageFeed(mPageNum, mPageSize);
        } else {
            username = "未知用户";
            showToast(username);
        }
        mTitleName.setText(username);
        mUserName.setText(username);
        if (!isRc) {
            mContact.setVisibility(View.GONE);
        }
        if (isPostUser) {
            setAvatar(avatar);
        }
    }

    /**
     * 设置头像相关图片
     */
    public void setAvatar(String avatar) {
//        ContentUtil.loadUserAvatar(mTitleImg, avatar);
//        ContentUtil.loadUserAvatar(mUserImg, avatar);
        ContentUtil.loadRelativeBlurImage(mParallax, avatar, 10);
        switchTitle();
    }

    // 初始化事件
    private void initEvent() {
        // 下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });

        // item点击
        mFeedAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Feed feed, int position) {
                switch (view.getId()) {
                    case R.id.feed_card:
                    case R.id.feed_comment_layout:
                        goToFeed(feed);
                        break;
                    case R.id.feed_like_layout:
                        if (feed.isLike()) return;
                        // 未点赞点赞
                        postLike(feed, position);
                        break;
                }
            }

            @Override
            public void onPhotoClick(ArrayList<String> photos, int position) {
                PhotoPreview.builder()
                        .setPhotos(photos)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(UserActivity.this);
            }
        });

        // 加载更多
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mFeedAdapter.getItemCount() < 4) return;

                mRefreshMode = MODE_LOADING;
                mLoadMoreAdapter.loading();

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageFeed(mPageNum, mPageSize);
                    }
                }, 1000);
            }
        });
    }

    private void switchTitle() {
        // 标题切换
        mButtonBar.setAlpha(0);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int h = appBarLayout.getTotalScrollRange();
                int offset = Math.abs(verticalOffset);
                if (h == offset) return;

                mSwipeRefreshLayout.setEnabled(offset == 0);

                int bbr = offset - 50 < 0 ? 0 : offset;
                mButtonBar.setAlpha(1f * bbr / h);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.contact) {
            showToast("紧张开发中");
        }
    }

    private void postLike(Feed feed, int position) {
    }

    // 获取动态列表
    private void pageFeed(int pageNum, int pageSize) {
        if (!mSwipeRefreshLayout.isRefreshing() && mRefreshMode == MODE_REFRESH) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        OkUtil.post()
                .url(Api.pageFeed)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .addParam("searchUserId", mUserId)
                .execute(new ResultCallback<Result<PageInfo<Feed>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Feed>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            mLoadMoreAdapter.loadNone();
                            showToast(R.string.toast_get_feed_error);
                            return;
                        }
                        PageInfo<Feed> page = response.getData();
                        Integer size = page.getSize();
                        if (size == 0) {
                            mLoadMoreAdapter.loadNone();
                            return;
                        }
                        mPageNum++;
                        List<Feed> list = page.getList();
                        if (mRefreshMode == MODE_LOADING) {
                            updateData(list);
                        } else {
                            mFeedAdapter.setData(list);
                            mFeedNum.setText(String.valueOf(page.getTotal()));
                        }
                        mLoadMoreAdapter.loadEnd();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mLoadMoreAdapter.loadNone();
                        showToast(R.string.toast_get_feed_error);
                    }
                });
    }

    //更新数据
    public void updateData(List<Feed> data) {
        mFeedAdapter.addData(data);
    }

    // 刷新数据
    private void onRefreshData() {
        mRefreshMode = MODE_REFRESH;
        mPageNum = 1;
        pageFeed(mPageNum, mPageSize);
    }

    // 前往动态详情
    private void goToFeed(Feed feed) {
        Intent intent = new Intent(this, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
//        startActivityForResult(intent, Constants.ACTIVITY_MOOD);
        startActivity(intent);
    }
}
