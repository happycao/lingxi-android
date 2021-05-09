package me.cl.lingxi.module.future;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.FutureActivityBinding;
import me.cl.lingxi.dialog.FutureDialog;
import okhttp3.Call;

/**
 * 写给未来
 */
public class FutureActivity extends BaseActivity {

    private FutureActivityBinding mActivityBinding;

    private Toolbar mToolbar;
    private AppCompatEditText mFutureInfo;

    private String futureInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = FutureActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        futureInfo = SPUtil.build().getString(Constants.SP_FUTURE_INFO);
        if (!TextUtils.isEmpty(futureInfo)) {
            mFutureInfo.setText(futureInfo);
            mFutureInfo.setSelection(futureInfo.length());
        }
    }

    /**
     * 初始化
     */
    private void init() {
        mToolbar = mActivityBinding.includeToolbar.toolbar;
        mFutureInfo = mActivityBinding.futureInfo;

        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_future)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .setMenu(R.menu.future_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_send:
                                if (TextUtils.isEmpty(futureInfo)) {
                                    showToast("没有写下任何给未来的话哟~");
                                } else {
                                    showSendDialog();
                                }
                                break;
                        }
                        return false;
                    }
                })
                .build();

        // 输入监听
        mFutureInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                futureInfo = s.toString();
                // 实时保存
                SPUtil.build().putString(Constants.SP_FUTURE_INFO, futureInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 发送配置Dialog
     */
    private void showSendDialog() {
        String tag = "sendFuture";
        // 展示dialog
        FutureDialog futureDialog = FutureDialog.newInstance();
        futureDialog.show(Utils.fragmentTransaction(getSupportFragmentManager(), tag), tag);
        futureDialog.setCancelable(false);
        futureDialog.setOnSendClickListener(this::postSaveFuture);
    }

    /**
     * 提交保存信息
     */
    private void postSaveFuture(int type, String mail, Integer startNum, Integer endNum) {
        OkUtil.post()
                .url(Api.saveFuture)
                .addParam("type", type)
                .addParam("mail", mail)
                .addParam("futureInfo", futureInfo)
                .addParam("startNum", startNum)
                .addParam("endNum", endNum)
                .execute(new ResultCallback<Result>() {
                    @Override
                    public void onSuccess(Result response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            showToast("信件进入时空隧道，等候传达");
                            SPUtil.build().putString(Constants.SP_FUTURE_INFO, null);
                            onBackPressed();
                        } else {
                            showToast("信件偏离预定轨道，请调整重试");
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("信件偏离预定轨道，请调整重试");
                    }
                });
    }
}
