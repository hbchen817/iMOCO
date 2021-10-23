package com.laffey.smart.contract;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 常量
 */
public class Constant {
    // 包名
    public static String PACKAGE_NAME = "com.laffey.smart";
    // 平台 plantForm
    public static String PLANT_FORM = "xxxxxx";
    // 测试数据开关
    public static boolean IS_TEST_DATA = true;

    // 测试环境APPKEY
    //public static final String APPKEY                               = "29162669";
    //public static final String APPSECRET                            = "26cb74538029f05eca1fe104e62af59c";
    // 正式环境APPKEY
    public static final String APPKEY                               = "32328272";
    public static final String APPSECRET                            = "bd4be36c5d3341bb1335f6b440bf6363";

    // 产品类型
    public static final int PRODUCT_TYPE_LIGHT                      = 0;
    public static final int PRODUCT_TYPE_ELECTRIC                   = 1;
    public static final int PRODUCT_TYPE_SAFE                       = 2;
    public static final int PRODUCT_TYPE_HOME                       = 3;
    public static final int PRODUCT_TYPE_SENSOR                     = 4;
    public static final int PRODUCT_TYPE_ENVIRONMENTAL              = 5;
    public static final int PRODUCT_TYPE_LIVING                     = 6;
    public static final int PRODUCT_TYPE_GATEWAY                    = 7;
    public static final int PRODUCT_TYPE_OUTHOR                     = 8;

    // 淘宝回调地址,用户协议,隐私政策
    public static final String TAOBAOREDIRECTURI                    = "http://www.laffey.com/laffeysmart";
    public static final String USER_PROTOCOL_URL                    = "http://www.rexense.cn/nd.jsp?id=71#_np=127_530";
    public static final String PRIVACY_POLICY_URL                   = "https://www.rexense.cn/cn/h-nd-135.html";

    //分页时每页的数量
    public static final int PAGE_SIZE                                = 20;

    // 定义设备类型常量
    public static final String NODETYPE_GATEWAY                     = "GATEWAY";
    public static final int DEVICETYPE_GATEWAY                      = 1;

    // 定义内容类型
    public static final int CONTENTTYPE_PROPERTY                    = 1;
    public static final int CONTENTTYPE_EVENT_ALERT                 = 2;
    public static final int CONTENTTYPE_EVENT_INFO                  = 3;

    // 定义设备连接状态
    public static final int ADD_STATUS_SUCCESS                      = 0;
    public static final int ADD_STATUS_OTHERBIND                    = 1;
    public static final int ADD_STATUS_FAIL                         = 2;
    public static final int ADD_STATUS_LOGOUT                       = 3;

    // 定义设备连接状态
    public static final int CONNECTION_STATUS_UNABLED               = 0;
    public static final int CONNECTION_STATUS_ONLINE                = 1;
    public static final int CONNECTION_STATUS_OFFLINE               = 3;
    public static final int CONNECTION_STATUS_PROHIBIT              = 8;

    // 定义请求码
    public static final int REQUESTCODE_CALLCHOICEACTIVITY          = 1;
    public static final int REQUESTCODE_CALLMOREACTIVITY            = 2;
    public static final int REQUESTCODE_CALLCHOICECONTENTACTIVITY   = 3;
    public static final int REQUESTCODE_CALLCHOICEWIFIACTIVITY      = 4;
    public static final int REQUESTCODE_CALLSETTIMEACTIVITY         = 5;

    // 定义结果码
    public static final int RESULTCODE_CALLCHOICEACTIVITY_TIME      = 1000;
    public static final int RESULTCODE_CALLCHOICEACTIVITY_ENABLE    = 1001;
    public static final int RESULTCODE_CALLMOREACTIVITYUNBIND       = 1002;
    public static final int RESULTCODE_CALLCHOICECONTENTACTIVITY    = 1003;
    public static final int RESULTCODE_CALLCHOICEWIFIACTIVITY       = 1004;
    public static final int RESULTCODE_CALLSETTIMEACTIVITY          = 1005;

