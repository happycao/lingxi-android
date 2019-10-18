package me.cl.lingxi.view.textview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;
/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/27
 * desc   : 可点击ImageSpan
 * version: 1.0
 */
public abstract class ClickableImageSpan extends ImageSpan {

    public ClickableImageSpan(Drawable b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public abstract void onClick(View view);

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = bottom - b.getBounds().bottom;
        transY -= paint.getFontMetricsInt().descent / 2;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}