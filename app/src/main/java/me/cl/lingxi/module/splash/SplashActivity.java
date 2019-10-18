package me.cl.lingxi.module.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.module.member.LoginActivity;
import me.cl.library.view.MoeToast;
import okhttp3.Call;

/**
 * 闪屏
 */
public class SplashActivity extends BaseActivity {

    private boolean isLogin;
    private boolean tokenNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) {
            MoeToast.makeText(this, R.string.egg_end);
        }

        isLogin = SPUtil.build().getBoolean(Constants.SP_BEEN_LOGIN);
        String token = SPUtil.build().getString(Api.X_APP_TOKEN);
        // 此版本校验token
        tokenNull = TextUtils.isEmpty(token);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin && !tokenNull) {
                    getUnRead();
                } else {
                    goLogin();
                }
            }
        }, 1500);
    }

    /**
     * 获取未读条数
     */
    public void getUnRead() {
        String userId = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.post()
                .url(Api.unreadComment)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        goHome(ResultConstant.CODE_SUCCESS.equals(response.getCode()) ? response.getData() : 0);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        goHome(0);
                    }
                });
    }

    /**
     * 前往主页
     */
    private void goHome(int num) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(Constants.PASSED_UNREAD_NUM, num);
        startActivity(intent);
        finish();
    }

    /**
     * 前往登录
     */
    private void goLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