    // 定义API PATH常量
    public static final String API_PATH_GETCONFIGPROCDUCTLIST       = "/thing/productInfo/getByAppKey";
    public static final String API_PATH_GETGUIDANCEINFORMATION      = "/awss/enrollee/guide/get";
    public static final String API_PATH_CREATEHOME                  = "/living/home/create";
    public static final String API_PATH_GETHOMELIST                 = "/living/home/query";
    public static final String API_PATH_GETHOMEROOMLIST             = "/living/home/room/query";
    public static final String API_PATH_GETHOMEDEVICELIST           = "/living/home/device/query";
    public static final String API_PATH_BINDDEVICE                  = "/awss/token/user/bind";
    public static final String API_PATH_PERMITJOINSUBDEVICE         = "/thing/gateway/permit";
    public static final String API_PATH_BINDSUBDEVICE               = "/awss/time/window/user/bind";
    public static final String API_PATH_UNBINDDEVICE                = "/uc/unbindAccountAndDev";
    public static final String API_PATH_GETTSLPROPERTY              = "/thing/properties/get";
    public static final String API_PATH_GETTSLPROPERTYTIMELINEDATA  = "/living/device/property/timeline/get";
    public static final String API_PATH_GETTSLEVENTTIMELINEDATA     = "/living/device/event/timeline/get";
    public static final String API_PATH_SETTSLPROPERTY              = "/thing/properties/set";
    public static final String API_PATH_GETGATEWAYSUBDEVICELIST     = "/subdevices/list";
    public static final String API_PATH_GETUSERDEVICELIST           = "/uc/listBindingByAccount";
    public static final String API_PATH_SETDEVICENICKNAME           = "/uc/setDeviceNickName";
    public static final String API_PATH_UPDATEDEVICEROOM            = "/living/home/room/device/full/update";
    public static final String API_PATH_GETOTAFIRMWAREINFO          = "/thing/ota/info/queryByUser";
    public static final String API_PATH_GETTHINGBASEINFORMATION     = "/thing/info/get";
    public static final String API_PATH_CREATESCENE                 = "/living/scene/create";
    public static final String API_PATH_UPDATESCENE                 = "/living/scene/update";
    public static final String API_PATH_QUERYSCENELIST              = "/living/scene/query";
    public static final String API_PATH_QUERYSCENEDETAIL            = "/living/scene/info/get";
    public static final String API_PATH_EXECUTESCENE                = "/scene/fire";
    public static final String API_PATH_DELETESCENE                 = "/living/scene/delete";
    public static final String API_PATH_UNREGISTER                  = "/account/unregister";
    public static final String API_PATH_MODIFYACCOUNT               = "/iotx/account/modifyAccount";
    public static final String API_PATH_MESSAGECENTER               = "/message/center/query/push/message";
    public static final String API_PATH_CONFIRMSHARE                = "/uc/confirmShare";
    public static final String API_PATH_GENERATEQRCODE              = "uc/generateShareQrCode";
    public static final String API_PATH_SCANSHAREQRCODE             = "/uc/scanBindByShareQrCode";
    public static final String API_PATH_SHAREDEVICEORSCENE          = "/uc/shareDevicesAndScenes";
    public static final String API_PATH_SHARENOTICELIST             = "/uc/getShareNoticeList";
    public static final String API_PATH_CLEARSHARENOTICELIST        = "/uc/clearShareNoticeList";
    public static final String API_PATH_CLEARMESSAGERECORD          = "/message/center/record/delete";
    public static final String API_PATH_GETSCENELOG                 = "/scene/log/list/get";
    public static final String API_PATH_GETBINDTAOBAOACCOUNT        = "/account/thirdparty/get";
    public static final String API_PATH_BINDTAOBAO                  = "/account/taobao/bind";
    public static final String API_PATH_UNBINDTAOBAO                = "/account/thirdparty/unbind";
    public static final String API_PATH_GETDEVICEINROOM             = "/living/home/room/device/query";
    public static final String API_PATH_UPGRADEFIRMWARE             = "/thing/ota/batchUpgradeByUser";
    public static final String API_PATH_UPDATEROOM                  = "/living/home/room/update";
    //过滤设备发现接口
    public static final String API_PATH_FILTER                      = "/awss/enrollee/product/filter";
    //创建虚拟用户
    public static final String API_PATH_CREATE_USER                 = "/uc/virtual/user/create";
    //删除虚拟用户
    public static final String API_PATH_DELETE_USER                 = "/uc/virtual/user/delete";
    //查询账户下的虚拟用户列表
    public static final String API_PATH_QUERY_USER_IN_ACCOUNT       = "/uc/virtual/user/list";
    //更新用户
    public static final String API_PATH_UPDATE_USER                 = "/uc/virtual/user/update";
    //查询历史记录
    public static final String API_PATH_QUERY_HISTORY               = "/lock/event/history/query";
    //查询钥匙对应的用户
    public static final String API_PATH_KEY_USER_GET                = "/lock/key/virtual/user/get";
    //设置临时密码
    public static final String API_PATH_TEMPORARY_KEY               = "/thing/service/invoke";
    //过滤未绑定用户钥匙
    public static final String API_PATH_FILTER_UNBIND_KEY           = "/lock/key/user/unbind/filter";
    //钥匙与用户绑定
    public static final String API_PATH_KEY_USER_BIND               = "/lock/key/user/bind";
    //查询设备下虚拟用户列表
    public static final String API_PATH_QUERY_USER_IN_DEVICE        = "/lock/dev/virtual/user/get";
    //查询虚拟用户绑定的钥匙列表
    public static final String API_PATH_QUERY_KEY_BY_USER           = "/lock/key/user/bindlist/get";
    //删除设备的钥匙信息
    public static final String API_PATH_DELETE_KEY                  = "/lock/key/user/delete";
    //删除设备的钥匙信息
    public static final String API_PATH_USER_KEY_UNBIND             = "/lock/key/user/unbind";
    //查询用户和设备的关系
    public static final String API_PATH_GET_BY_ACCOUNT_AND_DEV      = "/uc/getByAccountAndDev";
    //获取设备上支持trigger/condition/action配置的功能与TSL定义
    public static final String API_IOTID_SCENE_ABILITY_TSL_LIST     = "/iotid/scene/ability/tsl/list";
    //获取设备上支持TCA配置的功能属性列表
    public static final String API_IOTID_SCENE_ABILITY_LIST         = "/iotid/scene/ability/list";
    //设置设备扩展信息
    public static final String API_EXTENDED_PROPERTY_SET            = "/thing/extended/property/set";
    //获取设备扩展信息
    public static final String API_EXTENDED_PROPERTY_GET            = "/thing/extended/property/get";
    // 获取支持TCA的设备列表
    public static final String API_QUERY_DEV_LIST_FOR_CA            = "/scene/thing/list";
    // 根据设备ID获取物的模板
    public static final String API_QUERY_DEV_TSL                    = "/thing/tsl/get";
    // 提交意见反馈
    public static final String API_FEEDBACK_ADD                     = "/feedback/add";
    // 删除设备扩展属性信息
    public static final String API_EXTENDED_PROPERTY_DEL            = "/thing/extended/property/delete";

