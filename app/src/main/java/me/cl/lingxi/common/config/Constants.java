package me.cl.lingxi.common.config;

/**
 * 常量
 */
public class Constants {

    // 回复已读
    public static boolean isRead = true;

    // action
    public static final String UPDATE_USER_IMG = "me.cl.update.img";

    // 与我相关&我的回复
    public static final String REPLY_TYPE = "reply_type";
    public static final String REPLY_MY = "reply_my";
    public static final String REPLY_RELEVANT = "reply_relevant";

    // 本地缓存key
    public static final String SP_USER_ID = "user_id";
    public static final String SP_USER_NAME = "user_name";
    public static final String SP_BEEN_LOGIN = "been_login";
    public static final String SP_UPDATE_FLAG = "update_flag";
    public static final String SP_FUTURE_INFO = "sp_future_info";

    // 参数传递
    public static final String PASSED_UNREAD_NUM = "unread_num";
    public static final String PASSED_USER_NAME = "user_name";
    public static final String PASSED_USER_INFO = "user_info";

    // 页面标识
    public static final int ACTIVITY_MAIN = 10001;
    public static final int ACTIVITY_PUBLISH = 10002;
    public static final int ACTIVITY_MOOD = 10003;
    public static final int ACTIVITY_PERSONAL = 10004;

    // 回退标识
    public static final String GO_INDEX = "go_index";

    // 服务器rss图片
    public static final String IMG_URL = Api.rssUrl;

}
