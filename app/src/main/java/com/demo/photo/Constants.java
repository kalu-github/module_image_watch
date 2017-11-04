package com.demo.photo;

/**
 * description 常量
 * created by kalu on 2016/10/23 13:11
 */
public class Constants {
    // debug
    public static final boolean IS_DEBUG = true;
    // 正式环境地址
    private static final String API_BASE_URL_ONLINE = "https://api.icaibei.net";
    // 测试环境地址
    private static final String API_BASE_URL_TEST = "http://test.api.icaibei.net";

    public static String getApiBaseUrl() {
        if (IS_DEBUG) {
            return API_BASE_URL_TEST;
        } else {
            return API_BASE_URL_ONLINE;
        }
    }

    // http 接口变量
    /**
     * 需要缓存
     */
    protected static final String HttpCached = "xCached: 1";

    /**
     * 需要登录公共参数
     */
    protected static final String HttpLogin = "xLogin: 1";

    // 主播身份
    public static final int HOST = 2;
    // 游客身份
    public static final int MEMBER = 1;

    // 常量
    public static final int TIMELINE_ONLY_TEXT = 0;
    public static final int TIMELINE_TEXT_AND_IMAGE = 1;
    public static final int TIMELINE_TEXT_AND_REVIEW = 2;
    public static final int TIMELINE_TEXT_AND_LIVE = 3;

    public static final String LIVE_DETAIL_DELETE_TID = "live_detail_delete_tid";

    public static final String N = "\n";
    public static final String NULL = "";
    public static final String UNDERLINE = "_";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String PERCENT = "%";
    public static final String STOCK_CODE = "stock_code";
    public static final String TARGET = "Android";
    // cache
    private static final String CACHE_ROOT = "/photoview/";
    public static final String CACHE_IMAGE = CACHE_ROOT+"image/";
    public static final String CACHE_HTTP = CACHE_ROOT+"net/";
    public static final String CACHE_APK = CACHE_ROOT+"apk/";
    public static final String CACHE_USER = CACHE_ROOT+"user/";
    // Handler what
    public static final int HANDLER_WHAT_LIVE_MARQUEE_SCROLL = 10003;
    public static final int HANDLER_WHAT_LIVE_MARQUEE_REFRESH = 10004;
    // Intent 返回值
    public static final String INTENT_DATA = "intent_data";
    public static final String INTENT_DATA_LOGOUT = "logout";
    public static final String INTENT_DATA_MAIN = "main";
    public static final String INTENT_DATA_ROOM_ID = "roomId";
    public static final String INTENT_DATA_USER_ID = "user_id";
    public static final String INTENT_DATA_USER_TYPE = "user_type";
    public static final String INTENT_DATA_AD_URL = "resultURL";
    public static final String INTENT_DATA_PUSH_ID = "pushId";
    public static final String INTENT_DATA_PUSH_DATA = "pushData";
    public static final int INTENT_RESULT_SEARCH_ADD_MARQUEE = 60001; // 直播 - 点击搜索 - 添加自选股 - 跑马灯数据更新
    public static final int INTENT_RESULT_SEARCH_MINI_CHAT = 60002; // 直播 - 点击搜索 - 点击股票 - 显示行情图
    public static final int INTENT_RESULT_SEARCH_ADD_INPUT = 60003; // 直播 - 点击$ - 点击股票 - Edittext添加股票标签

    public static final int LIVE_TIMELINE_DETAIL_RESULT_CODE = 6010; //主播删除详情页
    public static final int LIVE_DETAIL_REQUEST_CODE = 6011;  //直播tab点击跳转详情页

    public static final String SHARE_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //Broadcast
    public static final String BROADCAST_LOGOUT = "broadcast_logout";

    public static final String USER_SP = "userSp";
    public static final String LIVE_SP = "liveSp";
    public static final String SP_ACCOUNT_PHONE = "phone";
    public static final String SP_USER_ID = "userId";
    public static final String SP_USER_COOKIE = "cookie";
    public static final String SP_ROOM_ID = "roomid";
    public static final String SP_USER_TOKEN = "token";
    public static final String SP_SDK_APPID = "sdkAppId";
    public static final String SP_ACCOUNT_TYPE = "accountType";
    public static final String SP_USER_SIG = "userSig";
    public static final String SP_EXPIRE_DATE = "expireDate";
    public static final String SP_USER_TYPE = "userType";
    public static final String SP_USER_NICK = "nick";
    public static final String SP_USER_BALANCE = "balance";
    public static final String SP_USER_HEAD_URL = "headUrl";
    public static final String SP_LIVE_STATUS = "liveStatus";
    public static final String SP_LIVE_TITLE = "sp_live_title";
    public static final String SP_LIVE_INCOME = "sp_live_income";
    public static final String SP_LIVE_TIME = "sp_live_time";
    public static final String SP_LIVE_DESCRIBE = "sp_live_describe";
    public static final String SP_LIVE_ID = "sp_live_id";
    public static final String SP_LIVE_COVER = "sp_live_cover";
    public static final String SP_LIVE_HOST_ID = "sp_live_host_id";
    public static final String SP_LIVE_ROOM_ID = "sp_live_room_id";
    public static final String SP_LIVE_REQUEST_NUM = "sp_live_request_num";
    public static final String SP_LIVE_HOST_NAME = "sp_live_host_name";
    public static final String SP_LIVE_HOST_HEAD = "sp_live_host_head";
    public static final String SP_LIVE_COUNT = "sp_live_count";
    public static final String SP_LIVE_FOLLOW = "sp_live_follow";
    public static final String SP_LIVE_FORBID = "sp_live_forbid";
    public static final String SP_LIVE_VIDEO_URL = "sp_live_video_url";
    public static final String SP_INPUT_SEARCH = "sp_input_search";

    public static final String IM_MSG_TYPE = "msg_type";
    public static final String IM_MSG_TXT = "msg_txt";                  // 消息内容
    public static final String IM_MSG_FROM_USER = "msg_from_user";      // 消息发送者昵称
    public static final int ALAPTIMEHIDESHOW = 700;
    public static final String CMD_FROM_USER = "fromUser";
    public static final String CMD_NUM = "num";
    public static final String CMD_TO_USER = "toUser";
    public static final String CMD_GIFT_ID = "giftId";
    public static final String CMD_ROOM_ID = "roomId";
    public static final String CMD_LIVE_ID = "liveId";

    /******************************************
     * 我的模块
     *******************************************************/
    public static final String RISKTIPSURL = "https://m.icaibei.net/app/riskTips.html";
    public static final String USERAGREEMENTURL = "https://m.icaibei.net/app/userAgreement.html";
    public static final String HELPFEEDBACKURL = "https://m.icaibei.net/app/help.html";
    public static final String H5_ANCHOR_AGREEMENT = "https://m.icaibei.net/app/anchorAgreement.html";

    public static final String WXPAY = "weixin";
    public static final String ALIPAY = "alipay";

    //推送开关
    public static final int PUSH_SWITCH_ANCHOR = 0; //主播
    public static final int PUSH_SWITCH_REPLY = 1;  //回复/评论
    public static final int PUSH_SWITCH_NEWFLASH = 2;  //彩贝快报

    public static final int PUSH_SWITCH_OPEN = 1;  //打开推送
    public static final int PUSH_SWITCH_CLOSED = 0;  //关闭推送

}