    // 定义插件URL常量
    public static final String PLUGIN_URL_COUNDTIMER                ="link://router/cloudtime";
    public static final String PLUGIN_URL_SCENE                     ="link://router/scene";
    public static final String PLUGIN_URL_ADVICE                    ="link://router/feedback";

    // 定义API返加代码常量
    public static final int API_CODE_SUCCESS                        = 200;

    // 定义长连接MQTT主题
    public static final String TOPIC_PROPERTYNOTIFY                 = "/thing/properties";
    public static final String TOPIC_EVENTNOTIFY                    = "/thing/events";
    public static final String TOPIC_THINGEVENTNOTIFY               = "/_thing/event/notify";
    public static final String TOPIC_STATUSNOTIFY                   = "/thing/status";
    public static final String TOPIC_SUBDEVICEJOINNOTIFY            = "/thing/topo/add/status";
    public static final String TOPIC_OTAUPGRADENOTITY               = "/ota/device/forward";

    // 定义一般消息常量
    public static final String API_MESSAGE_SUCCESS                  = "success";
    public static final int MSG_FAILURECODE                         = 0;
    public static final int MSG_SUCCESSCODE                         = 1;
    public static final int MSG_POSTLOGINPORCESS                    = 2;
    public static final int MSG_DOWNLOADIMAGE                       = 3;
    public static final int MSG_PARSE_CONFIGNETWORKFRAME            = 4;
    public static final int MSG_DIALOG_TWOCHOICEONE                 = 5;

