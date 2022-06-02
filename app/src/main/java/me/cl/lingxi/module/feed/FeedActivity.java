package me.cl.lingxi.module.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.cl.library.base.BaseActivity;
import me.cl.library.photo.PhotoBrowser;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.EvaluateAdapter;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.FeedContentUtil;
import me.cl.lingxi.databinding.FeedActionIncludeBinding;
import me.cl.lingxi.databinding.FeedActivityBinding;
import me.cl.lingxi.databinding.FeedInfoIncludeBinding;
import me.cl.lingxi.databinding.FeedLikeIncludeBinding;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.Reply;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.module.member.UserActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;

/**
 * 动态详情页
 * TODO 从与我相关跳转目前没用动态图片，需要新增获取动态详情的接口
 */
public class FeedActivity extends BaseActivity implements View.OnClickListener {

    private FeedActivityBinding mBinding;
    private FeedInfoIncludeBinding mInfoBinding;
    private FeedActionIncludeBinding mActionBinding;
    private FeedLikeIncludeBinding mLikeBinding;
    private FeedViewModel mFeedViewModel;

    private int MSG_MODE;
    private final int MSG_EVALUATE = 0;
    private final int MSG_REPLY = 1;

    private String mFeedId;
    private String mCommentId;
    private String mToUid;
    private InputMethodManager mImm;
    private EvaluateAdapter mAdapter;

    private static final String FEED_TYPE = "feed";
    private Feed mFeed;

