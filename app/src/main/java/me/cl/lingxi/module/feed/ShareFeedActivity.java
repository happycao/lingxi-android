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
import androidx.recyclerview.widget.RecyclerView;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.FeedContentUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.PublishActivityBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.module.main.MainActivity;
import okhttp3.Call;

/**
 * 动态发布
 */
public class ShareFeedActivity extends BaseActivity {

    private PublishActivityBinding mActivityBinding;

    private AppCompatEditText mFeedInfo;

    private String mUid;
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

        ToolbarUtil.init(toolbar, this)
                .setTitle(R.string.share_text)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .setMenu(R.menu.send_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_send:
                                postSaveFeed();
                                break;
                        }
                        return false;
                    }
                })
                .build();

        mUid = SPUtil.build().getString(Constants.SP_USER_ID);
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
        mFeedInfo.setEnabled(false);
        mFeedInfo.setText(FeedContentUtil.getFeedText(mInfo.toString(), mFeedInfo));

        // 预留
        int i = text.indexOf("http");
        String url = text.substring(i);
        Log.d(TAG, "init: title" + title);
        Log.d(TAG, "init: url" + url);
    }

    // 发布动态
    private void postSaveFeed() {
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("userId", mUid)
                .addParam("feedInfo", mInfo.toString())
                .execute(new ResultCallback<Result<Feed>>() {
                    @Override
                    public void onSuccess(Result<Feed> response) {
                        dismissLoading();
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("发布失败");
                            return;
                        }
                        showSuccess();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        dismissLoading();
                        showToast("发布失败");
                    }
                });
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