    // 定义API回调消息常量
    public static final int MSG_CALLBACK_APICOMMITFAIL              = 100;
    public static final int MSG_CALLBACK_APIRESPONSEERROR           = 101;
    public static final int MSG_CALLBACK_CREATEHOME                 = 102;
    public static final int MSG_CALLBACK_GETHOMELIST                = 103;
    public static final int MSG_CALLBACK_GETHOMEROOMLIST            = 104;
    public static final int MSG_CALLBACK_GETCONFIGPRODUCTLIST       = 105;
    public static final int MSG_CALLBACK_GETGUIDANCEINFOMATION      = 106;
    public static final int MSG_CALLBACK_GETHOMEDEVICELIST          = 107;
    public static final int MSG_CALLBACK_GETHOMEGATWAYLIST          = 108;
    public static final int MSG_CALLBACK_BINDEVICE                  = 109;
    public static final int MSG_CALLBACK_UNBINDEVICE                = 110;
    public static final int MSG_CALLBACK_PERMITJOINSUBDEVICE        = 111;
    public static final int MSG_CALLBACK_BINDSUBDEVICE              = 112;
    public static final int MSG_CALLBACK_GETTSLPROPERTY             = 113;
    public static final int MSG_CALLBACK_GETTSLPROPERTYTIMELINEDATA = 114;
    public static final int MSG_CALLBACK_GETTSLEVENTTIMELINEDATA    = 115;
    public static final int MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST    = 116;
    public static final int MSG_CALLBACK_GETUSERDEVICTLIST          = 117;
    public static final int MSG_CALLBACK_SETDEVICENICKNAME          = 118;
    public static final int MSG_CALLBACK_UPDATEDEVICEROOM           = 119;
    public static final int MSG_CALLBACK_GETOTAFIRMWAREINFO         = 120;
    public static final int MSG_CALLBACK_GETTHINGBASEINFO           = 121;
    public static final int MSG_CALLBACK_CREATESCENE                = 122;
    public static final int MSG_CALLBACK_QUERYSCENELIST             = 123;
    public static final int MSG_CALLBACK_UNREGISTER                 = 124;
    public static final int MSG_CALLBACK_MODIFYACCOUNT              = 124;
    public static final int MSG_CALLBACK_EXECUTESCENE               = 125;
    public static final int MSG_CALLBACK_DELETESCENE                = 126;
    public static final int MSG_CALLBACK_MSGCENTER                  = 127;
    public static final int MSG_CALLBACK_CONFIRMSHARE               = 128;
    public static final int MSG_CALLBACK_SHAREQRCODE                = 129;
    public static final int MSG_CALLBACK_SCANSHAREQRCODE            = 130;
    public static final int MSG_CALLBACK_SHAREDEVICEORSCENE         = 131;
    public static final int MSG_CALLBACK_SHARENOTICELIST            = 132;
    public static final int MSG_CALLBACK_QUERYSCENEDETAIL           = 133;
    public static final int MSG_CALLBACK_UPDATESCENE                = 134;
    public static final int MSG_CALLBACK_CLEARSHARENOTICELIST       = 135;
    public static final int MSG_CALLBACK_CLEARMESSAGERECORD         = 136;
    public static final int MSG_CALLBACK_GETSCENELOG                = 137;
    public static final int MSG_CALLBACK_GETBINDTAOBAOACCOUNT       = 138;
    public static final int MSG_CALLBACK_BINDTAOBAO                 = 139;
    public static final int MSG_CALLBACK_UNBINDTAOBAO               = 140;
    public static final int MSG_CALLBACK_GETDEVICEINROOM            = 141;
    public static final int MSG_CALLBACK_UPGRADEFIRMWARE            = 142;
    public static final int MSG_CALLBACK_UPDATEROOM                 = 143;
    public static final int MSG_CALLBACK_FILTER_DEVICE              = 144;
    public static final int MSG_CALLBACK_CREATE_USER                = 145;
    public static final int MSG_CALLBACK_QUERY_USER_IN_ACCOUNT      = 146;
    public static final int MSG_CALLBACK_UPDATE_USER                = 147;
    public static final int MSG_CALLBACK_QUERY_HISTORY              = 148;
    public static final int MSG_CALLBACK_FILTER_UNBIND_KEY          = 149;
    public static final int MSG_CALLBACK_KEY_USER_BIND              = 150;
    public static final int MSG_CALLBACK_QUERY_USER_IN_DEVICE       = 151;
    public static final int MSG_CALLBACK_QUERY_KEY_BY_USER          = 152;
    public static final int MSG_CALLBACK_DELETE_KEY                 = 153;
    public static final int MSG_CALLBACK_KEY_USER_GET               = 154;
    public static final int MSG_CALLBACK_KEY_USER_UNBIND            = 155;
    public static final int MSG_CALLBACK_GET_BY_ACCOUNT_AND_DEV     = 156;
    public static final int MSG_CALLBACK_SCENE_ABILITY_TSL          = 157;
    public static final int MSG_CALLBACK_SCENE_ABILITY_TSL_TRIGGER  = 158;
    public static final int MSG_CALLBACK_EXTENDED_PROPERTY_SET      = 159;
    public static final int MSG_CALLBACK_EXTENDED_PROPERTY_GET      = 160;
    public static final int MSG_CALLBACK_CREATE_SWITCH_AUTO_SCENE   = 161;
    public static final int MSG_CALLBACK_TEMPORARY_KEY              = 162;
    public static final int MSG_CALLBACK_QUERY_DEV_LIST_FOR_CA      = 163;
    public static final int MSG_CALLBACK_IDENTIFIER_LIST            = 164;
    public static final int MSG_CALLBACK_TSL_LIST                   = 165;
    public static final int MSG_CALLBACK_DEV_TSL                    = 166;
    public static final int MSG_CALLBACK_UPDATE_SCENE               = 167;
	public static final int MSG_CALLBACK_DELETE_USER                = 168;
    public static final int MSG_CALLBACK_SUBMIT_FEEDBACK            = 169;
    public static final int MSG_CALLBACK_EXTENDED_PROPERTY_DEL      = 170;

