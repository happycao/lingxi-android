package me.cl.lingxi.module.search;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setMenu(R.menu.search_view_menu, null)
                .setBack()
                .build();
        Menu menu = mToolbar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        initSearchView();
        initRecyclerView();
        initData();
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
                showToast(R.string.toast_feature_dev);
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
