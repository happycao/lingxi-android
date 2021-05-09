package me.cl.lingxi.module.setting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.MoeToast;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.AboutActivityBinding;
import me.cl.lingxi.entity.AppVersion;
import me.cl.lingxi.module.webview.WebActivity;
import okhttp3.Call;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private AboutActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = AboutActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mVersion = mActivityBinding.version;

        mActivityBinding.appUpdate.setOnClickListener(this);
        mActivityBinding.feedback.setOnClickListener(this);
        mActivityBinding.publicLicense.setOnClickListener(this);
        mActivityBinding.learnMore.setOnClickListener(this);

        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_about)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();


        int x = (int) (Math.random() * 3) + 1;
        if (x == 1) {
            MoeToast.makeText(this, R.string.egg_hidden_secrets);
        }

        String versionName = "V " + Utils.getAppVersionName(this);
        mVersion.setText(versionName);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.app_update:
                getAppVersion();
                break;
            case R.id.feedback:
                boolean isWpa = Utils.wpaQQ(this, "986417980");
                if (!isWpa) {
                    showToast("未安装手Q或安装的版本不支持");
                }
                break;
            case R.id.public_license:
                gotoPublicLicense();
                break;
            case R.id.learn_more:
                gotoWeb("前世今生", "file:///android_asset/about.html");
                break;
        }
    }

    // 获取版本信息
    public void getAppVersion() {
        OkUtil.post()
                .url(Api.latestVersion)
                .execute(new ResultCallback<Result<AppVersion>>() {
                    @Override
                    public void onSuccess(Result<AppVersion> response) {
                        String code = response.getCode();
                        AppVersion data = response.getData();
                        if (ResultConstant.CODE_SUCCESS.equals(code) && data != null) {
                            int versionCode = Utils.getAppVersionCode(AboutActivity.this);
                            if (versionCode >= data.getVersionCode()) {
                                showToast( "已是最新版");
                            } else {
                                showUpdate(data);
                            }
                        } else {
                            showToast("版本信息获取失败");
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("版本信息获取失败");
                    }
                });

    }

    private void gotoPublicLicense() {
        Intent intent = new Intent(this, PublicLicenseActivity.class);
        startActivity(intent);
    }

    // 展示更新弹窗
    private void showUpdate(final AppVersion appVersion) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("发现新版本");
        mDialog.setMessage(appVersion.getUpdateInfo());
        if (appVersion.getUpdateFlag() != 2) {
            mDialog.setNegativeButton("取消", null);
        }
        mDialog.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDownload(appVersion.getApkUrl());
            }
        }).setCancelable(false).create().show();
    }

    // 调起浏览器下载
    private void gotoDownload(String url){
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    // 前往web页
    private void gotoWeb(String tittle, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
