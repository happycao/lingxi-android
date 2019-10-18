package me.cl.library.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.cl.library.R;

/**
 * MoeToast
 * Created by bafsj on 16/1/21.
 */
public class MoeToast {

    /**
     * Toast字体大小
     */
    private static final float DEFAULT_TEXT_SIZE = 14;
    /**
     * Toast字体颜色
     */
    private static final int DEFAULT_TEXT_COLOR = 0xffffffff;
    /**
     * Toast背景图片
     */
    private static final int DEFAULT_BG_TEXT = R.drawable.img_toast_bc;
    /**
     * Toast位置高度
     */
    private static final int DEFAULT_TOAST_HIGHT = 80;

    private static Context mContext;
    private volatile static MoeToast mInstance;
    private static Toast mToast;
    private View layout;
    private TextView tv;

    private MoeToast(Context context) {
        mContext = context;
    }

    /**
     * 单例模式
     *
     * @param context 传入的上下文
     * @return TabToast实例
     */
    private static MoeToast getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MoeToast.class) {
                if (mInstance == null) {
                    mInstance = new MoeToast(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    /**
     * @param duration Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    private static void getToast(int duration) {
        if (mToast == null) {
            mToast = new Toast(mContext);
            mToast.setGravity(Gravity.BOTTOM, 0, dp2px(DEFAULT_TOAST_HIGHT));
            mToast.setDuration(duration == Toast.LENGTH_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        }
    }

    public static void makeText(Context context, String text) {
        makeText(context, text, Toast.LENGTH_SHORT);
    }

    public static void makeText(Context context, int resId) {
        makeText(context, context.getString(resId));
    }

    public static void makeText(Context context, int resId, int duration) {
        makeText(context, context.getString(resId), duration);
    }

    public static void makeText(Context context, String text, int duration) {
        getInstance(context);
        getToast(duration);
        if (mInstance.layout == null || mInstance.tv == null) {
            LinearLayout container = new LinearLayout(mContext);
            LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container.setLayoutParams(rootParams);
            container.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            container.setGravity(Gravity.CENTER);

            mInstance.tv = new TextView(mContext);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mInstance.tv.setLayoutParams(tvParams);
            mInstance.tv.setPadding(dp2px(10), dp2px(2), dp2px(10), dp2px(2));
            mInstance.tv.setGravity(Gravity.CENTER);
            mInstance.tv.setTextColor(DEFAULT_TEXT_COLOR);
            mInstance.tv.setMaxLines(2);
            mInstance.tv.setEllipsize(TextUtils.TruncateAt.END);
            mInstance.tv.setTextSize(DEFAULT_TEXT_SIZE);
            mInstance.tv.setBackgroundResource(DEFAULT_BG_TEXT);

            container.addView(mInstance.tv);
            mInstance.layout = container;
            mToast.setView(mInstance.layout);
        }
        mInstance.tv.setText(text);
        mToast.show();
    }

    /**
     * dp转px
     *
     * @param value dp
     * @return px
     */
    private static int dp2px(float value) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}
