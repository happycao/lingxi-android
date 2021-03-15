package me.cl.lingxi.common.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.cl.library.util.ColorPhrase;
import me.cl.lingxi.entity.Like;

/**
 * 工具类
 * Created by bafsj on 17/3/1.
 */
public class Utils {

    /**
     * dp2px
     */
    public static int dp2px(float dpValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dp(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 屏幕宽度
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * 手机验证
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[34578]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 邮箱验证
     */
    public static boolean isMail(String mail) {
        String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(mail);
        return m.matches();
    }

    // 正则表达式,一定要和服务器以及 iOS 端统一
    private static final String TOPIC = "#([^#]+?)#";
    private static final String AT = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26} ";
    private static final String ALL = "(" + AT + ")" + "|" + "(" + TOPIC + ")";

    /**
     * 查询艾特和话题事件
     */
    public static ArrayList<String> findAction(String s) {
        Pattern p = Pattern.compile(ALL);
        Matcher m = p.matcher(s);

        ArrayList<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    /**
     * 隐藏虚拟键盘
     */
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    /**
     * 获取点赞人字符串
     */
    public static String getLikeStr(List<Like> likes) {
        StringBuilder likeStr = new StringBuilder();
        for (int i = 0, size = likes.size(); i < size; i++) {
            if (i == 3) break;
            likeStr.append("{").append(likes.get(i).getUsername()).append("、}");
        }
        if (likes.size() > 0)
            likeStr = new StringBuilder(likeStr.substring(0, likeStr.length() - 2) + "}");
        return likeStr.toString();
    }

    /**
     * 获取所有点赞人字符串
     */
    public static String getLongLikeStr(List<Like> likes) {
        StringBuilder likeStr = new StringBuilder();
        for (int i = 0; i < likes.size(); i++) {
            if (i == likes.size() - 1) {
                likeStr.append("{").append(likes.get(i).getUsername()).append("}");
            } else {
                likeStr.append("{").append(likes.get(i).getUsername()).append("、}");
            }
        }
        return likeStr.toString();
    }

    /**
     * 突出颜色
     */
    public static CharSequence colorFormat(String str) {
        return ColorPhrase.from(str).withSeparator("{}").innerColor(0xFF4FC1E9).outerColor(0xFF666666).format();
    }

    /**
     * 获取Manifest中meta值
     */
    public static String getMetaValue(Context context, String metaKey) {
        if (context == null || TextUtils.isEmpty(metaKey)) {
            return null;
        }
        String metaValue = null;
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = null;
            if (null != app) {
                metaData = app.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaValue;
    }

    // 获取版本名称
    public static String getAppVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取版本号
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // qq临时会话，仅用于开通了qq推广的用户
    public static boolean wpaQQ(Context context, String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + key));
        if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(intent);
            return true;
        } else {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 发起添加群流程。群号：大龄儿童二次元同好群(468620613) 的 key 为： U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9
     * 调用 joinQQGroup(U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9) 即可发起手Q客户端申请加群 大龄儿童二次元同好群(468620613)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(intent);
            return true;
        } else {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 获取手机IMEI号((International Mobile Equipment Identity,国际移动身份识别码)
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            deviceId = telephonyManager.getDeviceId();
        }
        //android 10以上已经获取不了imei了 用 android id代替
        if(TextUtils.isEmpty(deviceId)){
            deviceId = Settings.System.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return  deviceId;
    }

    public static FragmentTransaction fragmentTransaction(FragmentManager fragmentManager, String tag) {
        // 清除已经存在的，同样的fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.remove(fragment);
        }
        transaction.addToBackStack(null);
        return transaction;
    }
}
