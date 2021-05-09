package me.iwf.photopicker.utils;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class AndroidLifecycleUtils {
    public static boolean canLoadImage(Fragment fragment) {
        if (fragment == null) {
            return true;
        }

        FragmentActivity activity = fragment.getActivity();

        return canLoadImage(activity);
    }

    public static boolean canLoadImage(Context context) {
        if (context == null) {
            return true;
        }

        if (!(context instanceof Activity)) {
            return true;
        }

        Activity activity = (Activity) context;
        return canLoadImage(activity);
    }

    public static boolean canLoadImage(Activity activity) {
        if (activity == null) {
            return true;
        }

        boolean destroyed = activity.isDestroyed();

        return !destroyed && !activity.isFinishing();
    }
}