    public static void gotoFeed(Context context, Feed feed) {
        Intent intent = new Intent(context, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FEED_TYPE, feed);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = FeedActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        mInfoBinding = mBinding.includeFeedInfo;
        mActionBinding = mBinding.includeFeedAction;
        mLikeBinding = mBinding.includeFeedLike;

        ToolbarUtil.init(mBinding.includeTb.toolbar, this)
                .setTitle(R.string.title_bar_feed_detail)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        // 输入状态模式默认为评论
        MSG_MODE = MSG_EVALUATE;
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mBinding.editTuCao.addTextChangedListener(new EditTextWatcher());
        // 开始禁用
        mBinding.btnPublish.setClickable(false);
        mBinding.btnPublish.setSelected(false);

        // 点击事件
        mBinding.editTuCao.setOnClickListener(this);
        mBinding.btnPublish.setOnClickListener(this);
        mBinding.editMask.setOnClickListener(this);
        mInfoBinding.userImg.setOnClickListener(this);
        mActionBinding.feedLikeLayout.setOnClickListener(this);
        mActionBinding.feedCommentLayout.setOnClickListener(this);

        initRecyclerView();
        initViewModel();
        initView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new EvaluateAdapter(new ArrayList<>());
        mBinding.recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new EvaluateAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Comment comment) {
                switch (view.getId()) {
                    case R.id.user_img:
                        UserActivity.gotoUser(FeedActivity.this, comment.getUser());
                        break;
                    case R.id.evaluate_body:
                        MSG_MODE = MSG_REPLY;
                        mCommentId = String.valueOf(comment.getId());
                        mToUid = comment.getUser().getId();
                        mBinding.editTuCao.setHint("回复：" + comment.getUser().getUsername());
                        openSofInput(mBinding.editTuCao);
                        mBinding.editMask.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onItemChildClick(View view, String eid, Reply reply) {
                MSG_MODE = MSG_REPLY;
                mCommentId = eid;
                mToUid = reply.getUser().getId();
                mBinding.editTuCao.setHint("回复：" + reply.getUser().getUsername());
                openSofInput(mBinding.editTuCao);
                mBinding.editMask.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) return;
        mFeed = (Feed) bundle.getSerializable(FEED_TYPE);
        if (mFeed == null) return;

        mFeedId = mFeed.getId();

        User user = mFeed.getUser();
        mToUid = user.getId();

        // 动态详情
        ContentUtil.loadUserAvatar(mInfoBinding.userImg, user.getAvatar());
        mInfoBinding.userName.setText(user.getUsername());
        mInfoBinding.feedTime.setText(mFeed.getCreateTime());
        AppCompatTextView feedInfo = mInfoBinding.feedInfo;
        feedInfo.setText(FeedContentUtil.getFeedText(mFeed.getFeedInfo(), feedInfo));
        // 查看评论点赞数
        mActionBinding.feedViewNum.setText(String.valueOf(mFeed.getViewNum()));
        mActionBinding.feedCommentNum.setText(String.valueOf(mFeed.getCommentNum()));
        // 是否已经点赞
        mActionBinding.feedLikeIcon.setSelected(mFeed.isLike());
        mActionBinding.feedLikeLayout.setClickable(mFeed.isLike());
        // 点赞列表
        List<Like> likeList = mFeed.getLikeList();
        TextView likePeople = mLikeBinding.likePeople;
        LinearLayout likeWindow = mLikeBinding.likeWindow;
        TextView feedLikeNum = mActionBinding.feedLikeNum;
        ContentUtil.setLikePeopleAll(likePeople, feedLikeNum, likeWindow, likeList);

        // 图片
        final List<String> photos = mFeed.getPhotoList();
        if (photos != null && photos.size() > 0) {
            mBinding.photoRecyclerView.setVisibility(View.VISIBLE);
            int size = photos.size();
            // 如果只有一张或四张图，设置两列，否则三列
            int column = (size == 1 || size == 4) ? 2 : 3;
            mBinding.photoRecyclerView.setLayoutManager(new GridLayoutManager(mBinding.photoRecyclerView.getContext(), column));
            // 设置动态图片适配器
            ContentUtil.setFeedPhotoAdapter(mBinding.photoRecyclerView, photos, new FeedAdapter.OnItemListener() {
                @Override
                public void onItemClick(View view, Feed feed, int position) {

                }

                @Override
                public void onPhotoClick(ArrayList<String> photos, int position) {
                    PhotoBrowser.builder()
                            .setPhotos(photos)
                            .setCurrentItem(position)
                            .start(FeedActivity.this);
                }
            });
        } else {
            mBinding.photoRecyclerView.setVisibility(View.GONE);
        }

        mFeedViewModel.viewFeed(mFeedId);
        mFeedViewModel.doPageComment(1, 20 , mFeedId);
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.mTipMessage.observe(this, tipMessage -> {
            if (tipMessage.isRes()) {
                showToast(tipMessage.getMsgId());
            } else {
                showToast(tipMessage.getMsgStr());
            }
        });
        mFeedViewModel.mFeed.observe(this, feed -> {

        });
        mFeedViewModel.mFeedComment.observe(this, i -> {
            if (i == MSG_EVALUATE) {
                mActionBinding.feedCommentNum.setText(String.valueOf(Integer.parseInt(mActionBinding.feedCommentNum.getText().toString()) + 1));
            }
            mFeedViewModel.doPageComment(1, 20, mFeedId);
            // TODO 对回复进行处理，而非单纯直接调用接口刷新
        });
        mFeedViewModel.mCommentPage.observe(this, commentPageInfo -> {
            setData(commentPageInfo.getList());
        });
    }

    /**
     * 点击事件
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:
                UserActivity.gotoUser(this, mFeed.getUser());
                break;
            case R.id.feed_like_layout:
                showToast("点赞");
                break;
            case R.id.feed_comment_layout:
                mBinding.editTuCao.setHint("吐槽一下");
                MSG_MODE = MSG_EVALUATE;
                openSofInput(mBinding.editTuCao);
                mBinding.editMask.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_mask:
                mBinding.editMask.setVisibility(View.GONE);
                hideSoftInput(mBinding.editTuCao);
                break;
            case R.id.edit_tu_cao:
                mBinding.editMask.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_publish:
                publishComment();
                break;
        }
    }

    private void publishComment() {
        String msg = Objects.requireNonNull(mBinding.editTuCao.getText()).toString().trim();
        switch (MSG_MODE) {
            case MSG_EVALUATE:
                // 评论
                setLoading("评论中...");
                mFeedViewModel.addEvaluate(mFeedId, mToUid, msg);
                mBinding.editTuCao.setText(null);
                hideSoftInput(mBinding.editTuCao);
                break;
            case MSG_REPLY:
                // 回复
                setLoading("回复中...");
                mFeedViewModel.addReply(mFeedId, mCommentId, mToUid, msg);
                mBinding.editTuCao.setText(null);
                hideSoftInput(mBinding.editTuCao);
                break;
        }
    }

    /**
     * 调用输入法
     */
    public void openSofInput(EditText edit) {
        edit.setText(null);
        edit.requestFocus();
        mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏输入法
     */
    public void hideSoftInput(EditText edit) {
        mImm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public void setData(List<Comment> data) {
        mAdapter.setDate(data);
    }

    /**
     * EditText 监听
     */
    private class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isEdit = Objects.requireNonNull(mBinding.editTuCao.getText()).length() > 0;
            mBinding.btnPublish.setClickable(isEdit);
            mBinding.btnPublish.setSelected(isEdit);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
