package me.cl.lingxi.module.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.loadmore.LoadMoreAdapter;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.library.model.TipMessage;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.databinding.UserActivityBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.module.feed.FeedActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;
import me.cl.lingxi.viewmodel.UserViewModel;
import me.iwf.photopicker.PhotoPreview;

/**
 * 用户界面
 */
public class UserActivity extends BaseActivity implements View.OnClickListener {

    private UserActivityBinding mBinding;
    private UserViewModel mUserViewModel;
    private FeedViewModel mFeedViewModel;

    private boolean isPostUser = true;
    private String mUserId;
    private String mUsername;
    private final List<Feed> mFeedList = new ArrayList<>();
    private ConcatAdapter mConcatAdapter;
    private FeedAdapter mFeedAdapter;
    private final LoadMoreAdapter mLoadMoreAdapter = new LoadMoreAdapter();

    private int mPageNum = 1;
    private static final int PAGE_SIZE = 10;

    public static void gotoUser(Context context, User user) {
        Intent intent = new Intent(context, UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASSED_USER_INFO, user);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = UserActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        mBinding.contact.setOnClickListener(this);

        ToolbarUtil.init(mBinding.toolbar, this)
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

        initRecyclerView();
        initViewModel();
        mUserViewModel.searchUser(username);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setItemAnimator(new ItemAnimator());
        mFeedAdapter = new FeedAdapter(mFeedList);
        mConcatAdapter = new ConcatAdapter(mFeedAdapter, mLoadMoreAdapter);
        mBinding.recyclerView.setAdapter(mConcatAdapter);
    }

    private void initViewModel() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        mUserViewModel = viewModelProvider.get(UserViewModel.class);
        mFeedViewModel = viewModelProvider.get(FeedViewModel.class);
        mUserViewModel.mTipMessage.observe(this, this::showTip);
        mFeedViewModel.mTipMessage.observe(this, this::showTip);
        mUserViewModel.mUserInfo.observe(this, this::initUser);
        mFeedViewModel.mFeedPage.observe(this, this::setFeedData);
        mFeedViewModel.mFeed.observe(this, this::setFeedLike);
    }

    /**
     * 设置用户相关信息
     */
    private void initUser(UserInfo userInfo) {
        boolean isRc = false;
        String signature = getString(R.string.hint_signature_default);
        String avatar = "";
        if (userInfo != null) {
            mUserId = userInfo.getId();
            mUsername = userInfo.getUsername();
            if (!TextUtils.isEmpty(userInfo.getSignature())) {
                signature = userInfo.getSignature();
            }
            avatar = userInfo.getAvatar();
            if (!TextUtils.isEmpty(userInfo.getImToken())) {
                isRc = true;
            }
            initEvent();
            onRefreshData();
        } else {
            mUsername = "未知用户";
            showToast(mUsername);
        }
        mBinding.titleName.setText(mUsername);
        mBinding.userName.setText(mUsername);
        mBinding.signature.setText(signature);
        if (!isRc) {
            mBinding.contact.setVisibility(View.GONE);
        }
        if (isPostUser) {
            setAvatar(avatar);
        }
    }

    /**
     * 设置头像相关图片
     */
    public void setAvatar(String avatar) {
        ContentUtil.loadRelativeBlurImage(mBinding.parallax, avatar, 10);
        switchTitle();
    }

    // 初始化事件
    private void initEvent() {
        // 下拉刷新
        mBinding.swipeRefreshLayout.setOnRefreshListener(this::onRefreshData);

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
                        feed.setPosition(position);
                        mFeedViewModel.doLike(feed);
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
        mBinding.recyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mFeedAdapter.getItemCount() < 4) return;

                mLoadMoreAdapter.loading();

                mBinding.recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFeedViewModel.doPageFeed(mPageNum, PAGE_SIZE, mUserId);
                    }
                }, 500);
            }
        });
    }

    private void switchTitle() {
        // 标题切换
        mBinding.buttonBar.setAlpha(0);
        mBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int h = appBarLayout.getTotalScrollRange();
                int offset = Math.abs(verticalOffset);
                if (h == offset) return;

                mBinding.swipeRefreshLayout.setEnabled(offset == 0);

                int bbr = offset - 50 < 0 ? 0 : offset;
                mBinding.buttonBar.setAlpha(1f * bbr / h);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.contact) {
            showToast("紧张开发中");
        }
    }

    // 设置动态数据
    private void setFeedData(PageInfo<Feed> feedPageInfo) {
        if (mBinding.swipeRefreshLayout.isRefreshing()) {
            mBinding.swipeRefreshLayout.setRefreshing(false);
        }
        Integer pageNum = feedPageInfo.getPageNum();
        Integer size = feedPageInfo.getSize();
        if (size == 0) {
            mLoadMoreAdapter.loadNone();
            return;
        }
        mPageNum = pageNum + 1;
        List<Feed> list = feedPageInfo.getList();
        if (pageNum == 1) {
            mFeedAdapter.setData(list);
            mBinding.feedNum.setText(String.valueOf(feedPageInfo.getTotal()));
        } else {
            updateData(list);
        }
        mLoadMoreAdapter.loadEnd();
    }

    // 设置动态点赞
    private void setFeedLike(Feed feed) {
        List<Like> likeList = new ArrayList<>(feed.getLikeList());
        Like like = new Like();
        like.setUserId(mUserId);
        like.setUsername(mUsername);
        likeList.add(like);
        feed.setLikeList(likeList);
        feed.setLike(true);
        mFeedAdapter.updateItem(feed, feed.getPosition());
    }

    @Override
    protected void showTip(TipMessage tipMessage) {
        if (mBinding.swipeRefreshLayout.isRefreshing()) {
            mBinding.swipeRefreshLayout.setRefreshing(false);
        }
        super.showTip(tipMessage);
        mLoadMoreAdapter.loadEnd();
    }

    // 更新数据
    public void updateData(List<Feed> data) {
        mFeedAdapter.addData(data);
    }

    // 刷新数据
    private void onRefreshData() {
        mPageNum = 1;
        if (!mBinding.swipeRefreshLayout.isRefreshing()) {
            mBinding.swipeRefreshLayout.setRefreshing(true);
        }
        mFeedViewModel.doPageFeed(mPageNum, PAGE_SIZE, mUserId);
    }

    private void hindLoadMore() {
        mConcatAdapter.removeAdapter(mLoadMoreAdapter);
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
