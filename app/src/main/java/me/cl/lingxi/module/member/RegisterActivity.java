package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

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

    private RegisteActivityBinding mBinding;
    private UserViewModel mUserViewModel;

    private LoadingDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = RegisteActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        ToolbarUtil.init(mBinding.includeTb.toolbar, this)
                .setTitle(R.string.title_bar_reg)
                .setBack()
                .setTitleCenter()
                .build();

        registerProgress = new LoadingDialog(this, R.string.dialog_loading_reg);

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
        mUserViewModel.mUserInfo.observe(this, userInfo -> {
            showToast(R.string.toast_reg_success);
            SPUtil.build().putString(Constants.SP_USER_NAME, userInfo.getUsername());
            onBackPressed();
        });
    }

    public void goRegister(View view) {
        String uName = mBinding.username.getText().toString().trim();
        String uPwd = mBinding.password.getText().toString().trim();
        String uDoPwd = mBinding.doPassword.getText().toString().trim();
        String uPhone = mBinding.phone.getText().toString().trim();
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
