package me.cl.lingxi.module.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseFragment;
import me.cl.library.loadmore.LoadMoreAdapter;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.library.photo.PhotoBrowser;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.FeedFragmentBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.module.feed.FeedActivity;
import me.cl.lingxi.module.feed.PublishActivity;
import me.cl.lingxi.module.member.UserActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;

/**
 * 圈子动态
 */
public class FeedFragment extends BaseFragment {

    private static final String FEED_TYPE = "feed_type";

    private FeedFragmentBinding mBinding;
    private FeedViewModel mFeedViewModel;

    private String saveUid;
    private String saveUName;

    private final List<Feed> mList = new ArrayList<>();
    private FeedAdapter mFeedAdapter;
    private final LoadMoreAdapter mLoadMoreAdapter = new LoadMoreAdapter();

    private int mPageNum = 1;
    private static final int PAGE_SIZE = 10;

    public FeedFragment() {

    }

    public static FeedFragment newInstance(String feedType) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(FEED_TYPE, feedType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mType = getArguments().getString(FEED_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FeedFragmentBinding.inflate(inflater, container, false);
        init();
        return mBinding.getRoot();
    }

    private void init() {
        ToolbarUtil.init(mBinding.includeTb.toolbar, getActivity())
                .setTitle(R.string.nav_camera)
                .setTitleCenter()
                .setMenu(R.menu.publish_menu, item -> {
                    if (item.getItemId() == R.id.action_share) {
                        gotoPublish();
                    }
                    return false;
                })
                .build();

        saveUid = SPUtil.build().getString(Constants.SP_USER_ID);
        saveUName = SPUtil.build().getString(Constants.SP_USER_NAME);

        initRecyclerView();
        initEvent();
        initViewModel();

        onRefresh();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setItemAnimator(new ItemAnimator());
        mFeedAdapter = new FeedAdapter(mList);
        ConcatAdapter concatAdapter = new ConcatAdapter(mFeedAdapter, mLoadMoreAdapter);
        mBinding.recyclerView.setAdapter(concatAdapter);
    }

    // 初始化事件
    private void initEvent() {
        // 刷新
        mBinding.swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        // item点击
        mFeedAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Feed feed, int position) {
                switch (view.getId()) {
                    case R.id.user_img:
                        goToUser(feed.getUser());
                        break;
                    case R.id.feed_card:
                    case R.id.feed_comment_layout:
                        gotoMood(feed);
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
                PhotoBrowser.builder()
                        .setPhotos(photos)
                        .setCurrentItem(position)
                        .start(requireActivity());
            }
        });

        // 滑动监听
        mBinding.recyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mFeedAdapter.getItemCount() < 4) return;

                mLoadMoreAdapter.loading();

                mBinding.recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFeedViewModel.doPageFeed(mPageNum, PAGE_SIZE);
                    }
                },500);
            }
        });
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.mTipMessage.observe(requireActivity(), tipMessage -> {
            if (mBinding.swipeRefreshLayout.isRefreshing()) {
                mBinding.swipeRefreshLayout.setRefreshing(false);
            }
            if (tipMessage.isRes()) {
                showToast(tipMessage.getMsgId());
            } else {
                showToast(tipMessage.getMsgStr());
            }
            mLoadMoreAdapter.loadEnd();
        });
        mFeedViewModel.mFeedPage.observe(requireActivity(), feedPageInfo -> {
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
                setData(list);
            } else {
                updateData(list);
            }
            mLoadMoreAdapter.loadEnd();
        });
        mFeedViewModel.mFeed.observe(requireActivity(), feed -> {
            List<Like> likeList = new ArrayList<>(feed.getLikeList());
            Like like = new Like();
            like.setUserId(saveUid);
            like.setUsername(saveUName);
            likeList.add(like);
            feed.setLikeList(likeList);
            feed.setLike(true);
            mFeedAdapter.updateItem(feed, feed.getPosition());
        });
    }

    // 前往动态发布
    private void gotoPublish() {
        Intent intent = new Intent(getActivity(), PublishActivity.class);
        startActivityForResult(intent, Constants.ACTIVITY_PUBLISH);
    }

    // 前往动态详情
    private void gotoMood(Feed feed) {
        Intent intent = new Intent(getActivity(), FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_MOOD);
    }

    // 设置数据
    private void setData(List<Feed> data){
        mFeedAdapter.setData(data);
    }

    // 更新数据
    public void updateData(List<Feed> data) {
        mFeedAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        mPageNum = 1;
        if (!mBinding.swipeRefreshLayout.isRefreshing()) {
            mBinding.swipeRefreshLayout.setRefreshing(true);
        }
        mFeedViewModel.doPageFeed(mPageNum, PAGE_SIZE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 发布动态回退则掉调起刷新
        if (resultCode == Constants.ACTIVITY_PUBLISH) {
            onRefresh();
        }
    }

    /**
     * 前往用户页面
     */
    private void goToUser(User user){
        Intent intent = new Intent(getActivity(), UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASSED_USER_INFO, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
