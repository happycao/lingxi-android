package me.cl.lingxi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.entity.UserToken;
import okhttp3.Call;

/**
 * @author : happyc
 * time    : 2021/05/10
 * desc    :
 * version : 1.0
 */
public class UserViewModel extends ViewModel {

    private final MutableLiveData<TipMessage> mTipMessage;
    private final MutableLiveData<UserToken> mUserToken;
    private final MutableLiveData<UserInfo> mUserInfo;
    private final MutableLiveData<PageInfo<User>> mUsers;

    public UserViewModel() {
        mTipMessage = new MutableLiveData<>();
        mUserToken = new MutableLiveData<>();
        mUserInfo = new MutableLiveData<>();
        mUsers = new MutableLiveData<>();
    }

    public LiveData<TipMessage> getTipMessage() {
        return mTipMessage;
    }

    public LiveData<UserToken> getUserToken() {
        return mUserToken;
    }

    public LiveData<UserInfo> getUserInfo() {
        return mUserInfo;
    }

    public LiveData<PageInfo<User>> getUsers() {
        return mUsers;
    }

    /**
     * 登录
     * @param userName 用户名
     * @param userPwd 密码
     * @param loadingDialog 加载动画
     */
    public void doLogin(String userName, String userPwd, LoadingDialog loadingDialog) {
        OkUtil.post()
                .url(Api.userLogin)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .setProgressDialog(loadingDialog)
                .setLoadDelay()
                .execute(new ResultCallback<Result<UserToken>>() {
                    @Override
                    public void onSuccess(Result<UserToken> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mUserToken.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.resId(R.string.toast_pwd_error));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_login_error));
                    }
                });
    }

    /**
     * 注册
     * @param userName 用户名
     * @param userPwd 密码
     * @param phone 手机号
     * @param loadingDialog 加载动画
     */
    public void doRegister(String userName, String userPwd, String phone, LoadingDialog loadingDialog) {
        OkUtil.post()
                .url(Api.userRegister)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("phone", phone)
                .setProgressDialog(loadingDialog)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case ResultConstant.CODE_SUCCESS:
                                mUserInfo.postValue(response.getData());
                                break;
                            case "00105":
                                mTipMessage.postValue(TipMessage.resId(R.string.toast_phone_being));
                                break;
                            case "00106":
                                mTipMessage.postValue(TipMessage.resId(R.string.toast_username_being));
                                break;
                            default:
                                mTipMessage.postValue(TipMessage.resId(R.string.toast_reg_error));
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_reg_error));
                    }
                });
    }

    /**
     * 重置密码
     * @param userName 用户名
     * @param userPwd 密码
     * @param phone 手机号
     * @param loadingDialog 加载动画
     */
    public void doResetPwd(String userName, String userPwd, String phone, LoadingDialog loadingDialog) {
        OkUtil.post()
                .url(Api.resetPassword)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("phone", phone)
                .setProgressDialog(loadingDialog)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case ResultConstant.CODE_SUCCESS:
                                mUserInfo.postValue(response.getData());
                                break;
                            case "00104":
                                mTipMessage.postValue(TipMessage.resId(R.string.toast_reset_pwd_user));
                                break;
                            default:
                                mTipMessage.postValue(TipMessage.resId(R.string.toast_reset_pwd_error));
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_reset_pwd_error));
                    }
                });
    }

    /**
     * 获取用户信息
     */
    public void doUserInfo() {
        this.doUserInfo(null);
    }

    /**
     * 获取用户信息
     * @param userId 用户id
     */
    public void doUserInfo(String userId) {
        OkUtil.post()
                .url(Api.userInfo)
                .addParam("id", userId)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mUserInfo.postValue(response.getData());
                        } else {
                            mUserInfo.postValue(null);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mUserInfo.postValue(null);
                    }
                });
    }

    /**
     * 通过用户名精准搜索用户
     * @param username 用户名
     */
    public void searchUser(String username) {
        OkUtil.post()
                .url(Api.searchUser)
                .addParam("username", username)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        mUserInfo.postValue(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mUserInfo.postValue(null);
                    }
                });
    }

    /**
     * 查询用户
     * @param queryName 搜索用户名
     * @param pageNum 页码
     * @param pageSize 页容量
     */
    public void queryUser(String queryName, int pageNum, int pageSize) {
        OkUtil.post()
            .url(Api.queryUser)
            .addParam("username", queryName)
            .addParam("pageNum", pageNum)
            .addParam("pageSize", pageSize)
            .execute(new ResultCallback<Result<PageInfo<User>>>() {
                @Override
                public void onSuccess(Result<PageInfo<User>> response) {
                    String code = response.getCode();
                    if (ResultConstant.CODE_SUCCESS.equals(code)) {
                        mUsers.postValue(response.getData());
                    } else {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_get_user_error));
                    }
                }

                @Override
                public void onError(Call call, Exception e) {
                    mTipMessage.postValue(TipMessage.resId(R.string.toast_get_user_error));
                }
            });
    }
}
