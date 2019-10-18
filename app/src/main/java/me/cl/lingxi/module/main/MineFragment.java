package me.cl.lingxi.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseFragment;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.dialog.LogoutDialog;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.module.member.LoginActivity;
import me.cl.lingxi.module.mine.PersonalInfoActivity;
import me.cl.lingxi.module.mine.RelevantActivity;
import me.cl.lingxi.module.setting.AboutActivity;
import me.cl.lingxi.module.setting.SettingsActivity;
import okhttp3.Call;

/**
 * 我的界面
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_img)
    ImageView mUserImg;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.user_description)
    TextView mUserDescription;
    @BindView(R.id.user_body)
    LinearLayout mUserBody;
    @BindView(R.id.mine_top)
    RelativeLayout mMineTop;
    @BindView(R.id.action_reply)
    TextView mMineReply;
    @BindView(R.id.action_relevant)
    TextView mMineRelevant;
    @BindView(R.id.action_setting)
    TextView mMineSetting;
    @BindView(R.id.action_about)
    TextView mMineAbout;
    @BindView(R.id.action_sign_out)
    TextView mMineSignOut;

    private String mUserId;
    private OperateBroadcastReceiver receiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment, container, false);
        ButterKnife.bind(this, view);
        init(view);
        initReceiver();
        return view;
    }

    private void init(View view) {
        ToolbarUtil.init(mToolbar, getActivity())
                .setTitle(R.string.nav_mine)
                .setTitleCenter()
                .build();

        mUserId = SPUtil.build().getString(Constants.SP_USER_ID);
        // 获取用户信息
        postUserInfo(mUserId);
    }


    /**
     * 广播接收者
     * 用于更新用户信息
     */
    private final class OperateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.UPDATE_USER_IMG:
                        postUserInfo(mUserId);
                        break;
                }
            }
        }
    }

    private void initReceiver() {
        receiver = new OperateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_USER_IMG);
        Objects.requireNonNull(getActivity()).registerReceiver(receiver, filter);
    }

    private void postUserInfo(String id) {
        OkUtil.post()
                .url(Api.userInfo)
                .addParam("id", id)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        initUser(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        initUser(null);
                    }
                });
    }

    private void initUser(UserInfo userInfo) {
        String username = getString(R.string.app_name);
        String avatar = "";
        if (userInfo != null) {
            username = userInfo.getUsername();
            avatar = userInfo.getAvatar();
        }
        mUserName.setText(username);
        ContentUtil.loadUserAvatar(mUserImg, avatar);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        ContentUtil.setMoreBadge(mMineRelevant);
        if (Constants.isRead) {
            ((MainActivity) getActivity()).goneBadge();
        }
    }

    @OnClick({R.id.user_body, R.id.action_reply, R.id.action_relevant, R.id.action_setting, R.id.action_about, R.id.action_sign_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_body:
                gotoPersonal();
                break;
            case R.id.action_reply:
                gotoRelevant(Constants.REPLY_MY);
                break;
            case R.id.action_relevant:
                gotoRelevant(Constants.REPLY_RELEVANT);
                break;
            case R.id.action_setting:
                gotoSettings();
                break;
            case R.id.action_about:
                gotoAbout();
                break;
            case R.id.action_sign_out:
                showLogoutDialog();
                break;
        }
    }

    /**
     * 展示登出Dialog
     */
    private void showLogoutDialog() {
        String tag = "logout";
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // 清除已经存在的，同样的fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.remove(fragment);
        }
        transaction.addToBackStack(null);
        // 展示dialog
        LogoutDialog logoutDialog = LogoutDialog.newInstance();
        logoutDialog.show(transaction, tag);
        logoutDialog.setLogoutListener(new LogoutDialog.LogoutListener() {
            @Override
            public void onLogout() {
                gotoLogin();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // 解决Activity has leaked window that was originally added here
        // 如果dialog存在或显示，dismiss
    }

    // 前往信息修改
    private void gotoPersonal() {
        Intent goPerson = new Intent(getActivity(), PersonalInfoActivity.class);
        startActivity(goPerson);
    }

    // 前往关于
    private void gotoAbout() {
        Intent goAbout = new Intent(getActivity(), AboutActivity.class);
        startActivity(goAbout);
    }

    private void gotoSettings() {
        Intent goSettings = new Intent(getActivity(), SettingsActivity.class);
        startActivity(goSettings);
    }

    // 前往与我相关
    private void gotoRelevant(String type) {
        Intent goRelevant = new Intent(getActivity(), RelevantActivity.class);
        goRelevant.putExtra(Constants.REPLY_TYPE, type);
        startActivity(goRelevant);
    }

    // 前往登录
    private void gotoLogin() {
        MainActivity activity = (MainActivity) getActivity();
        SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, false);
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
        if (activity != null) {
            activity.finish();
        }
    }

}