    // 定义长连接回调消息常量
    public static final int MSG_CALLBACK_LNCONNECTSTATENOTIFY       = 200;
    public static final int MSG_CALLBACK_LNPROPERTYNOTIFY           = 201;
    public static final int MSG_CALLBACK_LNEVENTNOTIFY              = 202;
    public static final int MSG_CALLBACK_LNTHINGEVENTNOTIFY         = 203;
    public static final int MSG_CALLBACK_LNSTATUSNOTIFY             = 204;
    public static final int MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY      = 205;
    public static final int MSG_CALLBACK_LNOTAUPGRADENOTIFY         = 206;

    // 定义配网进度消息常量
    public static final int MSG_CONFIGNETWORK_STEP_START            = 300;
    public static final int MSG_CONFIGNETWORK_STEP1_SSIDACK         = 301;
    public static final int MSG_CONFIGNETWORK_STEP2_PWDACK          = 302;
    public static final int MSG_CONFIGNETWORK_STEP3_DN              = 303;
    public static final int MSG_CONFIGNETWORK_STEP4_TOKEN           = 304;
    public static final int MSG_CONFIGNETWORK_STEP5_BIND            = 305;
    public static final int MSG_CONFIGNETWORK_STEP_END              = 306;
    public static final int MSG_CONFIGNETWORK_REMAIN_SECOND         = 307;
    public static final int MSG_CONFIGNETWORK_FAILURE               = 308;
    public static final int MSG_CONFIGNETWORK_TIMEOUT               = 310;

