package me.cl.library.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/07/24
 * desc   : Toast Util
 * version: 1.0
 */
public class ToastUtil {

    private static Toast toast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void showToast(final Context context, final String info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                toastShow(context, info, Toast.LENGTH_SHORT);
            }
        });
    }

    public static void showToast(final Context context, final int infoId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                toastShow(context, context.getResources().getString(infoId), Toast.LENGTH_SHORT);
            }
        });
    }

    @SuppressLint("ShowToast")
    private static void toastShow(Context context, String info, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, info, duration);
        } else {
            toast.setText(info);
        }
        toast.show();
    }
}
