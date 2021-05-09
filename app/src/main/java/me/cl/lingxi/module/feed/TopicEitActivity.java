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
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.databinding.TopicEitActivityBinding;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.Topic;
import me.cl.lingxi.entity.User;
import okhttp3.Call;

/**
 * @author : happyc
 * time    : 2020/11/05
 * desc    :
 * version : 1.0
 */
public class TopicEitActivity extends BaseActivity implements View.OnClickListener {

    private TopicEitActivityBinding mActivityBinding;

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
    private ArrayList<String> mMsgList = new ArrayList<>();
    private TopicEitAdapter mAdapter;

    private int mPage = 1;
    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;


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

    private void init() {
        mEditSearch = mActivityBinding.editSearch;
        mSwipeRefreshLayout = mActivityBinding.swipeRefreshLayout;
        mRecyclerView = mActivityBinding.recyclerView;
        mBtnNegative = mActivityBinding.btnNegative;
        mFloatButton = mActivityBinding.floatButton;

        mBtnNegative.setOnClickListener(this);
        mFloatButton.setOnClickListener(this);

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                mPage = 1;
                doSearch(queryName);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.VERTICAL, 2, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemDecoration.setGoneLast(true);
        // mRecyclerView.addItemDecoration(itemDecoration);
        mAdapter = new TopicEitAdapter(new ArrayList<>(), mType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemListener(new TopicEitAdapter.OnItemListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(View view, Topic future) {
                if (mAdapter.isSelected()) {
                    mFloatButton.setVisibility(View.VISIBLE);
                } else {
                    mFloatButton.setVisibility(View.GONE);
                }
            }
        });
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
        RefreshMODE = MOD_REFRESH;
        mPage = 1;
        queryName = searchStr;
        if (mType == Type.EIT) {
            getUserList(mPage, mCount);
        }
        if (mType == Type.TOPIC) {
            getTopicList(mPage, mCount);
        }
    }

    // 获取用户
    private void getUserList(int pageNum, int pageSize) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        OkUtil.post()
                .url(Api.queryUser)
                .addParam("username", queryName)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<User>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<User>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast(R.string.toast_get_user_error);
                            return;
                        }
                        mPage++;
                        PageInfo<User> page = response.getData();
                        List<User> users = page.getList();
                        List<Topic> list = new ArrayList<>();
                        for (User user: users) {
                            Topic topic = new Topic();
                            topic.setId(user.getId());
                            topic.setTopicName(user.getUsername());
                            topic.setAvatar(user.getAvatar());
                            topic.setSelected(false);
                            list.add(topic);
                        }
                        if (RefreshMODE == MOD_LOADING) {
                            mAdapter.addData(list);
                        } else {
                            mAdapter.setData(list);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        showToast(R.string.toast_get_user_error);
                    }
                });
    }

    // 获取话题
    private void getTopicList(int pageNum, int pageSize) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        OkUtil.post()
                .url(Api.queryTopic)
                .addParam("name", queryName)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Topic>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Topic>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast(R.string.toast_get_topic_error);
                            return;
                        }
                        mPage++;
                        PageInfo<Topic> page = response.getData();
                        List<Topic> list = page.getList();
                        if (RefreshMODE == MOD_LOADING) {
                            mAdapter.addData(list);
                        } else {
                            mAdapter.setData(list);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        showToast(R.string.toast_get_topic_error);
                    }
                });
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