    // 定义允许入网进度消息常量
    public static final int MSG_PERMITJOIN_STEP_START               = 400;
    public static final int MSG_PERMITJOIN_STEP_END                 = 406;
    public static final int MSG_PERMITJOIN_REMAIN_SECOND            = 407;
    public static final int MSG_PERMITJOIN_TIMEOUT                  = 410;

    // 定义本地服务请求消息常量
    public static final int MSG_QUEST_QUERY_SCENE_LIST              = 500;
    public static final int MSG_QUEST_QUERY_SCENE_LIST_ERROR        = 501;
    public static final int MSG_QUEST_ADD_SCENE                     = 502;
    public static final int MSG_QUEST_ADD_SCENE_ERROR               = 503;
    public static final int MSG_QUEST_UPDATE_SCENE                  = 504;
    public static final int MSG_QUEST_UPDATE_SCENE_ERROR            = 505;
    public static final int MSG_QUEST_GW_ID_BY_SUB_ID               = 506;
    public static final int MSG_QUEST_GW_ID_BY_SUB_ID_ERROR         = 507;
    public static final int MSG_QUEST_DELETE_SCENE                  = 508;
    public static final int MSG_QUEST_DELETE_SCENE_ERROR            = 509;
    public static final int MSG_QUEST_QUERY_MAC_BY_IOT_ID           = 510;
    public static final int MSG_QUEST_QUERY_MAC_BY_IOT_ID_ERROR     = 511;

    // intent resultCode
    public static final int ADD_LOCAL_SCENE                         = 10001;
    public static final int ADD_LOCAL_SCENE_FOR_SCENE_MODEL         = 10002;
    public static final int DEL_SCENE_IN_LOCALSCENEACTIVITY         = 10003;
    public static final int RESULT_CODE_UPDATE_SCENE                = 10004;

    // 定义配网常量
    public static final String CONFIGNETWORK_AES_CBC_IV             = "3333333333333333";
    public static final String CONFIGNETWORK_AES_CBC_YZ             = "8888888888888888";
    public static final int CONFIGNETWORK_FRAME_MINSIZE             = 7;
    public static final int CONFIGNETWORK_FRAME_HEADER              = 0x5505;
    public static final int CONFIGNETWORK_FRAME_NONACK              = 0xFF;
    public static final int CONFIGNETWORK_FRAME_FOOTER              = 0x33;
    public static final int CONFIGNETWORK_BUFFER_MAXSIZE            = 256;
    public static final int CONFIGNETWORK_ACK_SUCCESS               = 0;
    public static final int CONFIGNETWORK_CMD_NULL                  = 0;
    public static final int CONFIGNETWORK_CMD_SENDSSID              = 1;
    public static final int CONFIGNETWORK_CMD_SENDPASSWORD          = 2;
    public static final int CONFIGNETWORK_CMD_SENDYZ                = 3;
    public static final int CONFIGNETWORK_CMD_RECEIVEDN             = 4;
    public static final int CONFIGNETWORK_CMD_RECEIVETOKEN          = 5;
    public static final int CONFIGNETWORK_CMD_RECEIVESTATUS         = 6;
    public static final int CONFIGNETWORK_CMD_READTOKEN             = 7;

    // 定义配网发送数据状态机
    public static final int CONFIGNETWORK_SEND_STATUSMACHINE_0      = 0;
    public static final int CONFIGNETWORK_SEND_STATUSMACHINE_1      = 1;
    public static final int CONFIGNETWORK_SEND_STATUSMACHINE_2      = 2;

    public static final String TIMER_LINUX                          = "linux";
    public static final String TIMER_QUARTZ_CRON                    = "quartz_cron";
    public static final String TIMER_ZONE_ID                        = "Asia/Shanghai";

    public static final int SCENE_CONDITION_TIMER_EDIT              = 100;
    public static final int SCENE_CONDITION_TIME_RANGE_EDIT         = 101;

