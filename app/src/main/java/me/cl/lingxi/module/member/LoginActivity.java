package me.cl.lingxi.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.library.view.MoeToast;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.LoginActivityBinding;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.viewmodel.UserViewModel;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private LoginActivityBinding mBinding;
    private UserViewModel mUserViewModel;

    private long mExitTime = 0;
    private LoadingDialog loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        ToolbarUtil.init(mBinding.includeTb.toolbar, this)
                .setTitle(R.string.title_bar_login)
                .setTitleCenter()
                .build();

        loginProgress = new LoadingDialog(this, R.string.dialog_loading_login);

        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) MoeToast.makeText(this, R.string.egg_from_where);

        String saveName = SPUtil.build().getString(Constants.SP_USER_NAME);
        mBinding.username.setText(saveName);
        mBinding.username.setSelection(saveName.length());

        initViewModel();
    }

    private void initViewModel() {
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.mTipMessage.observe(this, tipMessage -> {
            if (tipMessage.isRes()) {
                showToast(tipMessage.getMsgId());
            } else {
                showToast(tipMessage.getMsgStr());
            }
        });
        mUserViewModel.mUserToken.observe(this, userToken -> {
            SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, true);
            SPUtil.build().putString(Constants.SP_USER_ID, userToken.getId());
            SPUtil.build().putString(Constants.SP_USER_NAME, userToken.getUsername());
            SPUtil.build().putString(Api.X_APP_TOKEN, userToken.getToken());
            OkUtil.newInstance().addCommonHeader(Api.X_APP_TOKEN, userToken.getToken());
            goHome();
        });
    }

    public void login(View view) {
        String username = mBinding.username.getText().toString().trim();
        String password = mBinding.password.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast(R.string.toast_login_null);
            return;
        }
        mUserViewModel.doLogin(username, password, loginProgress);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, R.string.toast_again_exit);
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void updatePwd(View view) {
        Intent intent = new Intent(this, ResetPwdActivity.class);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.PASSED_UNREAD_NUM, 0);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (loginProgress.isShowing()) {
            loginProgress.dismiss();
        }
        super.onDestroy();
    }
}
