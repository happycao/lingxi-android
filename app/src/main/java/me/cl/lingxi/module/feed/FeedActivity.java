package me.cl.lingxi.module.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.photo.PhotoBrowser;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.EvaluateAdapter;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Constants;
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

    private FeedActivityBinding mActivityBinding;
    private FeedInfoIncludeBinding mFeedInfoBinding;
    private FeedActionIncludeBinding mFeedActionBinding;
    private FeedLikeIncludeBinding mFeedLikeBinding;
    private FeedViewModel mFeedViewModel;

    private AppCompatEditText mEditTuCao;
    private Button mBtnPublish;
    private View mEditMask;
    private RecyclerView mRecyclerView;
    private RecyclerView mPhotoRecyclerView;
    private ImageView mUserImg;
    private TextView mFeedCommentNum;

    private int MSG_MODE;
    private final int MSG_EVALUATE = 0;
    private final int MSG_REPLY = 1;

    private String mFeedId;
    private String mCommentId;
    private String toUid;
    private InputMethodManager imm;
    private EvaluateAdapter mAdapter;

    private Feed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = FeedActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mFeedInfoBinding = mActivityBinding.includeFeedInfo;
        mFeedActionBinding = mActivityBinding.includeFeedAction;
        mFeedLikeBinding = mActivityBinding.includeFeedLike;

        Toolbar toolbar = mActivityBinding.includeToolbar.toolbar;
        mEditTuCao = mActivityBinding.editTuCao;
        mBtnPublish = mActivityBinding.btnPublish;
        mEditMask = mActivityBinding.editMask;
        mRecyclerView = mActivityBinding.recyclerView;
        mPhotoRecyclerView = mActivityBinding.photoRecyclerView;

        mUserImg = mFeedInfoBinding.userImg;
        mFeedCommentNum = mFeedActionBinding.feedCommentNum;
        LinearLayout feedLikeLayout = mFeedActionBinding.feedLikeLayout;
        LinearLayout feedCommentLayout = mFeedActionBinding.feedCommentLayout;

        ToolbarUtil.init(toolbar, this)
                .setTitle(R.string.title_bar_feed_detail)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        // 输入状态模式默认为评论
        MSG_MODE = MSG_EVALUATE;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditTuCao.addTextChangedListener(new EditTextWatcher());
        // 开始禁用
        mBtnPublish.setClickable(false);
        mBtnPublish.setSelected(false);

        // 点击事件
        mEditTuCao.setOnClickListener(this);
        mBtnPublish.setOnClickListener(this);
        mEditMask.setOnClickListener(this);
        mUserImg.setOnClickListener(this);
        feedLikeLayout.setOnClickListener(this);
        feedCommentLayout.setOnClickListener(this);

        initRecyclerView();
        initViewModel();
        initView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new EvaluateAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new EvaluateAdapter.OnItemListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, Comment comment) {
                switch (view.getId()) {
                    case R.id.user_img:
                        gotoUser(comment.getUser());
                        break;
                    case R.id.evaluate_body:
                        MSG_MODE = MSG_REPLY;
                        mCommentId = String.valueOf(comment.getId());
                        toUid = comment.getUser().getId();
                        mEditTuCao.setHint("回复：" + comment.getUser().getUsername());
                        openSofInput(mEditTuCao);
                        mEditMask.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onItemChildClick(View view, String eid, Reply reply) {
                MSG_MODE = MSG_REPLY;
                mCommentId = eid;
                toUid = reply.getUser().getId();
                mEditTuCao.setHint("回复：" + reply.getUser().getUsername());
                openSofInput(mEditTuCao);
                mEditMask.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) return;
        feed = (Feed) bundle.getSerializable("feed");
        if (feed == null) return;

        mFeedId = feed.getId();

        User user = feed.getUser();
        toUid = user.getId();

        // 动态详情
        ContentUtil.loadUserAvatar(mUserImg, user.getAvatar());
        mFeedInfoBinding.userName.setText(user.getUsername());
        mFeedInfoBinding.feedTime.setText(feed.getCreateTime());
        AppCompatTextView feedInfo = mFeedInfoBinding.feedInfo;
        feedInfo.setText(FeedContentUtil.getFeedText(feed.getFeedInfo(), feedInfo));
        // 查看评论点赞数
        mFeedActionBinding.feedViewNum.setText(String.valueOf(feed.getViewNum()));
        mFeedCommentNum.setText(String.valueOf(feed.getCommentNum()));
        // 是否已经点赞
        mFeedActionBinding.feedLikeIcon.setSelected(feed.isLike());
        mFeedActionBinding.feedLikeLayout.setClickable(feed.isLike());
        // 点赞列表
        List<Like> likeList = feed.getLikeList();
        TextView likePeople = mFeedLikeBinding.likePeople;
        LinearLayout likeWindow = mFeedLikeBinding.likeWindow;
        TextView feedLikeNum = mFeedActionBinding.feedLikeNum;
        ContentUtil.setLikePeopleAll(likePeople, feedLikeNum, likeWindow, likeList);

        // 图片
        final List<String> photos = feed.getPhotoList();
        if (photos != null && photos.size() > 0) {
            mPhotoRecyclerView.setVisibility(View.VISIBLE);
            int size = photos.size();
            // 如果只有一张或四张图，设置两列，否则三列
            int column = (size == 1 || size == 4) ? 2 : 3;
            mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(mPhotoRecyclerView.getContext(), column));
            // 设置动态图片适配器
            ContentUtil.setFeedPhotoAdapter(mPhotoRecyclerView, photos, new FeedAdapter.OnItemListener() {
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
            mPhotoRecyclerView.setVisibility(View.GONE);
        }

        mFeedViewModel.viewFeed(mFeedId);
        mFeedViewModel.doPageComment(1, 20 , mFeedId);
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.getTipMessage().observe(this, tipMessage -> {
            if (tipMessage.isRes()) {
                showToast(tipMessage.getMsgId());
            } else {
                showToast(tipMessage.getMsgStr());
            }
        });
        mFeedViewModel.getFeed().observe(this, feed -> {

        });
        mFeedViewModel.getFeedComment().observe(this, i -> {
            if (i == MSG_EVALUATE) {
                mFeedCommentNum.setText(String.valueOf(Integer.parseInt(mFeedCommentNum.getText().toString()) + 1));
            }
            mFeedViewModel.doPageComment(1, 20, mFeedId);
            // TODO 对回复进行处理，而非单纯直接调用接口刷新
        });
        mFeedViewModel.getCommentPage().observe(this, commentPageInfo -> {
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
                gotoUser(feed.getUser());
                break;
            case R.id.feed_like_layout:
                showToast("点赞");
                break;
            case R.id.feed_comment_layout:
                mEditTuCao.setHint("吐槽一下");
                MSG_MODE = MSG_EVALUATE;
                openSofInput(mEditTuCao);
                mEditMask.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_mask:
                mEditMask.setVisibility(View.GONE);
                hideSoftInput(mEditTuCao);
                break;
            case R.id.edit_tu_cao:
                mEditMask.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_publish:
                publishComment();
                break;
        }
    }

    private void publishComment() {
        String msg = mEditTuCao.getText().toString().trim();
        switch (MSG_MODE) {
            case MSG_EVALUATE:
                // 评论
                setLoading("评论中...");
                mFeedViewModel.addEvaluate(mFeedId, toUid, msg);
                mEditTuCao.setText(null);
                hideSoftInput(mEditTuCao);
                break;
            case MSG_REPLY:
                // 回复
                setLoading("回复中...");
                mFeedViewModel.addReply(mFeedId, mCommentId, toUid, msg);
                mEditTuCao.setText(null);
                hideSoftInput(mEditTuCao);
                break;
        }
    }

    /**
     * 前往用户界面
     */
    private void gotoUser(User user) {
        Intent intent = new Intent(this, UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASSED_USER_INFO, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 调用输入法
     */
    public void openSofInput(EditText edit) {
        edit.setText(null);
        edit.requestFocus();
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏输入法
     */
    public void hideSoftInput(EditText edit) {
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
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
            boolean isEdit = mEditTuCao.getText().length() > 0;
            mBtnPublish.setClickable(isEdit);
            mBtnPublish.setSelected(isEdit);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