    public static final String SCENE_CONDITION_TIMER                = "condition/timer";
    public static final String SCENE_CONDITION_TIME_RANGE           = "condition/timeRange";
    public static final String SCENE_CONDITION_PROPERTY             = "condition/device/property";
    public static final String SCENE_CONDITION_EVENT                = "condition/device/event";
    public static final String SCENE_ACTION_SEND                    = "action/mq/send";
    public static final String SCENE_ACTION_TRIGGER                 = "action/scene/trigger";
    public static final String SCENE_ACTION_PROPERTY                = "action/device/setProperty";
    public static final String SCENE_ACTION_SERVICE                 = "action/device/invokeService";

    public static final String ICON_FONT_TTF                        = "iconfont/jk/iconfont.ttf";

    public static final String TAG_DEV_KEY_NICKNAME                 = "devKeyNickName";
    public static final String TAG_GATEWAY_FOR_DEV                  = "gatewayForDev";

    public static final String KEY_NICK_NAME_PK                     = CTSL.PK_SIX_TWO_SCENE_SWITCH + " , "
            + CTSL.PK_ONEWAYSWITCH + " , " + CTSL.PK_SIX_SCENE_SWITCH + " , " + CTSL.PK_THREE_KEY_SWITCH + " , " + CTSL.PK_TWOWAYSWITCH + " , "
            + CTSL.PK_FOUR_SCENE_SWITCH + " , " + CTSL.PK_FOURWAYSWITCH_2 + " , " + CTSL.PK_ONE_SCENE_SWITCH + " , " + CTSL.PK_THREE_SCENE_SWITCH + " , "
            + CTSL.PK_TWO_SCENE_SWITCH + " , " + CTSL.TEST_PK_ONEWAYWINDOWCURTAINS + " , " + CTSL.TEST_PK_TWOWAYWINDOWCURTAINS + " , " + CTSL.PK_ANY_TWO_SCENE_SWITCH
            + " , " + CTSL.PK_ANY_FOUR_SCENE_SWITCH + " , " + CTSL.PK_SIX_SCENE_SWITCH_YQSXB + " , " + CTSL.PK_SYT_ONE_SCENE_SWITCH + " , "
            + CTSL.PK_SYT_TWO_SCENE_SWITCH + " , " + CTSL.PK_SYT_THREE_SCENE_SWITCH + " , " + CTSL.PK_SYT_FOUR_SCENE_SWITCH + " , " + CTSL.PK_SYT_SIX_SCENE_SWITCH;

