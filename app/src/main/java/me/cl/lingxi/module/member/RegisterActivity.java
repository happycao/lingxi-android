package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.RegisteActivityBinding;
import me.cl.lingxi.viewmodel.UserViewModel;

/**
 * 用户注册
 */
public class RegisterActivity extends BaseActivity {

    private RegisteActivityBinding mActivityBinding;
    private UserViewModel mUserViewModel;

    private EditText mUsername;
    private EditText mPassword;
    private EditText mDoPassword;
    private EditText mPhone;

    private LoadingDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = RegisteActivityBinding.inflate(getLayoutInflater());
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
                .setTitle(R.string.title_bar_reg)
                .setBack()
                .setTitleCenter()
                .build();

        registerProgress = new LoadingDialog(this, R.string.dialog_loading_reg);

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
            showToast(R.string.toast_reg_success);
            SPUtil.build().putString(Constants.SP_USER_NAME, userInfo.getUsername());
            onBackPressed();
        });
    }

    public void goRegister(View view) {
        String uName = mUsername.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            showToast(R.string.toast_reg_null);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }
        if (uPhone.length() != 11) {
            showToast(R.string.toast_phone_format_error);
            return;
        }
        if (!isMobileNum(uPhone)) {
            showToast(R.string.toast_phone_format_error);
            return;
        }

        mUserViewModel.doRegister(uName, uPwd, uPhone, registerProgress);
    }

    /**
     * 验证手机
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[3-9]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    @Override
    protected void onDestroy() {
        if (registerProgress.isShowing()) {
            registerProgress.dismiss();
        }
        super.onDestroy();
    }
}
