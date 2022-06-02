package me.cl.lingxi.module.search;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Collections;
import java.util.List;

import me.cl.library.base.BaseActivity;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.recycle.ItemDecoration;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.UserInfoAdapter;
import me.cl.lingxi.databinding.SearchActivityBinding;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.module.member.UserActivity;
import me.cl.lingxi.viewmodel.UserViewModel;

public class SearchActivity extends BaseActivity {

    private SearchActivityBinding mBinding;
    private UserViewModel mUserViewModel;
    private SearchView mSearchView;
    private UserInfoAdapter mUserInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = SearchActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        ToolbarUtil.init(mBinding.toolbar, this)
                .setMenu(R.menu.search_view_menu, null)
                .setBack()
                .build();
        Menu menu = mBinding.toolbar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        initViewModel();
        initSearchView();
        initRecyclerView();
        initData();
    }

    private void initViewModel() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        mUserViewModel = viewModelProvider.get(UserViewModel.class);
        mUserViewModel.mTipMessage.observe(this, this::showTip);
        mUserViewModel.mUsers.observe(this, userPageInfo -> {
            List<User> users = userPageInfo.getList();
            mUserInfoAdapter.setData(users);
        });
    }

    private void initSearchView() {
        // 当展开无输入内容的时候，没有关闭的图标
        mSearchView.onActionViewExpanded();
        // 显示隐藏提交按钮
        mSearchView.setSubmitButtonEnabled(true);
        // 搜索确认图标
        AppCompatImageView searchGo = mSearchView.findViewById(R.id.search_go_btn);
        searchGo.setImageResource(R.drawable.ic_search);

        // 事件
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mUserViewModel.queryUser(query, 1, 20);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.VERTICAL, 2, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemDecoration.setGoneLast(true);
        mUserInfoAdapter = new UserInfoAdapter(Collections.emptyList());
        mBinding.recyclerView.setAdapter(mUserInfoAdapter);
        mUserInfoAdapter.setOnItemListener((view, item) -> UserActivity.gotoUser(SearchActivity.this, item));
    }

    private void initData() {

    }

    /**
     * 设置异常提示
     */
    private void setError() {
        showToast(R.string.toast_search_none);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
