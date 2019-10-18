package me.cl.library.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import android.widget.ImageView;
import android.widget.TextView;

import me.cl.library.R;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/02/24
 * desc   : 加载动画
 * version: 1.0
 */
public class LoadingDialog extends ProgressDialog {

    private Context mContext;
    private AnimationDrawable mAnimation;
    private ImageView mImageView;
    private CharSequence mMessage;
    private TextView mMessageView;
    private int mResId;

    public LoadingDialog(Context context, @StringRes int tipId) {
        this(context, context.getResources().getString(tipId));
    }

    public LoadingDialog(Context context, CharSequence tip) {
        this(context, R.style.Lib_Dialog, tip, R.drawable.loading_frame);
    }

    public LoadingDialog(Context context, @StyleRes int theme, CharSequence tip, int id) {
        super(context, theme);
        this.mContext = context;
        this.mMessage = tip;
        this.mResId = id;
        setCanceledOnTouchOutside(true);
    }

    public void setMessage(@StringRes int msgId) {
        setMessage(mContext.getResources().getString(msgId));
    }

    public void setMessage(CharSequence msg) {
        mMessage = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_dialog_progress);
        init();
    }

    private void init() {
        mMessageView = findViewById(R.id.lib_loading_text);
        mImageView = findViewById(R.id.lib_loading_img);
        mImageView.setBackgroundResource(mResId);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
        mAnimation = (AnimationDrawable) mImageView.getBackground();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();
            }
        });
        mMessageView.setText(mMessage);
    }

    @Override
    public void dismiss() {
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                if (mAnimation.isRunning()) mAnimation.stop();
            }
        });
        super.dismiss();
    }
}