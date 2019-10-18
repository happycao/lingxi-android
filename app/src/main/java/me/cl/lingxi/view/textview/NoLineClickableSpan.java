package me.cl.lingxi.view.textview;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import me.cl.lingxi.R;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/27
 * desc   : ClickableSpan去下划线
 * version: 1.0
 */
public class NoLineClickableSpan extends ClickableSpan {

    private Context mContext;

    public NoLineClickableSpan(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View widget) {
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        // 文本颜色
        ds.setColor(mContext.getResources().getColor(R.color.blue));
        // 去掉超链接下划线
        ds.setUnderlineText(false);
    }
}