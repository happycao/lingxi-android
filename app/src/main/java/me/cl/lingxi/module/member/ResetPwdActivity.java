package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.databinding.ResetpwdActivityBinding;
import me.cl.lingxi.viewmodel.UserViewModel;

/**
 * 忘记密码重置
 */
public class ResetPwdActivity extends BaseActivity {

    private ResetpwdActivityBinding mActivityBinding;
    private UserViewModel mUserViewModel;

    private EditText mUsername;
    private EditText mPhone;
    private EditText mPassword;
    private EditText mDoPassword;

    private LoadingDialog updateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = ResetpwdActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        Toolbar toolbar = mActivityBinding.includeToolbar.toolbar;
        mUsername = mActivityBinding.username;
        mPassword = mActivityBinding.password;
        mDoPassword = mActivityBinding.doPassword;
        mPhone = mActivityBinding.phone;

        ToolbarUtil.init(toolbar, this)
                .setTitle(R.string.title_bar_reset_pwd)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        updateProgress = new LoadingDialog(this, R.string.dialog_loading_reset_wd);

        initViewModel();
    }

    private void initViewModel() {
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.getTipMessage().observe(this, tipMessage -> {
            if (tipMessage.isRes()) {
                showToast(tipMessage.getMsgId());
            } else {
                showToast(tipMessage.getMsgStr());
            }
        });
        mUserViewModel.getUserInfo().observe(this, userInfo -> {
            showToast(R.string.toast_reset_ped_success);
            onBackPressed();
        });
    }

    public void goUpdatePwd(View view) {
        String uName = mUsername.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            showToast(R.string.toast_reg_null);
        }
        if (uPhone.length() != 11) {
            showToast(R.string.toast_phone_format_error);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }

        mUserViewModel.doResetPwd(uName, uPwd, uPhone, updateProgress);
    }

    @Override
    protected void onDestroy() {
        if (updateProgress.isShowing()) {
            updateProgress.dismiss();
        }
        super.onDestroy();
    }
}
