package me.cl.lingxi.module.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.recycle.ItemDecoration;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.TopicEitAdapter;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.databinding.TopicEitActivityBinding;
import me.cl.lingxi.entity.Topic;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.viewmodel.TopicViewModel;
import me.cl.lingxi.viewmodel.UserViewModel;

/**
 * @author : happyc
 * time    : 2020/11/05
 * desc    :
 * version : 1.0
 */
public class TopicEitActivity extends BaseActivity implements View.OnClickListener {

    private TopicEitActivityBinding mActivityBinding;
    private UserViewModel mUserViewModel;
    private TopicViewModel mTopicViewModel;

    public static final int REQUEST_CODE = 2233;

    public static final String TYPE = "type";
    public static final String MSG = "msg";

    private ImageButton mBtnNegative;
    private AppCompatEditText mEditSearch;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFloatButton;

    private Type mType = Type.TOPIC;
    private String queryName = "";
    private final ArrayList<String> mMsgList = new ArrayList<>();
    private TopicEitAdapter mAdapter;

    private int mPageNum = 1;
    private static final int PAGE_SIZE = 10;

    public enum Type implements Serializable {
        TOPIC,
        EIT
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = TopicEitActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        Intent intent = getIntent();
        mType = (Type) intent.getSerializableExtra(TYPE);
        init();
    }

    @SuppressLint("RestrictedApi")
    private void init() {
        mEditSearch = mActivityBinding.editSearch;
        mSwipeRefreshLayout = mActivityBinding.swipeRefreshLayout;
        mRecyclerView = mActivityBinding.recyclerView;
        mBtnNegative = mActivityBinding.btnNegative;
        mFloatButton = mActivityBinding.floatButton;

        initListener();
        initRecyclerView();
        initViewModel();
    }

    private void initListener() {
        mBtnNegative.setOnClickListener(this);
        mFloatButton.setOnClickListener(this);

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPageNum = 1;
                doSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPageNum = 1;
            doSearch(queryName);
        });
    }

    @SuppressLint("RestrictedApi")
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.VERTICAL, 2, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemDecoration.setGoneLast(true);
        // mRecyclerView.addItemDecoration(itemDecoration);
        mAdapter = new TopicEitAdapter(new ArrayList<>(), mType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemListener((view, future) -> {
            if (mAdapter.isSelected()) {
                mFloatButton.setVisibility(View.VISIBLE);
            } else {
                mFloatButton.setVisibility(View.GONE);
            }
        });
    }

    private void initViewModel() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        mUserViewModel = viewModelProvider.get(UserViewModel.class);
        mTopicViewModel = viewModelProvider.get(TopicViewModel.class);
        mUserViewModel.getTipMessage().observe(this, this::showTip);
        mTopicViewModel.getTipMessage().observe(this, this::showTip);
        mUserViewModel.getUsers().observe(this, userPageInfo -> {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            Integer pageNum = userPageInfo.getPageNum();
            mPageNum = pageNum + 1;
            List<User> users = userPageInfo.getList();
            List<Topic> list = new ArrayList<>();
            for (User user: users) {
                Topic topic = new Topic();
                topic.setId(user.getId());
                topic.setTopicName(user.getUsername());
                topic.setAvatar(user.getAvatar());
                topic.setSelected(false);
                list.add(topic);
            }
            if (pageNum == 1) {
                mAdapter.setData(list);
            } else {
                mAdapter.addData(list);
            }
        });
        mTopicViewModel.getTopics().observe(this, topicPageInfo -> {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            Integer pageNum = topicPageInfo.getPageNum();
            mPageNum = pageNum + 1;
            List<Topic> list = topicPageInfo.getList();
            if (pageNum == 1) {
                mAdapter.setData(list);
            } else {
                mAdapter.addData(list);
            }
        });
    }

    private void showTip(TipMessage tipMessage) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (tipMessage.isRes()) {
            showToast(tipMessage.getMsgId());
        } else {
            showToast(tipMessage.getMsgStr());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_negative:
                mMsgList.clear();
                onBackPressed();
                break;
            case R.id.float_button:
                mMsgList.clear();
                for (Topic topic : mAdapter.getData()) {
                    if (topic.isSelected()) {
                        mMsgList.add(topic.getTopicName());
                    }
                }
                onBackPressed();
                break;
        }
    }

    void doSearch(String searchStr) {
        mPageNum = 1;
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        queryName = searchStr;
        if (mType == Type.EIT) {
            mUserViewModel.queryUser(queryName, mPageNum, PAGE_SIZE);
        }
        if (mType == Type.TOPIC) {
            mTopicViewModel.queryTopic(queryName, mPageNum, PAGE_SIZE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(TYPE, mType);
        intent.putStringArrayListExtra(MSG, mMsgList);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
