package me.cl.lingxi.module.member;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.entity.UserInfo;
import okhttp3.Call;

/**
 * 用户注册
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.do_password)
    EditText mDoPassword;
    @BindView(R.id.phone)
    EditText mPhone;

    private LoadingDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registe_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_reg)
                .setBack()
                .setTitleCenter()
                .build();

        registerProgress = new LoadingDialog(this, R.string.dialog_loading_reg);
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
        postRegister(uName, uPwd, uPhone);
    }

    /**
     * 验证手机
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[3-9]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 注册请求
     */
    public void postRegister(String userName, String userPwd, String phone) {
        OkUtil.post()
                .url(Api.userRegister)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("phone", phone)
                .setProgressDialog(registerProgress)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case "00000":
                                showToast(R.string.toast_reg_success);
                                UserInfo user = response.getData();
                                SPUtil.build().putString(Constants.SP_USER_NAME, user.getUsername());
                                onBackPressed();
                                break;
                            case "00105":
                                showToast(R.string.toast_phone_being);
                                break;
                            case "00106":
                                showToast(R.string.toast_username_being);
                                break;
                            default:
                                showToast(R.string.toast_reg_error);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast(R.string.toast_reg_error);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (registerProgress.isShowing()) {
            registerProgress.dismiss();
        }
        super.onDestroy();
    }
}
