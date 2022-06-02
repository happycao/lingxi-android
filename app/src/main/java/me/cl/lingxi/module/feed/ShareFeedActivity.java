package me.cl.lingxi.module.feed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import me.cl.library.base.BaseActivity;
import me.cl.library.model.TipMessage;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.FeedContentUtil;
import me.cl.lingxi.databinding.PublishActivityBinding;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.viewmodel.FeedViewModel;

/**
 * 动态发布
 */
public class ShareFeedActivity extends BaseActivity {

    private PublishActivityBinding mBinding;
    private FeedViewModel mFeedViewModel;

    private final StringBuffer mInfo = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = PublishActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        ToolbarUtil.init(mBinding.includeTb.toolbar, this)
                .setTitle(R.string.share_text)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .setMenu(R.menu.send_menu, item -> {
                    if (item.getItemId() == R.id.action_send) {
                        setLoading();
                        mFeedViewModel.saveFeed(mInfo.toString(), null);
                    }
                    return false;
                })
                .build();

        setLoading("发布中...");
        mBinding.llAction.setVisibility(View.GONE);
        mBinding.recyclerView.setVisibility(View.GONE);

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
        mBinding.feedInfo.setEnabled(false);
        mBinding.feedInfo.setText(FeedContentUtil.getFeedText(mInfo.toString(), mBinding.feedInfo));

        // 预留
        int i = text.indexOf("http");
        String url = text.substring(i);
        Log.d(TAG, "init: title" + title);
        Log.d(TAG, "init: url" + url);

        initViewModel();
    }

    private void initViewModel() {
        mFeedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        mFeedViewModel.mTipMessage.observe(this, this::showTip);
        mFeedViewModel.mFeed.observe(this, feed -> {
            dismissLoading();
            showSuccess();
        });
    }

    @Override
    protected void showTip(TipMessage tipMessage) {
        dismissLoading();
        super.showTip(tipMessage);
    }

    // 发布成功
    private void showSuccess() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setMessage("发布成功，是否留在本APP");
        mDialog.setNegativeButton("离开", (dialog, which) -> onBackPressed());
        mDialog.setPositiveButton("留下", (dialog, which) -> gotoHome());
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
