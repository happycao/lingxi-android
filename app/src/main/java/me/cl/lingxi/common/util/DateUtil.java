package me.cl.lingxi.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间换算类
 */
public class DateUtil {

    /**
     * 显示时间，如果与当前时间差别小于一天，则自动用**秒(分，小时)前，
     * 如果大于一天则用format规定的格式显示
     *
     * @param date 时间
     * @return 处理得到的时间字符串
     */
    public static String showTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        Date cTime = null;
        try {
            cTime = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return showTime(cTime);
    }

    public static String showTime(Date data) {
        if (data == null) return "未知";
        long nowTimeLong = System.currentTimeMillis();
        long cTimeLong = data.getTime();
        long result = Math.abs(nowTimeLong - cTimeLong);

        if (result < 60000) {
            long seconds = result / 1000;
            if (seconds == 0) {
                return "刚刚";
            } else {
                return seconds + "秒前";
            }
        }
        if (result < 3600000) {
            long seconds = result / 60000;
            return seconds + "分钟前";
        }
        if (result < 86400000) {
            long seconds = result / 3600000;
            return seconds + "小时前";
        }
        if (result < 1702967296) {
            long seconds = result / 86400000;
            return seconds + "天前";
        }

        // 跨年
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.SIMPLIFIED_CHINESE);
        long nowYearLong = 0;
        try {
            nowYearLong = sdf.parse(sdf.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        if (nowYearLong < cTimeLong){
            sdf = new SimpleDateFormat("MM-dd hh:mm", Locale.SIMPLIFIED_CHINESE);
        }
        return sdf.format(data);
    }
}
