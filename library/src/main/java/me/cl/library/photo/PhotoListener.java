package me.cl.library.photo;

import android.net.Uri;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/26
 * desc   : 图片相关事件
 * version: 1.0
 */
public interface PhotoListener {

    interface OnPhotoClickListener {
        void onClick(FragmentActivity activity, ImageView view, int position, String url);
    }

    interface OnPhotoLongClickListener {
        void onLongClick(FragmentActivity activity, ImageView view, int position, String url);
    }

    interface OnClickListener {
        void onClick(View view, int position, String url);
    }

    interface OnLongClickListener {
        void onLongClick(View view, int position, String url);
    }

    interface OnMoveOutListener {
        void onMoveOut();
    }

    interface OnPageChangeListener {
        void onPageSelected(int position);
    }

    interface OnDownLoadListener {
        void onSuccess(Uri uri);
        void onFailed(@StringRes int message);
    }
}
