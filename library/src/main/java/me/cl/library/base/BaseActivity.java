package me.cl.library.base;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import me.cl.library.R;
import me.cl.library.util.ToastUtil;
import me.cl.library.view.LoadingDialog;
import me.cl.library.view.MoeToast;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "lcDev";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemUiLightStatus();
    }

    // 加载动画开始
    private LoadingDialog mLoading;

    public void setLoading(){
        setLoading(getString(R.string.lib_dialog_loading));
    }

    public void setLoading(@StringRes int msgId){
        setLoading(getString(msgId));
    }

    public void setLoading(CharSequence msg){
        mLoading = new LoadingDialog(this, msg);
    }

    public void showLoading() {
        if (mLoading == null) return;
        if (mLoading.isShowing()) return;
        mLoading.show();
    }

    public void dismissLoading() {
        if (mLoading == null) return;
        if (mLoading.isShowing()) mLoading.dismiss();
    }
    // 加载动画结束

    public void showToast(@StringRes int msgId) {
        ToastUtil.showToast(this, msgId);
    }

    public void showToast(String msg) {
        ToastUtil.showToast(this, msg);
    }

    public void showMoeToast(@StringRes int msgId) {
        MoeToast.makeText(this, msgId);
    }

    public void showMoeToast(String msg) {
        MoeToast.makeText(this, msg);
    }

    /**
     * 切换全屏，屏幕常量
     * @param fullscreen 是否全屏
     */
    public void setFullscreen(boolean fullscreen) {
        if (fullscreen) {
            // 设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // 隐藏状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 常亮
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            // 设置竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // 显示状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 清除常亮
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 设置状态栏文字深色，同时保留之前的flag
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void setSystemUiLightStatus() {
        View decorView = getWindow().getDecorView();
        int originFlag = decorView.getSystemUiVisibility();
        originFlag = originFlag | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(originFlag);
    }

    /**
     * 清除状态栏文字深色，同时保留之前的flag
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void clearSystemUiLightStatus() {
        View decorView = getWindow().getDecorView();
        int originFlag = decorView.getSystemUiVisibility();
        // 使用异或清除SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        originFlag = originFlag ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(originFlag);
    }
}
