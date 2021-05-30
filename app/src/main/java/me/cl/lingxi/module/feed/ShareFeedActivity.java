package me.cl.lingxi.module.feed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.common.util.FeedContentUtil;
import me.cl.lingxi.databinding.PublishActivityBinding;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;

/**
 * 动态发布
 */
public class ShareFeedActivity extends BaseActivity {

    private PublishActivityBinding mActivityBinding;
    private FeedViewModel mFeedViewModel;

    private final StringBuffer mInfo = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = PublishActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        Toolbar toolbar = mActivityBinding.includeToolbar.toolbar;
        RecyclerView recyclerView = mActivityBinding.recyclerView;
        LinearLayout llAction = mActivityBinding.llAction;
        AppCompatEditText feedInfo = mActivityBinding.feedInfo;

        ToolbarUtil.init(toolbar, this)
                .setTitle(R.string.share_text)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .setMenu(R.menu.send_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_send) {
                            setLoading();
                            mFeedViewModel.saveFeed(mInfo.toString(), null);
                        }
                        return false;
                    }
                })
                .build();

        setLoading("发布中...");
        llAction.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent == null) {
            onBackPressed();
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            onBackPressed();
            return;
        }
        if (!"text/plain".equals(intent.getType())) {
            onBackPressed();
            return;
        }
        String title = bundle.getString(Intent.EXTRA_TITLE);
        String text = bundle.getString(Intent.EXTRA_TEXT);
        if (TextUtils.isEmpty(text)) {
            onBackPressed();
            return;
        }

        if (text.contains("bilibili")) {
            mInfo.append("#bilibili#");
        }

        if (text.contains("music.163")) {
            mInfo.append("#网易云音乐#");
        }

        mInfo.append(text);
        feedInfo.setEnabled(false);
        feedInfo.setText(FeedContentUtil.getFeedText(mInfo.toString(), feedInfo));

        // 预留
        int i = text.indexOf("http");
        String url = text.substring(i);
        Log.d(TAG, "init: title" + title);
        Log.d(TAG, "init: url" + url);

        initViewModel();
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.getTipMessage().observe(this, this::showTip);
        mFeedViewModel.getFeed().observe(this, feed -> {
            dismissLoading();
            showSuccess();
        });
    }

    // 提示
    private void showTip(TipMessage tipMessage) {
        dismissLoading();
        if (tipMessage.isRes()) {
            showToast(tipMessage.getMsgId());
        } else {
            showToast(tipMessage.getMsgStr());
        }
    }

    // 发布成功
    private void showSuccess() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setMessage("发布成功，是否留在本APP");
        mDialog.setNegativeButton("离开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        mDialog.setPositiveButton("留下", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoHome();
            }
        });
        mDialog.setCancelable(false).create().show();
    }

    public void gotoHome() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, R.id.navigation_camera);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
