package me.cl.lingxi.module.member;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseActivity;
import me.cl.library.loadmore.LoadMord;
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
public class UserActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.button_bar)
    RelativeLayout mButtonBar;
    @BindView(R.id.parallax)
    ImageView mParallax;
    @BindView(R.id.title_name)
    TextView mTitleName;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.contact)
    TextView mContact;
    @BindView(R.id.feed_num)
    TextView mFeedNum;

    private boolean isPostUser = true;
    private String mUserId;
    private List<Feed> mFeedList = new ArrayList<>();
    private FeedAdapter mAdapter;

    private int mPageNum = 1;
    private int mPageSize = 10;
    private final int MODE_REFRESH = 1;
    private final int MODE_LOADING = 2;
    private int mRefreshMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
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
        mAdapter = new FeedAdapter(mFeedList);
        mRecyclerView.setAdapter(mAdapter);

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
        mAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
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
                if (mAdapter.getItemCount() < 4) return;

                mRefreshMode = MODE_LOADING;
                mAdapter.updateLoadStatus(LoadMord.LOAD_MORE);

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

    @OnClick({R.id.contact})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact:
                showToast("紧张开发中");
                break;
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
                            mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                            showToast(R.string.toast_get_feed_error);
                            return;
                        }
                        PageInfo<Feed> page = response.getData();
                        Integer size = page.getSize();
                        if (size == 0) {
                            mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                            return;
                        }
                        mPageNum++;
                        List<Feed> list = page.getList();
                        switch (mRefreshMode) {
                            case MODE_LOADING:
                                updateData(list);
                                break;
                            default:
                                mAdapter.setData(list);
                                mFeedNum.setText(String.valueOf(page.getTotal()));
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                        showToast(R.string.toast_get_feed_error);
                    }
                });
    }

    //更新数据
    public void updateData(List<Feed> data) {
        mAdapter.addData(data);
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
