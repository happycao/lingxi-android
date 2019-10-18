package me.cl.library.util;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import me.cl.library.R;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/03/26
 * desc   : https://blog.csdn.net/longsh_/article/details/81607665
 * version: 1.0
 */
public class ToolbarUtil {

    public static Builder init(Toolbar toolbar, Activity activity) {
        return new Builder(toolbar, activity);
    }

    public static class Builder {
        private Toolbar mToolbar;
        private Activity mActivity;
        private String mTitle;

        public Builder(Toolbar toolbar, Activity activity) {
            mToolbar = toolbar;
            mActivity = activity;
        }

        /**
         * 设置Title
         */
        public Builder setTitle(@StringRes int titleId) {
            mTitle = mActivity.getString(titleId);
            mToolbar.setTitle(mTitle);
            return this;
        }

        /**
         * 设置Title
         */
        public Builder setTitle(@NonNull String title) {
            mTitle = title;
            mToolbar.setTitle(mTitle);
            return this;
        }

        /**
         * 设置返回Icon
         */
        public Builder setBack(){
            mToolbar.setNavigationIcon(R.drawable.ic_navigate);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onBackPressed();
                }
            });
            return this;
        }

        /**
         * 设置菜单
         * @param menuId menu资源id
         * @param listener 事件
         */
        public Builder setMenu(@MenuRes int menuId, Toolbar.OnMenuItemClickListener listener) {
            mToolbar.inflateMenu(menuId);
            if (listener != null) {
                mToolbar.setOnMenuItemClickListener(listener);
            }
            return this;
        }

        /**
         * Title居中，适合没有NavigationIcon，如果有title会偏移
         */
        public Builder setTitleCenter() {
            for (int i = 0, count = mToolbar.getChildCount(); i < count; i++) {
                View view = mToolbar.getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    if (mTitle.contentEquals(textView.getText())) {
                        textView.setGravity(Gravity.CENTER);
                        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER;
                        textView.setLayoutParams(params);
                        break;
                    }
                }
            }
            return this;
        }

        /**
         * Title居中，通过添加一个TextView，需要传入StyleId
         */
        public Builder setTitleCenter(@StyleRes int styleId) {
            mToolbar.setTitle(null);
            TextView titleText = new TextView(mActivity);
            titleText.setMaxLines(1);
            titleText.setText(mTitle);
            titleText.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                titleText.setTextAppearance(styleId);
            } else {
                titleText.setTextAppearance(mActivity, styleId);
            }
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            titleText.setLayoutParams(layoutParams);
            mToolbar.addView(titleText);
            return this;
        }

        public void build() {
            // do something
        }

    }

    /**
     * 设置title居中,
     * @param toolbar Toolbar
     * @param title title内容
     */
    public static void setTitleCenter(Toolbar toolbar, String title) {
        for (int i = 0, count = toolbar.getChildCount(); i < count; i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (title.contentEquals(textView.getText())) {
                    textView.setGravity(Gravity.CENTER);
                    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER;
                    textView.setLayoutParams(params);
                    break;
                }
            }
        }
    }
}