    // 可作为本地场景条件的设备类型
    public static final String CONDITION_DEVS_PK                    = CTSL.PK_ONE_SCENE_SWITCH + " , " + CTSL.PK_TWO_SCENE_SWITCH + " , "
            + CTSL.PK_THREE_SCENE_SWITCH + " , " + CTSL.PK_FOUR_SCENE_SWITCH + " , " + CTSL.PK_SIX_SCENE_SWITCH + " , " + CTSL.TEST_PK_ONEWAYWINDOWCURTAINS + " , "
            + CTSL.TEST_PK_TWOWAYWINDOWCURTAINS + " , " + CTSL.PK_ONEWAYSWITCH + " , " + CTSL.PK_TWOWAYSWITCH + " , " + CTSL.PK_THREE_KEY_SWITCH + " , " + CTSL.PK_FOURWAYSWITCH_2
            + " , " + CTSL.PK_SIX_TWO_SCENE_SWITCH + " , " + CTSL.PK_OUTLET + " , " + CTSL.PK_AIRCOMDITION_TWO + " , " + CTSL.PK_AIRCOMDITION_FOUR + " , " + CTSL.PK_VRV_AC
            + " , " + CTSL.PK_FLOORHEATING001 + " , " + CTSL.PK_FAU + " , " + CTSL.PK_LIGHT + " , " + CTSL.PK_ONE_WAY_DIMMABLE_LIGHT + " , " + CTSL.PK_PIRSENSOR
            + " , " + CTSL.PK_GASSENSOR + " , " + CTSL.PK_TEMHUMSENSOR + " , " + CTSL.PK_SMOKESENSOR + " , " + CTSL.PK_WATERSENSOR + " , " + CTSL.PK_DOORSENSOR
            + " , " + CTSL.PK_MULTI_THREE_IN_ONE + " , " + CTSL.PK_MULTI_AC_AND_FH + " , " + CTSL.PK_MULTI_AC_AND_FA + " , " + CTSL.PK_MULTI_FH_AND_FA + " , " + CTSL.PK_SYT_ONE_SCENE_SWITCH
            + " , " + CTSL.PK_SYT_TWO_SCENE_SWITCH + " , " + CTSL.PK_SYT_THREE_SCENE_SWITCH + " , " + CTSL.PK_SYT_FOUR_SCENE_SWITCH + " , " + CTSL.PK_SYT_SIX_SCENE_SWITCH;
    // 可作为本地场景动作的设备类型
    public static final String ACTION_DEVS_PK                       = CTSL.TEST_PK_ONEWAYWINDOWCURTAINS + " , "
            + CTSL.TEST_PK_TWOWAYWINDOWCURTAINS + " , " + CTSL.PK_ONEWAYSWITCH + " , " + CTSL.PK_TWOWAYSWITCH + " , " + CTSL.PK_THREE_KEY_SWITCH + " , " + CTSL.PK_FOURWAYSWITCH_2
            + " , " + CTSL.PK_SIX_TWO_SCENE_SWITCH + " , " + CTSL.PK_OUTLET + " , " + CTSL.PK_AIRCOMDITION_TWO + " , " + CTSL.PK_AIRCOMDITION_FOUR + " , " + CTSL.PK_VRV_AC
            + " , " + CTSL.PK_FLOORHEATING001 + " , " + CTSL.PK_FAU + " , " + CTSL.PK_LIGHT + " , " + CTSL.PK_ONE_WAY_DIMMABLE_LIGHT
            + " , " + CTSL.PK_MULTI_THREE_IN_ONE + " , " + CTSL.PK_MULTI_AC_AND_FH + " , " + CTSL.PK_MULTI_AC_AND_FA + " , " + CTSL.PK_MULTI_FH_AND_FA;

    public static final String JD_APP_ID                            = "apidemo";
    public static final String JD_DEV_ID                            = "test_device1";
    public static final String JD_URL                               = "http://demo.jdshtech.com/open/m.php";

    public static final int REMOTE_CONTROL_TV                       = 2;// 电视
    public static final int REMOTE_CONTROL_AIR_CONDITIONER          = 7;// 空调
    public static final int REMOTE_CONTROL_FAN                      = 6;// 风扇

    // 查询本地场景列表
    public static final String QUERY_SCENE_LIST                     = "/app/scene/queryList";
    public static final String QUERY_SCENE_LIST_VER                 = "1.0";

    // 根据IotId查询Mac
    public static final String QUERY_MAC_BY_IOTID                   = "/app/device/queryMacByIotId";
    public static final String QUERY_MAC_BY_IOTID_VER               = "1.0";

    // 获取数据转换规则
    public static final String GET_DATA_CONVERSION_RULES            = "/gw/product/getDataConversionRules";
    public static final String GET_DATA_CONVERSION_RULES_VER        = "1.0";

    // 增加本地场景
    public static final String ADD_SCENE                            = "/app/scene/add";
    public static final String ADD_SCENE_VER                        = "1.0";

    // 删除场景
    public static final String DELETE_SCENE                         = "/app/scene/delete";
    public static final String DELETE_SCENE_VER                     = "1.0";

    // 修改场景
    public static final String UPDATE_SCENE                         = "/app/scene/update";
    public static final String UPDATE_SCENE_VER                     = "1.0";

    // 根据Mac查询IotId
    public static final String QUERY_IOT_ID_BY_MAC                  = "/app/device/queryIotIdByMac";
    public static final String QUERY_IOT_ID_BY_MAC_VER              = "1.0";

    // 根据子设备iotId查询网关iotId
    public static final String QUERY_GW_ID_BY_SUB_ID                = "/app/device/getGWIotIdBySubIotId";
    public static final String QUERY_GW_ID_BY_SUB_ID_VER            = "1.0";
}

