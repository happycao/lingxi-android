package me.cl.lingxi.view.webview;

import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/13
 * desc   : ChromeClient
 * version: 1.0
 */
public class MoeChromeClient extends WebChromeClient implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "MoeChromeClient";

    private FrameLayout mVideoView;
    private View mCustomView;
    private CustomViewCallback mCallback;

    private onChangedListener mOnChangedListener;

    public interface onChangedListener {
        void onFullscreen(boolean fullscreen);
    }

    public View getCustomView() {
        return mCustomView;
    }

    public MoeChromeClient(FrameLayout videoView, onChangedListener listener) {
        this.mVideoView = videoView;
        this.mOnChangedListener = listener;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.release();
        }
    }

    // 播放网络视频时全屏会被调用的方法
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        if (mOnChangedListener != null) {
            mOnChangedListener.onFullscreen(true);
        }
        mCustomView = view;
        mVideoView.addView(view);
        mVideoView.setVisibility(View.VISIBLE);
        mCallback = callback;
    }

    // 视频播放退出全屏会被调用的
    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }
        if (mOnChangedListener != null) {
            mOnChangedListener.onFullscreen(false);
        }
        mCustomView.setVisibility(View.GONE);
        mVideoView.removeView(mCustomView);
        mCustomView = null;
        mVideoView.setVisibility(View.GONE);
        mCallback.onCustomViewHidden();
    }
}